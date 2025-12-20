package net.mmly.openminemap.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.TileUrlErrorType;
import net.mmly.openminemap.map.TileManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class TileUrlFile {

    public static boolean loadWasFailed = false;
    public static boolean urlWasReset = false;
    public static String osmAttribution;
    public static final String osmAttributionUrl = "https://openstreetmap.org/copyright";

    private static TileUrlErrorType loadError = TileUrlErrorType.NO_ERROR;
    private static TileUrl errorUrl;

    private static TileUrl[] tileUrls;
    private static final TileUrl defaultUrl = new TileUrl(
            "OpenStreetMap",
            "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
            "",
            new String[] {
                "https://openstreetmap.org/copyright"
            }
    );
    private static int currentUrlId;

    private static boolean createDefaultFile(File file) {
       try {
            if (!file.createNewFile()) throw new IOException();
           //System.out.println(file.createNewFile());

            //Identifier i = Identifier.of("openminemap", "defaulttilesources.json");
            //InputStream stream = MinecraftClient.getInstance().getResourceManager().getResource(i).get().getInputStream();

            FileWriter writer = new FileWriter(TileManager.getRootFile() + "openminemap/tileSources.json");
            //writer.write(Arrays.toString(stream.readAllBytes()));
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

    public static void addApplicableErrors(ClientLoginNetworkHandler clientLoginNetworkHandler, MinecraftClient minecraftClient) {
        Text debugStart = Text.translatable("omm.error.tile-url.start");
        if (loadError != TileUrlErrorType.NO_ERROR) {
            String name;
            if (errorUrl == null) {
                name = ": ";
            } else if (errorUrl.name == null)  {
                name = ": ";
            } else name = " - Error Parsing Tile Source " + errorUrl.name + ": ";
            OpenMineMapClient.debugMessages.add(debugStart.getString() + name + switch (loadError) {
                case MALFORMED_JSON_FILE -> Text.translatable("omm.error.tile-source-json-formatting").getString();
                case NULL_TILE_URL -> Text.translatable("omm.error.blank-tile-url").getString();
                case NULL_VALUE -> Text.translatable("omm.error.blank-field").getString();
                case MALFORMED_SOURCE_URL -> Text.translatable("omm.error.source-link-invalid").getString();
                case MALFORMED_ATTRIBUTION_LINK -> Text.translatable("omm.error.attribution-link-invalid").getString();
                case INVALID_SOURCE_URL_BRACKET_PLACEMENT -> Text.translatable("omm.error.source-bracket-placement").getString();
                case INVALID_ATTRIBUTION_BRACKET_PLACEMENT -> Text.translatable("omm.error.attribution-bracket-placement").getString();
                case MISMATCHED_ATTRIBUTION_LINKS -> Text.translatable("omm.error.link-number-mismatch").getString();
                case MISSING_X_POSITION_FIELD -> Text.translatable("omm.error.field-missing-x").getString();
                case MISSING_Y_POSITION_FIELD -> Text.translatable("omm.error.field-missing-y").getString();
                case MISSING_ZOOM_FIELD -> Text.translatable("omm.error.field-missing-zoom").getString();
                default -> throw new IllegalStateException("Unexpected value: " + loadError);
            });
        }
    }

    public static void establishUrls() {
        Gson gson = new Gson();

        File tileUrlsFile = new File(TileManager.getRootFile() + "openminemap/tileSources.json");
        if (!tileUrlsFile.exists()) createDefaultFile(tileUrlsFile);

        try (FileReader reader = new FileReader(tileUrlsFile)) {
            TileUrlGroup tileUrlGroup = null;
            try {
                tileUrlGroup = gson.fromJson(reader, TileUrlGroup.class);
            } catch (JsonSyntaxException e) {
                setError(TileUrlErrorType.MALFORMED_JSON_FILE, null);
                throw new TileUrlFileFormatException();
            }

            if (tileUrlGroup == null) {
                setError(TileUrlErrorType.NULL_TILE_URL, null);
                throw new TileUrlFileFormatException();
            }

            tileUrls = new TileUrl[tileUrlGroup.sources.length + 1];
            tileUrls[0] = defaultUrl;
            TileUrlErrorType isValid;
            for (int i = 0; i < tileUrlGroup.sources.length; i++) {
                isValid = checkValidityOf(tileUrlGroup.sources[i]);
                if (isValid == TileUrlErrorType.NO_ERROR) tileUrls[i + 1] = tileUrlGroup.sources[i];
                else setError(isValid, tileUrlGroup.sources[i]);
            }

            String setUrl = ConfigFile.readParameter(ConfigOptions.TILE_MAP_URL);
            for (int i = 0; i < tileUrls.length; i++) {
                if (tileUrls[i].name.equals(setUrl)) {
                    currentUrlId = i;
                    System.out.println("Current Tile Url set to \""+tileUrls[currentUrlId].name+"\"");
                    TileManager.setCacheDir();
                    return;
                }
            }

        } catch (IOException | TileUrlFileFormatException e) {
            loadWasFailed = true;
            tileUrls = new TileUrl[]{defaultUrl};
        }
        currentUrlId = 0;
        urlWasReset = true;
        TileManager.setCacheDir();
    }

    private static TileUrlErrorType checkValidityOf(TileUrl tileUrl) {
        //System.out.println(" # Starting a TileUrl check.");
        //check for null values
        if (tileUrl == null) return TileUrlErrorType.NULL_TILE_URL;
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

    public static TileUrl getTileUrl(int id) {
        return tileUrls[id];
    }

    public static TileUrl getCurrentUrl() {
        return tileUrls[currentUrlId];
    }

    public static int getCurrentUrlId() {
        return currentUrlId;
    }

    public static int getCurrentIdRange() {
        return tileUrls.length;
    }

    public static void setCurrentUrl(int id) {
        currentUrlId = id;
    }

    private static String getDefaultFileText() {
        return "{\n" +
                "  \"sources\": [\n" +
                "    {\n" +
                "      \"name\": \"Humanitarian\",\n" +
                "      \"source_url\": \"https://{s}.tile.openstreetmap.fr/hot/{z}/{x}/{y}.png\",\n" +
                "      \"attribution\": \"Tiles style by {Humanitarian OpenStreetMap Team} hosted by {OpenStreetMap France.}\",\n" +
                "      \"attribution_links\": [\n" +
                "        \"https://www.hotosm.org\",\n" +
                "        \"https://www.openstreetmap.fr\"\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"CyclOSM\",\n" +
                "      \"source_url\": \"https://{s}.tile-cyclosm.openstreetmap.fr/cyclosm/{z}/{x}/{y}.png\",\n" +
                "      \"attribution\": \"{Leaflet} | {CyclOSM}\",\n" +
                "      \"attribution_links\": [\n" +
                "        \"https://leafletjs.com\",\n" +
                "        \"https://www.cyclosm.org\"\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}

class TileUrlGroup {
    TileUrl[] sources;
}

class TileUrlFileFormatException extends Exception { //done
    public TileUrlFileFormatException() {
        super("Formatting error while reading tileSources.json");
    }
}

