package net.mmly.openminemap.map;

import com.google.gson.Gson;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.search.SearchBoxLayer;
import net.mmly.openminemap.search.SearchResult;
import net.mmly.openminemap.search.SearchResultType;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.Notification;
import net.mmly.openminemap.util.TileUrlFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class Requester extends Thread {

    public static boolean disableWebRequests = ConfigOptions.__DISABLE_WEB_REQUESTS.getAsBoolean(); //development variable for disabling web requests. If disabled, tiles will ony be loaded from the cache or as error tiles
    int requestAttempts = 2; //how many times a tile will be requested before it is determined to not request it anymore
    private final String[] subDomains = new String[]{"a", "b", "c"};
    private final String subDomain = subDomains[new Random().nextInt(3)];

    ArrayList<String> failedRequests = new ArrayList<>();

    int requestCounter = 0;

    public void run() {
        if (disableWebRequests) OpenMineMapClient.debugMessages.add("OpenMineMap: Web requests are disabled.");
        while (true) {
            if (RequestManager.searchString != null) {
                SearchResult[] results = searchResultRequest(RequestManager.searchString, RequestManager.searchPriorityLat, RequestManager.searchPriorityLon);
                if (results == null) {
                    RequestManager.searchResultReturn = getErrorResult();
                } else {
                    RequestManager.searchResultReturn = results;
                }
                RequestManager.searchString = null;
            }
            else if (!Double.isNaN(RequestManager.reverseSearchLat)) {
                doReverseSearch();
            }
            else if (RequestManager.needToLoadClaims()) {
                RequestManager.setClaims(getClaims());
            }
            else if (RequestManager.pendingRequest != null) {
                RequestableTile request = RequestManager.pendingRequest;
                this.tileGetRequest(request.x, request.y, request.zoom, TileUrlFile.getCurrentUrl().source_url, request.cacheName);
                if (!disableWebRequests) requestCounter++;
                if (requestCounter >= requestAttempts) {
                    requestCounter = 0;
                    failedRequests.add(TileManager.getKey(request.zoom, request.x, request.y));
                }
                //System.out.println("Tile request");
            }
            //System.out.println("Request loop");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private SearchResult[] getErrorResult() {
        return (new SearchResult[] {
                new SearchResult(
                        SearchResultType.LOCATION,
                        Double.NaN,
                        Double.NaN,
                        false,
                        "",
                        Text.translatable("omm.notification.something-wrong").getString(),
                        0
                ),
                null, null, null, null, null, null, null
        });
    }

    private InputStream getClaims() {
        return get("https://api.buildtheearth.net/api/v1/claims/geojson?active=true");
    }

    private SearchResult[] parseLocationJson(InputStream stream) {
        Gson gson = new Gson();
        SearchResult[] results = new SearchResult[SearchBoxLayer.MAX_SEARCH_RESULTS];
        Map returnedResult;

        try {
            returnedResult = gson.fromJson(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)), Map.class);
        } catch (NullPointerException e) {
            return getErrorResult();
        }
        if (!returnedResult.get("type").equals("FeatureCollection")) return null;

        ArrayList features = (ArrayList) returnedResult.get("features");

        for (int i = 0; i < features.size(); i++) {
            Map feature = (Map) (features.get(i));
            Map geometry = (Map) feature.get("geometry");
            Map properties = (Map) feature.get("properties");
            ArrayList coords = (ArrayList) geometry.get("coordinates");

            String context = "";
            //if (properties.get("county") != null) context += properties.get("county") + ", ";
            if (properties.get("city") != null) context += properties.get("city") + ", ";
            if (properties.get("state") != null) context += properties.get("state") + ", ";
            if (properties.get("country") != null) context += properties.get("country") + ", ";
            if (!context.isEmpty()) context = context.substring(0, context.length() - 2);

            ArrayList extentList = (ArrayList) properties.get("extent");
            double[] extent = null;
            if (extentList != null) extent = new double[] {
                    (double) extentList.get(1),
                    (double) extentList.get(3),
                    (double) extentList.get(0),
                    (double) extentList.get(2)
            };

            results[i] = new SearchResult(
                    SearchResultType.LOCATION,
                    (Double) coords.get(1),
                    (Double) coords.get(0),
                    false,
                    (String) properties.get("name"),
                    context,
                    extent
            );
        }

        return results;
    }

    private void doReverseSearch() {
        SearchResult result = reverseSearchRequest(RequestManager.reverseSearchLat, RequestManager.reverseSearchLong);
        RequestManager.resetReverseSearchCandidate();
        if (result == null) {
            MapScreen.addNotification(new Notification(Text.translatable("omm.notification.something-wrong")));
        } else {
            try {
                String location = "";
                if (result.name != null && MapScreen.map.getTileZoom() >= 14) location += result.name + ", ";
                if (result.context != null) location += result.context + ", ";
                if (!location.isEmpty()) location = location.substring(0, location.length() - 2);

                MinecraftClient.getInstance().keyboard.setClipboard(location);
                MapScreen.addNotification(new Notification(Text.translatable("omm.notification.location-copied")));
            } catch (HeadlessException e) {
                MapScreen.addNotification(new Notification(Text.translatable("omm.notification.something-wrong")));
            }
        }
    }

    SearchResult reverseSearchRequest(double lat, double lon) {
        if (disableWebRequests) return null;
        String urlPattern = "https://photon.komoot.io/reverse?lon=" + lon + "&lat=" + lat;

        InputStream stream = get(urlPattern);
        SearchResult[] results = parseLocationJson(stream);
        if (results == null) return null;
        if (results[0] == null) return null;
        if (Double.isNaN(results[0].latitude)) return null;

        return results[0];
    }

    SearchResult[] searchResultRequest(String query, double latFocus, double lonFocus) {
        if (disableWebRequests) return null;

        String urlPattern = "https://photon.komoot.io/api/?q=" + query.replaceAll("[^a-zA-Z0-9 ]", "").replaceAll(" ", "+") + "&limit=7";
        if (!OmmMap.geoCoordsOutOfBounds(latFocus, lonFocus)) {
            urlPattern += "&lat=" + latFocus + "&lon=" + lonFocus;
        }
        //System.out.println(urlPattern);

        InputStream stream = get(urlPattern);
        if (stream == null) return null;

        SearchResult[] results = parseLocationJson(stream);
        if (results == null) return null;

        if (results[0] == null) {
            results[0] = new SearchResult(
                SearchResultType.LOCATION,
                0,
                0,
                false,
                "",
                Text.translatable("omm.search.no-results").getString(),
                0
            );
        }

        return results;
    }

    void tileGetRequest(int x, int y, int zoom, String urlPattern, String cacheName) {
        BufferedImage image = null;
        if (disableWebRequests || TileManager.isTileOutOfBounds(x, y, zoom) || failedRequests.contains(TileManager.getKey(zoom, x, y))) return;

        urlPattern = ((urlPattern.replace("{z}", Integer.toString(zoom)).replace("{x}", Integer.toString(x))).replace("{y}", Integer.toString(y)).replace("{s}", subDomain));
        try {
            InputStream inputStream = get(urlPattern);
            if (inputStream == null) return;
            image = ImageIO.read(inputStream);
            File out = new File(TileManager.getRootFile() + "openminemap/"+cacheName+"/"+zoom+"/"+x+"-"+y+".png");
            ImageIO.write(image, "png", out);
            RequestManager.pendingRequest = null;
            requestCounter = 0;
        } catch (IOException e) {
            OpenMineMap.LOGGER.error("Error during tile write: " + e.getMessage());
        }
    }

    private InputStream get(String url) {
        try {
            URL url1 = new URI(url).toURL();
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();

            connection.setRequestProperty("User-Agent", "Java/21.0.8 OpenMineMap (contact: minemasterlegacy@gmail.com)");
            connection.setRequestProperty("cache-control", "max-age=7");
            connection.setUseCaches(true);
            connection.setRequestProperty("Retry-After", "3");

            connection.connect();
            if (connection.getResponseCode() != Math.clamp(connection.getResponseCode(), 200, 299)) {
                OpenMineMap.LOGGER.error("Error during url request: Code " + connection.getResponseCode() + " received.");
                return null;
            }
            return connection.getInputStream();
        } catch (Exception e) {
            OpenMineMap.LOGGER.error("Error during url request: " + e.getMessage());
            return null;
        }
    }

}
