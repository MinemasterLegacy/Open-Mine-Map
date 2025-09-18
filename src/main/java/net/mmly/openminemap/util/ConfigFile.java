package net.mmly.openminemap.util;

import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.TileManager;

import java.io.*;
import java.util.HashMap;

public class ConfigFile {
    public static File configFile;
    public static boolean isConfigLoaded = false;
    private static HashMap<String, String> configParams = new HashMap<>();
    private static final String[] keyNames = new String[] { //names for every config option / parameter
            "Theme",
            "HudMapX",
            "HudMapY",
            "HudMapWidth",
            "HudMapHeight",
            "TileMapUrl",
            "MaxLoadedTiles",
            "§hudlastzoom",
            "§fslastzoom",
            "§fslastx",
            "§fslasty"
    };
    private static final String[] defaultValues = new String[] { //default values for every config option / parameter
            "vanilla",
            "10",
            "10",
            "144",
            "81",
            "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
            "500",
            "0",
            "0",
            "64",
            "64"
    };
    private static final int numOfArgs = keyNames.length;

    public static boolean establishConfigFile() {
        try {
            configFile = new File(TileManager.getRootFile() + "openminemap/config.txt");
            configFile.createNewFile();
            readFromFile();
            isConfigLoaded = true;
            writeToFile();
        } catch (IOException e) {
            System.out.println("Could not discover/create openminemap/config.txt ; Configuration options will not be loaded or saved");
            return false;
        }
        return true;
    }

    public static boolean writeParameter(String parameter, String value) {
        if (configParams.replace(parameter, value) == null) return false;
        else return true;
    }

    public static String readParameter(String parameter) {
        return configParams.get(parameter);
    }

    public static boolean writeToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            for (int i = 0; i < numOfArgs; i++) {
                writer.write(keyNames[i]+" : "+configParams.get(keyNames[i]));
                writer.newLine();

            }
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean readFromFile() {
        configParams.clear();
        String line;
        String[] kvPair;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            for(int i = 0; i < numOfArgs; i++) {
                line = reader.readLine();
                if (line != null) {
                    kvPair = line.split(" : ");
                    if (kvPair.length == 1) configParams.put(kvPair[0], "");
                    else configParams.put(kvPair[0], kvPair[1]);
                } else {
                    configParams.put(keyNames[i], defaultValues[i]);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}
