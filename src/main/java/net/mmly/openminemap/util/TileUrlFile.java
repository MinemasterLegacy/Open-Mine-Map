package net.mmly.openminemap.util;

import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.enums.TileUrlErrorType;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.raster.LayerType;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class TileUrlFile {

    public static boolean loadFailed = false;
    public static String osmAttribution;
    public static final String osmAttributionUrl = "https://openstreetmap.org/copyright";
    public static File rasterFile;

    private static TileUrlErrorType loadError = TileUrlErrorType.NO_ERROR;
    private static TileUrl errorUrl;
    private static TileUrl[] tileUrls;
    public static final TileUrl defaultUrl = new TileUrl(
            0,
            "OpenStreetMap",
            "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
            "",
            new String[] {
                "https://openstreetmap.org/copyright"
            },
            "base"
    );

    private static boolean createDefaultFile(File file) {
        if (renameOldFile()) return true;
       try {
            if (!file.createNewFile()) throw new IOException();
            FileWriter writer = new FileWriter(rasterFile);
            writer.write(getDefaultFileText());
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /// Returns: True if an old file was found and successfully renamed
    private static boolean renameOldFile() {
        File tileSourcesFile = new File(TileManager.getRootFile() + "openminemap/tileSources.json"); //old raster file
        if (!tileSourcesFile.exists()) return false; //check if it exists
        File tileSourcesOldFile = new File(TileManager.getRootFile() + "openminemap/tileSources.json.old"); //file to rename to

        try {
            //copy old file to new file
            Files.copy(tileSourcesFile.toPath(), rasterFile.toPath());

            //rename old raster file
            tileSourcesOldFile.createNewFile();
            tileSourcesFile.renameTo(tileSourcesOldFile);

            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void initOsmAttribution() {
        osmAttribution = Component.translatable("omm.osm-attribution").getString();
    }

    private static void setError(TileUrlErrorType errorType, TileUrl url) {
        loadError = errorType;
        errorUrl = url;
    }

    public static void loadRastersFromFile() {
        rasterFile = new File(TileManager.getRootFile() + "openminemap/rasters.json");
        try {
            TileUrlFile.establishPresets();
            TileUrlFile.establishRasterProviders();
        } catch (TileUrlFileFormatException | IOException | NullPointerException ignored) {
            //do nothing, will try again next requester cycle
            //ignored.printStackTrace();
            //System.out.println("failed cycle");
            loadFailed = true;
            OpenMineMap.LOGGER.warn("Raster Providers failed to load, will fallback to default settings");
            addApplicableErrors();
            RasterProvider.initWithFailedLoad();
        }
        RasterProvider.finishInitialization();
        OpenMineMap.LOGGER.info("Loaded Raster Providers");
    }

    /// Adds url load errors to chat as needed
    private static void addApplicableErrors() {
        Component debugStart = Component.translatable("omm.error.tile-url.start");
        if (loadError != TileUrlErrorType.NO_ERROR) {
            String name;
            if (errorUrl == null) {
                name = ": ";
            } else if (errorUrl.name == null)  {
                name = ": ";
            } else name = " - " + Component.translatable("omm.error.tile-url.parse").getString() +" "+ errorUrl.name + ": ";
            OpenMineMapClient.debugMessages.add(debugStart.getString() + name + Component.translatable(loadError.translationKey).getString());
        }
    }

    private static void establishRasterProviders() throws IOException, TileUrlFileFormatException {

        try {
            if (!rasterFile.exists()) if (!createDefaultFile(rasterFile)) {
                throw new IOException();
            }

            TileUrl[] tileUrlArray;
            try {
                tileUrlArray = loadRasters(new FileInputStream(rasterFile), false);
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

            RasterProvider.initCustomRasters(tileUrlArray);

        } catch (IOException | TileUrlFileFormatException e) {
            OpenMineMap.LOGGER.error("Custom Rasters failed to load: ");
            e.printStackTrace();
            throw e;
        }

    }

    private static void establishPresets() throws NullPointerException, IOException, TileUrlFileFormatException {
        try {
            TileUrl[] tileUrlArray;
            try {
                Optional<Resource> file = Minecraft.getInstance().getResourceManager().getResource(Identifier.fromNamespaceAndPath("openminemap", "rasterpresets.json"));
                if (file.isEmpty()) {
                    throw new IOException();
                }
                tileUrlArray = loadRasters(file.get().open(), true);
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
            RasterProvider.initPresetRasters(tileUrlArray);

        } catch (TileUrlFileFormatException | IOException | NullPointerException e) {
            //urlPresets = new TileUrl[]{};
            OpenMineMap.LOGGER.error("Raster Presets failed to load: ");
            e.printStackTrace();
            throw e;
        }
    }

    private static void checkArrayValidity(TileUrl[] urls, boolean isPresets) throws TileUrlFileFormatException {
        for (int i = isPresets ? 1 : 0; i < urls.length; i++) {
            TileUrlErrorType exception = checkValidityOf(urls[i]);
            if (exception == TileUrlErrorType.NULL_TILE_URL) {
                continue; //will be handled once an arraylist
            }
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
            setError(TileUrlErrorType.MALFORMED_JSON_FILE, null);
            return null;
        }

        JsonArray rasters = gson.toJsonTree(returnedResult, Map.class).getAsJsonObject().get("sources").getAsJsonArray();
        TileUrl[] tileUrls = new TileUrl[rasters.size()];

        for (int i = 0; i < tileUrls.length; i++) {
            tileUrls[i] = tileUrlOf(rasters.get(i).getAsJsonObject(), isPreset);
        }

        return tileUrls;

    }

    /// Convert a JsonObject representing a raster to TileUrl
    private static TileUrl tileUrlOf(JsonObject raster, boolean isPreset) {
        try {
            if (isPreset) return new TileUrl(
                   raster.get("templateId").getAsInt(),
                   raster.get("name").getAsString(),
                   raster.get("source_url").getAsString(),
                   raster.get("attribution").getAsString(),
                   arrayOf(raster.get("attribution_links").getAsJsonArray()),
                   LayerType.BASE.toString()
            );

            //added to account for pre-overlay preset rasters
            if (RasterProvider.getPresetById(1).dataIsEqual(raster)) {
                OpenMineMap.LOGGER.warn("Ignoring preset-duplicate custom raster provider \"Humanitarian\"");
                return null;
            }
            if (RasterProvider.getPresetById(2).dataIsEqual(raster)) {
                OpenMineMap.LOGGER.warn("Ignoring preset-duplicate custom raster provider \"CyclOSM\"");
                return null;
            }

            if (raster.get("name") != null) return new TileUrl(
                   raster.get("name").getAsString(),
                   raster.get("source_url").getAsString(),
                   raster.get("attribution").getAsString(),
                   arrayOf(raster.get("attribution_links").getAsJsonArray()),
                   raster.get("layerType").getAsString()
            );

        } catch (NullPointerException ignored) {}
        
        String urlName;
        try {
            urlName = raster.get("name").getAsString();
        } catch (NullPointerException e) {
            urlName = "unknown";
        }
        OpenMineMap.LOGGER.warn("Ignoring unparseable raster provider \"" + urlName + "\".");

        return null;
    }

    public static String[] arrayOf(JsonArray jsonArray) {
        String[] array = new String[jsonArray.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = jsonArray.get(i).getAsString();
        }
        return array;
    }

    private static boolean charIsValid(char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == ' ' || c == '_';
    }

    /// Check a raster provider to see if it is valid, returns an error as an enum if not
    public static TileUrlErrorType checkValidityOf(TileUrl tileUrl) {
        return checkValidityOf(tileUrl, null);
    }

    /// Check a raster provider to see if it is valid, returns an error as an enum if not
    public static TileUrlErrorType checkValidityOf(TileUrl raster, TileUrl nameIgnoredRaster) {
        //check for null values
        if (raster == null) {
            return TileUrlErrorType.NULL_TILE_URL;
        }
        if (raster.name == null ||
            raster.attribution == null ||
            raster.source_url == null ||
            raster.attribution_links == null
        ) return TileUrlErrorType.NULL_VALUE;

        //Check for invalid characters
        for (char c : raster.name.toCharArray()) {
            if (!charIsValid(c)) return TileUrlErrorType.INVALID_CHARACTERS;
        }

        //Check for duplicate names
        TileUrl match = null;
        for (TileUrl customRaster : RasterProvider.getCustomRasters()) {
            if (customRaster.name.toLowerCase(Locale.US).equals(raster.name.toLowerCase(Locale.US))) match = customRaster;
        }
        for (TileUrl presetRaster : RasterProvider.getPresetRasters()) {
            if (presetRaster.name.toLowerCase(Locale.US).equals(raster.name.toLowerCase(Locale.US))) match = presetRaster;
        }
        if (raster.name.toLowerCase(Locale.US).equals(TileUrl.generatedLayerUrl.name)) match = TileUrl.generatedLayerUrl;

        // if the mathching raster is meant to be ignored, ignore it, otherwise return error
        if (match != null) {
            if (!(nameIgnoredRaster != null && match.dataIsEqual(nameIgnoredRaster))) {
                return TileUrlErrorType.DUPLICATE_NAME;
            }
        }

        //check for zoom, x, and y fields
        if (raster.source_url.replaceAll("\\{x}", "").length() == raster.source_url.length()) return TileUrlErrorType.MISSING_X_POSITION_FIELD;
        if (raster.source_url.replaceAll("\\{y}", "").length() == raster.source_url.length()) return TileUrlErrorType.MISSING_Y_POSITION_FIELD;
        if (raster.source_url.replaceAll("\\{z}", "").length() == raster.source_url.length()) return TileUrlErrorType.MISSING_ZOOM_FIELD;

        //check for invalid urls
        try {
            new URL(raster.source_url.replaceAll("\\{.}", "a")).toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return TileUrlErrorType.MALFORMED_SOURCE_URL;
        }
        for (String url : raster.attribution_links) {
            try {
                new URL(url).toURI();
            } catch (MalformedURLException | URISyntaxException e) {
                return TileUrlErrorType.MALFORMED_ATTRIBUTION_LINK;
            }
        }

        //check for bracket placement/formatting
        int numLinks = 0;
        boolean inBrackets = false;
        for (char c : raster.source_url.toCharArray()) {
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
        for (char c : raster.attribution.toCharArray()) {
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
        if (raster.attribution_links.length != numLinks) return TileUrlErrorType.MISMATCHED_ATTRIBUTION_LINKS;

        //if all previous check were passed (nothing returned false), return true
        return TileUrlErrorType.NO_ERROR;
    }

    public static TileUrl getUrlByName(String name) {
        for (TileUrl url : tileUrls) {
            if (url.name.equals(name)) return url;
        }
        return null;
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

    public static void saveCustomRastersToFile() {
        if (loadFailed) return;
        Gson gson = new Gson();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(rasterFile));
            writer.write(gson.toJson(new RasterSources(CustomUrl.ofUrls(RasterProvider.getCustomRasters()))));
            writer.close();
        } catch (IOException | JsonParseException e) {
            OpenMineMap.LOGGER.error("Unable to write rasters to tileSources.json: ");
            e.printStackTrace();
        }
    }
}

class RasterSources {
    ArrayList<CustomUrl> sources;

    RasterSources(ArrayList<CustomUrl> sources) {
        this.sources = sources;
    }
}

class CustomUrl {
    String name;
    String source_url;
    String attribution;
    String[] attribution_links;
    String layerType;

    CustomUrl(TileUrl tileUrl) {
        name = tileUrl.name;
        source_url = tileUrl.source_url;
        attribution = tileUrl.attribution;
        attribution_links = tileUrl.attribution_links;
        layerType = tileUrl.layerType.toString().toLowerCase(Locale.US);
    }

    public static ArrayList<CustomUrl> ofUrls(ArrayList<TileUrl> tileUrls) {
        ArrayList<CustomUrl> customUrls = new ArrayList<>();
        for (TileUrl url : tileUrls) {
            customUrls.add(new CustomUrl(url));
        }
        return customUrls;
    }
}

class TileUrlFileFormatException extends Exception { //done
    public TileUrlFileFormatException() {
        super("Formatting error while reading rasters.json");
    }
}

class RasterLoadException extends Exception {
    public RasterLoadException() {
        super("Raster Providers failed to initialize");
    }
}

