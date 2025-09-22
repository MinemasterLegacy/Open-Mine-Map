package net.mmly.openminemap.util;

import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.TileManager;

import javax.xml.crypto.dsig.keyinfo.KeyName;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

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
            "HudCompassX",
            "HudCompassY",
            "HudCompassWidth",
            "TileMapUrl",
            "ArtificialZoom",
            "SnapAngle",
            "§hudtoggle",
            "§hudlastzoom",
            "§fslastzoom",
            "§fslastx",
            "§fslasty"
    };
    private static final String[] defaultValues = new String[] { //default values for every config option / parameter
            "vanilla", //theme
            "10", //hudmapx
            "10", //hudmapy
            "144", //hudmapwidth
            "81", //hudmapheight
            "10", //hudcompassx
            "96", //hudcompassy
            "144", //hudcompasswidth
            "https://tile.openstreetmap.org/{z}/{x}/{y}.png", //tilemapurl
            "false",
            "/",
            "true",
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
        String value = configParams.get(parameter);
        //System.out.println("Value for key "+parameter+" is "+value);
        if (value.equals("null")) { //failsafe in case a value is somehow set to null
            for (int i = 0; i < keyNames.length; i++) {
                if (keyNames[i].equals(parameter)) {
                    System.out.println("Null value for valid parameter detected; possible error occoured");
                    writeParameter(parameter, defaultValues[i]);
                    writeToFile();
                    return defaultValues[i];
                }
            }
            return null;
        } else {
            return value;
        }
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
        boolean[] foundParameter = new boolean[keyNames.length];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            for (int i = 0; i < numOfArgs; i++) {
                line = reader.readLine();
                if (line == null) break;
                kvPair = line.split(" : ");
                if (kvPair.length == 1) kvPair = new String[] {kvPair[0], ""};
                int searchResult = searchArray(keyNames, kvPair[0]);
                if (searchResult >= 0) {
                    configParams.put(kvPair[0], kvPair[1]);
                    System.out.println("set "+kvPair[0]+" to "+kvPair[1]);
                    foundParameter[searchResult] = true;
                }
            }
            reader.close();
            for (int i = 0; i < numOfArgs; i++) {
                if (!foundParameter[i]) {
                    configParams.put(keyNames[i], defaultValues[i]);
                }
            }
        } catch (IOException e) {
            System.out.println("readFromFile Error: "+e.getMessage());
            return false;
        }
        return true;
    }

    private static int searchArray(String[] array, String searchFor) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], searchFor)) return i;
        }
        return -1;
    }
}
