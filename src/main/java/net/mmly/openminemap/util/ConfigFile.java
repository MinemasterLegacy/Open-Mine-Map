package net.mmly.openminemap.util;

import net.minecraft.client.MinecraftClient;
import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.map.TileManager;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class ConfigFile {
    public static File configFile;
    public static boolean isConfigLoaded = false;
    private static final HashMap<ConfigOptions, String> configParams = new HashMap<>();
    private static final String[] defaultValues = ConfigOptions.defaultValues;
    private static final int numOfArgs = ConfigOptions.length();

    public static void establishConfigFile() {
        try {
            configFile = new File(TileManager.getRootFile() + "openminemap/config.txt");
            configFile.createNewFile();
            readFromFile();
            isConfigLoaded = true;
            writeToFile();
        } catch (IOException e) {
            OpenMineMap.LOGGER.warn("Could not discover/create openminemap/config.txt ; Configuration options will not be loaded or saved");
        }
    }

    public static boolean writeParameter(ConfigOptions parameter, String value) {
        return configParams.replace(parameter, value) != null;
    }

    public static void writeDefaultParameter(ConfigOptions parameter) {
        configParams.replace(parameter, defaultValues[searchFor(ConfigOptions.getRawTextOf(parameter))]);
    }

    public static String readDefaultParameter(ConfigOptions parameter) {
        return defaultValues[searchFor(ConfigOptions.getRawTextOf(parameter))];
    }

    public static String readOption(ConfigOptions parameter) {
        return configParams.get(parameter);
        //Erroneous value handling is now handled in the ConfigOptions enum
    }

    /*
    @Deprecated
    public static String readParameter(ConfigOptions parameter) {
        String value = configParams.get(parameter);
        //System.out.println("Value for key "+parameter+" is "+value);
        if (value.equals("null")) { //failsafe in case a value is somehow set to null
            OpenMineMap.LOGGER.warn("Null value for valid parameter " + parameter.toString() + "detected; possible error occured. Will revert to default parameter.");
            writeDefaultParameter(parameter);
            return configParams.get(parameter);
        } else {
            return value;
        }
    }

     */

    public static void writeOnClose(MinecraftClient client) {
        MapScreen.writeParameters();
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
            OpenMineMap.LOGGER.error("Could not write to config file: " + e.getMessage());
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
                    //System.out.println("set "+kvPair[0]+" to "+kvPair[1]);
                    foundParameter[searchResult] = true;
                }
            }
            reader.close();
            for (int i = 0; i < numOfArgs; i++) {
                if (!foundParameter[i]) {
                    configParams.put(ConfigOptions.values()[i], defaultValues[i]);
                }
            }
            OpenMineMap.LOGGER.info("Loaded " + configParams.size() + " configuration options");
        } catch (IOException e) {
            OpenMineMap.LOGGER.error("Could not read from config file: " + e.getMessage());
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
