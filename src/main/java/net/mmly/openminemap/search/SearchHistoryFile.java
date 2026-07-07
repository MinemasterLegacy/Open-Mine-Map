package net.mmly.openminemap.search;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.map.TileManager;

import java.io.*;
import java.util.ArrayList;

public class SearchHistoryFile {
    private static boolean loaded = false;
    private static File histoyFile = null;
    private static ArrayList<SearchHistoryEntry> searchHistory = new ArrayList<>();
    private static final int TERM_LIMIT = 50;

    private static void establishFile() {
        if (histoyFile != null) return;
        try {
            histoyFile = new File(TileManager.getRootFile() + "openminemap/searchHistory.txt");
            histoyFile.createNewFile();
            loaded = true;
        } catch (IOException e) {
            OpenMineMap.LOGGER.warn("Could not discover/create openminemap/searchHistory.txt ; History will not be loaded or saved");
        }
    }

    public static void addHistoricResult(String term, SearchResult[] results) {
        searchHistory.addFirst(new SearchHistoryEntry(term, results));
        if (searchHistory.size() > TERM_LIMIT) searchHistory.removeLast();
    }

    public static void readFromFile() {
        Gson gson = new Gson();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(histoyFile));
            StringBuilder fileStringBuilder = new StringBuilder();
            for (Object s : reader.lines().toArray()) {
                fileStringBuilder.append((String) s);
            }
            String fileString = fileStringBuilder.toString();
            System.out.println(fileString);
            SearchHistoryEntry[] results = gson.fromJson(fileString, SearchHistoryEntry[].class);
        } catch (IOException e) {
            //TODO
        }
    }

    public static void writeToFile() {
        Gson gson = new Gson();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(histoyFile));
            writer.write(gson.toJson(searchHistory));
            writer.close();
        } catch (IOException | JsonParseException e) {
            OpenMineMap.LOGGER.error("Unable to write history to searchHistory.json: ");
            e.printStackTrace();
        }
    }

}

class SearchHistoryEntry {
    final String term;
    final SearchResult[] results;

    public SearchHistoryEntry(String term, SearchResult[] results) {
        this.term = term;
        this.results = results;
    }
}