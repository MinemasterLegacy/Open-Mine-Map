package net.mmly.openminemap.util;

import com.google.gson.Gson;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.map.TileManager;

import java.io.FileReader;
import java.io.IOException;

public class TileUrlFile {

    public static boolean loadWasFailed = false;
    public static boolean urlWasReset = false;
    public static final String osmAttribution = "© {OpenStreetMap Contributors}";
    public static final String osmAttributionUrl = "https://openstreetmap.org/copyright";

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

    public static void establishUrls() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(TileManager.getRootFile() + "openminemap/tileSources.json")) {
            TileUrlGroup tileUrlGroup = gson.fromJson(reader, TileUrlGroup.class);

            /*
            System.out.println(defaultUrl.name);
            System.out.println(defaultUrl.source_url);
            System.out.println(defaultUrl.attribution);
            System.out.println(Arrays.toString(defaultUrl.attribution_links));

            for (TileUrl tileUrl : tileUrlGroup.sources) {
                System.out.println(tileUrl.name);
                System.out.println(tileUrl.source_url);
                System.out.println(tileUrl.attribution);
                System.out.println(Arrays.toString(tileUrl.attribution_links));
            }

             */

            tileUrls = new TileUrl[tileUrlGroup.sources.length + 1];
            tileUrls[0] = defaultUrl;
            for (int i = 0; i < tileUrlGroup.sources.length; i++) {
                tileUrls[i + 1] = tileUrlGroup.sources[i];
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

            //TODO do not register urls that have missing data (OpenStreetMap will be missing attribution becuase the only attribution it needs is the base)

        } catch (IOException e) {
            loadWasFailed = true; //TODO manage scenarios where loads fail and urls arent found
            tileUrls = new TileUrl[]{defaultUrl};
        }
        currentUrlId = 0;
        urlWasReset = true;
        TileManager.setCacheDir();
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

}

class TileUrlGroup {
    TileUrl[] sources;
}