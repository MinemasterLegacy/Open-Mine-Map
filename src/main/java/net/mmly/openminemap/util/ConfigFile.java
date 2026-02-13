package net.mmly.openminemap.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.map.TileManager;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class ConfigFile {
    public static File configFile;
    public static boolean isConfigLoaded = false;
    private static HashMap<ConfigOptions, String> configParams = new HashMap<>();
    private static final String[] defaultValues = new String[] { //default values for every config option / parameter
            "10", //hudmapx
            "10", //hudmapy
            "144", //hudmapwidth
            "81", //hudmapheight
            "10", //hudcompassx
            "96", //hudcompassy
            "144", //hudcompasswidth
            "OpenStreetMap", //tilemapurl, was "https://tile.openstreetmap.org/{z}/{x}/{y}.png" in 1.3.0
            "false", //ArtificialZoom
            "", //SnapAngle
            "/tpll", //RightClickMenuUses
            "off", //ReverseScroll
            "local", //ShowPlayers
            "local", //ShowDirectionIndicators
            "on", //AltitudeShading
            "0.4", //ZoomStrength
            "on", //HoverNames

            "true", //hudtoggle
            "true", //hudenabled
            "0", //hudlastzoom
            "0", //fslastzoom
            "64", //fslastx
            "64", //fslasty

            "false", //DisableWebRequests
            "false"
    };
    private static final int numOfArgs = ConfigOptions.length();

    public static void establishConfigFile() {
        try {
            configFile = new File(TileManager.getRootFile() + "openminemap/config.txt");
            configFile.createNewFile();
            readFromFile();
            isConfigLoaded = true;
            writeToFile();
        } catch (IOException e) {
            System.out.println("Could not discover/create openminemap/config.txt ; Configuration options will not be loaded or saved");
        }
    }

    public static boolean writeParameter(ConfigOptions parameter, String value) {
        return configParams.replace(parameter, value) != null;
    }

    public static boolean writeDefaultParameter(ConfigOptions parameter) {
        System.out.println(parameter);
        System.out.println(defaultValues[searchFor(ConfigOptions.getRawTextOf(parameter))]);
        return configParams.replace(parameter, defaultValues[searchFor(ConfigOptions.getRawTextOf(parameter))]) != null;
    }

    public static String readDefaultParameter(ConfigOptions parameter) {
        return defaultValues[searchFor(ConfigOptions.getRawTextOf(parameter))];
    }

    public static String readParameter(ConfigOptions parameter) {
        String value = configParams.get(parameter);
        //System.out.println("Value for key "+parameter+" is "+value);
        if (value.equals("null")) { //failsafe in case a value is somehow set to null
            for (int i = 0; i < ConfigOptions.length(); i++) {
                if (ConfigOptions.values()[i] == parameter) {
                    System.out.println("Null value for valid parameter detected; possible error occoured");
                    writeParameter(parameter, defaultValues[i]);
                    writeToFile();
                    return defaultValues[i];
                }
            }
            System.out.println("A parameter was not found when reading the ConfigFile");
            return null;
        } else {
            return value;
        }
    }

    public static void writeOnClose(ClientLoginNetworkHandler clientLoginNetworkHandler, MinecraftClient minecraftClient) {
        writeToFile();
    }

    public static boolean writeToFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            for (int i = 0; i < numOfArgs; i++) {
                writer.write(ConfigOptions.getRawTextOf(ConfigOptions.values()[i])+" : "+configParams.get(ConfigOptions.values()[i]));
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
        boolean[] foundParameter = new boolean[ConfigOptions.length()];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFile));
            for (int i = 0; i < numOfArgs; i++) {
                line = reader.readLine();
                if (line == null) break;
                kvPair = line.split(" : ");
                if (kvPair.length == 1) kvPair = new String[] {kvPair[0], ""};
                int searchResult = searchFor(kvPair[0]);
                if (searchResult >= 0) {
                    configParams.put(ConfigOptions.getOptionOf(kvPair[0]), kvPair[1]);
                    System.out.println("set "+kvPair[0]+" to "+kvPair[1]);
                    foundParameter[searchResult] = true;
                }
            }
            reader.close();
            for (int i = 0; i < numOfArgs; i++) {
                if (!foundParameter[i]) {
                    configParams.put(ConfigOptions.values()[i], defaultValues[i]);
                }
            }
        } catch (IOException e) {
            System.out.println("readFromFile Error: "+e.getMessage());
            return false;
        }
        return true;
    }

    private static int searchFor(String searchFor) {
        ConfigOptions[] array = ConfigOptions.values();
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(ConfigOptions.getRawTextOf(array[i]), searchFor)) return i;
        }
        return -1;
    }
}
