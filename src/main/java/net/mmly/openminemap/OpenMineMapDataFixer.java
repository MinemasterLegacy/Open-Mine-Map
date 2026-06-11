package net.mmly.openminemap;

import com.google.gson.Gson;

import java.io.File;

public class OpenMineMapDataFixer {
    public static void fix() {
        //transform old tileSources.json file into rasters.json file
        File tileSources = new File("openminemap/tileSources.json");
        if (tileSources.exists()) {
            File tileSourcesOld = new File("openminemap/tileSources.json.old");
            if (tileSources.renameTo(tileSourcesOld)) {
                File rasters = new File("openminemap/rasters.json");
                Gson gson = new Gson();

            }
        }
    }
}
