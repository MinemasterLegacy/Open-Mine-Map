package net.mmly.openminemap.util;

import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.map.TileManager;

import java.io.*;
import java.util.HashMap;

public class RasterApiKeysFile {

    private static HashMap<Integer, String> apiKeys = new HashMap<>();
    private static File keyFile = null;
    private static boolean loaded = false;
    private static final int NUM_KEYS = 4;
    private static final String DISCLAIMER_MESSAGE =
        "!!! WARNING: Do not publicly share or display this file; It contains API Keys !!!"
    ;

    private static void establishFile() {
        if (keyFile != null) return;
        try {
            keyFile = new File(TileManager.getRootFile() + "openminemap/apiKeys.txt");
            keyFile.createNewFile();
            loaded = true;
        } catch (IOException e) {
            OpenMineMap.LOGGER.warn("Could not discover/create openminemap/apiKeys.txt ; Keys will not be loaded or saved");
        }
    }

    public static String readApiKey(int presetId) {
        return apiKeys.get(presetId);
    }

    public static boolean hasApiKey(int presetId) {
        return apiKeys.containsKey(presetId);
    }

    public static void writeApiKey(int presetId, String key) {
        writeApiKey(presetId, key, true);
    }

    public static void writeApiKey(int presetId, String key, boolean writeToDisk) {
        apiKeys.put(presetId, key);
        if (key.isBlank()) apiKeys.remove(presetId);
        if (writeToDisk) writeToFile();
    }

    public static void writeToFile() {
        if (!loaded) return;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(keyFile));
            String[] values = apiKeys.values().toArray(new String[0]);
            Integer[] keys = apiKeys.keySet().toArray(new Integer[0]);
            writer.write(DISCLAIMER_MESSAGE);
            writer.newLine();
            for (int i = 0; i < values.length; i++) {
                writer.write(keys[i] + " : " + values[i]);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            OpenMineMap.LOGGER.error("Could not write to api keys file: " + e.getMessage());
        }
    }

    public static void readFromFile() {
        apiKeys.clear();
        establishFile();
        if (!loaded) return;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(keyFile));
            String line;
            String[] keyValuePair;
            reader.readLine(); //skip the disclaimer
            for (int i = 0; i < NUM_KEYS; i++) {
                line = reader.readLine();
                if (line == null) break;
                keyValuePair = line.split(" : ");
                if (keyValuePair.length != 2) throw new IOException("Invalid Pair");
                apiKeys.put(Integer.parseInt(keyValuePair[0].trim()), keyValuePair[1].trim());
            }
            reader.close();
        } catch (IOException | NumberFormatException e) {
            OpenMineMap.LOGGER.error("Could not read from api keys file: " + e.getMessage());
        }
    }

}
