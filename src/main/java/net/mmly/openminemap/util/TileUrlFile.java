package net.mmly.openminemap.util;

import com.google.gson.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.TileUrlErrorType;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.raster.LayerType;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class TileUrlFile {

    private static ArrayList<TileUrl> enabledRasters = new ArrayList<>();
    private static ArrayList<TileUrl> urlPresets = new ArrayList<>();
    private static ArrayList<TileUrl> customRasters = new ArrayList<>();

    public static boolean loadWasFailed = false;
    public static String osmAttribution;
    public static final String osmAttributionUrl = "https://openstreetmap.org/copyright";

    private static TileUrlErrorType loadError = TileUrlErrorType.NO_ERROR;
    private static TileUrl errorUrl;
    //TODO make sure names must be custom
    //TODO validate if name is valid for file path
    //TODO show mapbox logo for mapbox urls
    private static TileUrl[] tileUrls;
    private static final TileUrl defaultUrl = new TileUrl(
            "OpenStreetMap",
            "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
            "",
            new String[] {
                "https://openstreetmap.org/copyright"
            },
            LayerType.BASE
    );
    private static TileUrl currentTileUrl;

    private static boolean createDefaultFile(File file) {
       try {
            if (!file.createNewFile()) throw new IOException();
            FileWriter writer = new FileWriter(TileManager.getRootFile() + "openminemap/tileSources.json");
            writer.write(getDefaultFileText());
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void initOsmAttribution() {
        osmAttribution = Text.translatable("omm.osm-attribution").getString();
    }

    private static void setError(TileUrlErrorType errorType, TileUrl url) {
        loadError = errorType;
        errorUrl = url;
    }

    public static boolean loadRastersFromFile() {
        try {
            TileUrlFile.establishPresets();
            TileUrlFile.establishUrls();
        } catch (IOException | NullPointerException ignored) {
            //do nothing, will try again next requester cycle
            //ignored.printStackTrace();
            //System.out.println("failed cycle");
            return false;
        }
        OpenMineMap.LOGGER.info("Loaded Raster Providers");
        addApplicableErrors(null);
        return true;
    }

    /// Adds url load errors to chat as needed
    public static void addApplicableErrors(MinecraftClient client) {
        Text debugStart = Text.translatable("omm.error.tile-url.start");
        if (loadError != TileUrlErrorType.NO_ERROR) {
            String name;
            if (errorUrl == null) {
                name = ": ";
            } else if (errorUrl.name == null)  {
                name = ": ";
            } else name = " - " + Text.translatable("omm.error.tile-url.parse").getString() +" "+ errorUrl.name + ": ";
            OpenMineMapClient.debugMessages.add(debugStart.getString() + name + Text.translatable(loadError.translationKey).getString());
        }
    }

    public static void establishUrls() throws IOException {

        try {
            File tileUrlsFile = new File(TileManager.getRootFile() + "openminemap/tileSources.json");
            if (!tileUrlsFile.exists()) if (!createDefaultFile(tileUrlsFile)) {
                throw new IOException();
            }

            TileUrl[] tileUrlArray;
            try {
                tileUrlArray = loadRasters(new FileInputStream(tileUrlsFile), false);
            } catch (JsonSyntaxException e) {
                setError(TileUrlErrorType.MALFORMED_JSON_FILE, null);
                throw new TileUrlFileFormatException();
            }

            if (tileUrlArray == null) {
                setError(TileUrlErrorType.NULL_TILE_URL, null);
                throw new TileUrlFileFormatException();
            }

            checkArrayValidity(tileUrlArray, false);
            tileUrls = addDefaultRaster(tileUrlArray);

            //set the current url based on the set config option
            String setUrl = ConfigOptions.TILE_MAP_URL.getAsString();
            for (TileUrl tileUrl : tileUrls) {
                if (tileUrl.name.equals(setUrl)) {
                    enabledRasters.addLast(tileUrl);
                    setCurrentUrl(tileUrl);
                    return;
                }
            }

            //TODO temp
            enabledRasters.addLast(defaultUrl);
            setCurrentUrl(enabledRasters.getLast());
            //

        } catch (IOException | TileUrlFileFormatException e) {
            loadWasFailed = true;
            tileUrls = new TileUrl[]{defaultUrl};
            enabledRasters.addLast(defaultUrl);
            setCurrentUrl(defaultUrl);
        }

        //TODO check urls with undefined template id for presets
    }

    public static void establishPresets() throws NullPointerException, IOException {
        //TODO specific error handling for presets
        try {
            TileUrl[] tileUrlArray;
            try {
                Optional<Resource> file = MinecraftClient.getInstance().getResourceManager().getResource(Identifier.of("openminemap", "rasterpresets.json"));
                if (file.isEmpty()) {
                    throw new IOException();
                }
                tileUrlArray = loadRasters(file.get().getInputStream(), true);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                setError(TileUrlErrorType.MALFORMED_JSON_FILE, null);
                throw new TileUrlFileFormatException();
            }

            if (tileUrlArray == null) {
                setError(TileUrlErrorType.NULL_TILE_URL, null);
                throw new TileUrlFileFormatException();
            }

            tileUrlArray[0] = defaultUrl;

            checkArrayValidity(tileUrlArray, true);
            urlPresets = new ArrayList<>(Arrays.stream(tileUrlArray).toList());

        } catch (TileUrlFileFormatException e) {
            //urlPresets = new TileUrl[]{};
            OpenMineMap.LOGGER.error("Raster Presets failed to load.");
        }
    }

    private static void checkArrayValidity(TileUrl[] urls, boolean isPresets) throws TileUrlFileFormatException {
        for (int i = isPresets ? 1 : 0; i < urls.length; i++) {
            TileUrlErrorType exception = checkValidityOf(urls[i]);
            if (exception != TileUrlErrorType.NO_ERROR) {
                setError(exception, urls[i]);
                throw new TileUrlFileFormatException();
            }
        }
    }

    private static TileUrl[] addDefaultRaster(TileUrl[] urls) {
        TileUrl[] newUrls = new TileUrl[urls.length + 1];
        newUrls[0] = defaultUrl;
        System.arraycopy(urls, 0, newUrls, 1, urls.length);
        return newUrls;
    }

    private static TileUrl[] loadRasters(InputStream stream, boolean isPreset) throws TileUrlFileFormatException {
        Gson gson = new Gson();
        Map returnedResult;

        try {
            returnedResult = gson.fromJson(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)), Map.class);
        } catch (NullPointerException e) {
            //TODO
            return null;
        }

        JsonArray rasters = gson.toJsonTree(returnedResult, Map.class).getAsJsonObject().get("sources").getAsJsonArray();
        TileUrl[] tileUrls = new TileUrl[rasters.size()];

        for (int i = 0; i < tileUrls.length; i++) {
            tileUrls[i] = tileUrlOf(rasters.get(i).getAsJsonObject(), isPreset);
        }

        if (tileUrls[tileUrls.length - 1] == null) return null;
        return tileUrls;

    }

    /// Convert a JsonObject representing a raster to TileUrl
    private static TileUrl tileUrlOf(JsonObject raster, boolean isPreset) throws TileUrlFileFormatException {
       try {
           if (isPreset) return new TileUrl(
                   raster.get("templateId").getAsInt(),
                   raster.get("name").getAsString(),
                   raster.get("source_url").getAsString(),
                   raster.get("attribution").getAsString(),
                   arrayOf(raster.get("attribution_links").getAsJsonArray()),
                   LayerType.BASE.toString()
           );

           if (raster.get("templateId") != null) return new TileUrl(
                   raster.get("templateId").getAsInt(),
                   raster.get("token").getAsString()
           );

           if (raster.get("name") != null) return new TileUrl(
                   raster.get("name").getAsString(),
                   raster.get("source_url").getAsString(),
                   raster.get("attribution").getAsString(),
                   arrayOf(raster.get("attribution_links").getAsJsonArray()),
                   raster.get("layerType").getAsString()
           );
       } catch (NullPointerException e) {
           setError(TileUrlErrorType.NULL_VALUE, null);
           throw new TileUrlFileFormatException();
       }

       return null;
    }

    private static String[] arrayOf(JsonArray jsonArray) {
        String[] array = new String[jsonArray.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = jsonArray.get(i).getAsString();
        }
        return array;
    }

    /// Check a raster provider to see if it is valid, returns an error as an enum if not
    private static TileUrlErrorType checkValidityOf(TileUrl tileUrl) {
        //System.out.println(" # Starting a TileUrl check.");
        //check for null values

        if (tileUrl == null) {
            return TileUrlErrorType.NULL_TILE_URL;
        }
        if (tileUrl.name == null ||
            tileUrl.attribution == null ||
            tileUrl.source_url == null ||
            tileUrl.attribution_links == null
        ) return TileUrlErrorType.NULL_VALUE;

        //check for zoom, x, and y fields
        if (tileUrl.source_url.replaceAll("\\{x}", "").length() == tileUrl.source_url.length()) return TileUrlErrorType.MISSING_X_POSITION_FIELD;
        if (tileUrl.source_url.replaceAll("\\{y}", "").length() == tileUrl.source_url.length()) return TileUrlErrorType.MISSING_Y_POSITION_FIELD;
        if (tileUrl.source_url.replaceAll("\\{z}", "").length() == tileUrl.source_url.length()) return TileUrlErrorType.MISSING_ZOOM_FIELD;

        //check for invalid urls
        try {
            new URL(tileUrl.source_url.replaceAll("\\{.}", "a")).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return TileUrlErrorType.MALFORMED_SOURCE_URL;
        }
        for (String url : tileUrl.attribution_links) {
            try {
                new URL(url).toURI();
            } catch (MalformedURLException | URISyntaxException e) {
                return TileUrlErrorType.MALFORMED_ATTRIBUTION_LINK;
            }
        }

        //check for bracket placement/formatting
        int numLinks = 0;
        boolean inBrackets = false;
        for (char c : tileUrl.source_url.toCharArray()) {
            if (c == '{') {
                if (!inBrackets) inBrackets = true;
                 else return TileUrlErrorType.INVALID_SOURCE_URL_BRACKET_PLACEMENT;
            }
            if (c == '}') {
                if (inBrackets) inBrackets = false;
                 else return TileUrlErrorType.INVALID_SOURCE_URL_BRACKET_PLACEMENT;
            }
        }
        if (inBrackets) return TileUrlErrorType.INVALID_SOURCE_URL_BRACKET_PLACEMENT;
        for (char c : tileUrl.attribution.toCharArray()) {
            if (c == '{') {
                if (!inBrackets) inBrackets = true;
                else return TileUrlErrorType.INVALID_ATTRIBUTION_BRACKET_PLACEMENT;
            }
            if (c == '}') {
                if (inBrackets) {
                    inBrackets = false;
                    numLinks++;
                } else return TileUrlErrorType.INVALID_ATTRIBUTION_BRACKET_PLACEMENT;
            }
        }
        if (inBrackets) return TileUrlErrorType.INVALID_ATTRIBUTION_BRACKET_PLACEMENT;

        //check that number of attribution links is equal to brackets
        if (tileUrl.attribution_links.length != numLinks) return TileUrlErrorType.MISMATCHED_ATTRIBUTION_LINKS;

        //if all previous check were passed (nothing returned false), return true
        return TileUrlErrorType.NO_ERROR;
    }

    public static TileUrl[] getTileUrls() {
        return tileUrls;
    }

    public static ArrayList<TileUrl> getPresets() {
        return urlPresets;
    }

    public static TileUrl getTileUrl(int id) {
        return tileUrls[id];
    }

    public static TileUrl getUrlByName(String name) {
        for (TileUrl url : tileUrls) {
            if (url.name.equals(name)) return url;
        }
        return null;
    }

    public static TileUrl getCurrentUrl() {
        if (tileUrls == null || currentTileUrl == null) return defaultUrl;
        return currentTileUrl;
    }

    public static ArrayList<TileUrl> getEnabledRasters() {
        return enabledRasters;
    }

    public static int getCurrentIdRange() {
        return enabledRasters.size();
    }

    public static void setCurrentUrl(TileUrl tileUrl) {
        currentTileUrl = tileUrl;
        TileManager.setCacheDir();
    }

    private static String getDefaultFileText() {
        return """
            {
                "sources": []
            }
        """;
    }
    /*
    private static String getDefaultFileText() {
        return """
                {
                  "sources": [
                    {
                      "name": "Humanitarian",
                      "source_url": "https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png",
                      "attribution": "Tiles style by {Humanitarian OpenStreetMap Team} hosted by {OpenStreetMap France.}",
                      "attribution_links": [
                        "https://www.hotosm.org",
                        "https://www.openstreetmap.fr"
                      ]
                    },
                    {
                      "name": "CyclOSM",
                      "source_url": "https://{s}.tile-cyclosm.openstreetmap.fr/cyclosm/{z}/{x}/{y}.png",
                      "attribution": "{Leaflet} | {CyclOSM}",
                      "attribution_links": [
                        "https://leafletjs.com",
                        "https://www.cyclosm.org"
                      ]
                    }
                  ]
                }""";
    }

     */
}

class TileUrlFileFormatException extends Exception { //done
    public TileUrlFileFormatException() {
        super("Formatting error while reading tileSources.json");
    }
}

