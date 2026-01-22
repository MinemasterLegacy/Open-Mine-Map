package net.mmly.openminemap.map;

import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.search.SearchResult;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.TileUrlFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Random;

public class Requester extends Thread {

    boolean disableWebRequests = Boolean.parseBoolean(ConfigFile.readParameter(ConfigOptions.__DISABLE_WEB_REQUESTS)); //development variable for disabling web requests. If disabled, tiles will ony be loaded from the cache or as error tiles
    int requestAttempts = 2; //how many times a tile will be requested before it is determined to not request it anymore
    private final String[] subDomains = new String[]{"a", "b", "c"};
    private final String subDomain = subDomains[new Random().nextInt(3)];

    ArrayList<String> failedRequests = new ArrayList<>();

    int requestCounter = 0;

    public void run() {
        if (disableWebRequests) OpenMineMapClient.debugMessages.add("OpenMineMap: Web requests are disabled for this session.");
        while (true) {
            if (RequestManager.searchString != null) {
                //searchResultRequest();
            }
            else if (!Double.isNaN(RequestManager.reverseSearchLat)) {
                //TODO
            }
            else if (RequestManager.pendingRequest != null) {
                RequestableTile request = RequestManager.pendingRequest;
                this.tileGetRequest(request.x, request.y, request.zoom, TileUrlFile.getCurrentUrl().source_url);
                requestCounter++;
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

    SearchResult[] searchResultRequest(String search) {
        //TODO other parameters (lang)
        String urlPattern = "photon.komoot.io/api/?q=" + search.replaceAll("&", ""); //TODO check for any other characters that need to be accounted for
        InputStream results = get(urlPattern);
        return null;
    }

    SearchResult[] reverseSearchRequest(double lat, double lon) {
        //TODO
        return null;
    }

    void tileGetRequest(int x, int y, int zoom, String urlPattern) {
        BufferedImage image = null;
        if (disableWebRequests || TileManager.isTileOutOfBounds(x, y, zoom) || failedRequests.contains(TileManager.getKey(zoom, x, y))) return;

        urlPattern = ((urlPattern.replace("{z}", Integer.toString(zoom)).replace("{x}", Integer.toString(x))).replace("{y}", Integer.toString(y)).replace("{s}", subDomain));
        try {
            InputStream inputStream = get(urlPattern);
            if (inputStream == null) return;
            image = ImageIO.read(inputStream);
            File out = new File(TileManager.getRootFile() + "openminemap/"+TileManager.cacheName+"/"+zoom+"/"+x+"-"+y+".png");
            ImageIO.write(image, "png", out);
            RequestManager.pendingRequest = null;
            requestCounter = 0;
        } catch (IOException e) {
            System.out.println("Error during tile write: " + e);
            e.printStackTrace();
        }
    }

    private InputStream get(String url) {
        try {
            URL url1 = new URI(url).toURL();
            URLConnection connection = url1.openConnection();

            connection.setRequestProperty("User-Agent", "Java/21.0.8 OpenMineMap (contact: minemasterlegacy@gmail.com)");
            connection.setRequestProperty("cache-control", "max-age=7");
            connection.setUseCaches(true);
            connection.setRequestProperty("Retry-After", "3");

            connection.connect();
            return connection.getInputStream();
        } catch (Exception e) {
            System.out.println("Error during url request: " + e);
            e.printStackTrace();
            return null;
        }
    }

}
