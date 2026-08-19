package net.mmly.openminemap.search;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.map.TileManager;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SearchHistoryFile {
    private static boolean loaded = false;
    private static File histoyFile = null;
    private static ArrayList<SearchHistoryEntry> searchHistory = new ArrayList<>();
    private static ArrayList<SearchResult> historyAsResults = new ArrayList<>();
    private static final int TERM_LIMIT = 50;

    public static void establishFile() {
        if (histoyFile != null) return;
        try {
            histoyFile = new File(TileManager.getRootFile() + "openminemap/searchHistory.json");
            histoyFile.createNewFile();
            readFromFile();
            loaded = true;
        } catch (IOException e) {
            OpenMineMap.LOGGER.warn("Could not discover/create openminemap/searchHistory.json ; History will not be loaded or saved");
        }
    }

    public static void addHistoricResult(String term, SearchResult[] results, boolean bounded) {
        if (results == null) return;
        if (results[0] == null) return;
        if (results[0].name.isEmpty() || Double.isNaN(results[0].latitude)) return;
        SearchResult[] results1 = new SearchResult[results.length];
        System.arraycopy(results, 0, results1, 0, results.length);
        searchHistory.addFirst(new SearchHistoryEntry(term, results1, bounded));
        historyAsResults.addFirst(SearchResult.historic(term, bounded));
        if (searchHistory.size() > TERM_LIMIT) {
            searchHistory.removeLast();
            historyAsResults.removeLast();
        }
        writeToFile();
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
            SearchHistoryEntry[] historyEntries = gson.fromJson(fileString, SearchHistoryEntry[].class);
            if (historyEntries == null) return;
            searchHistory = new ArrayList<>(Arrays.asList(historyEntries));
            for (SearchHistoryEntry entry : searchHistory) {
                historyAsResults.add(SearchResult.historic(entry.term, entry.bounded));
            }
        } catch (IOException e) {
            searchHistory = new ArrayList<>();
            OpenMineMap.LOGGER.error("Error while reading search history, history will not be loaded: ");
            e.printStackTrace();
        }
    }

    public static void writeToFile() {
        Gson gson = new Gson();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(histoyFile));
            writer.write(gson.toJson(searchHistory.toArray()));
            writer.close();
        } catch (IOException | JsonParseException e) {
            OpenMineMap.LOGGER.error("Unable to write history to searchHistory.json: ");
            e.printStackTrace();
        }
    }

    public static ArrayList<SearchResult> getHistoryAsResults() {
        return (ArrayList<SearchResult>) historyAsResults.clone();
    }

    public static SearchResult[] getResultsOf(SearchResult historicResult) {
        int i = 0;
        for (SearchResult result : historyAsResults) {
            if (result == historicResult) {
                SearchResult[] results = searchHistory.get(i).results;
                SearchResult[] returnArray = new SearchResult[SearchBoxLayer.MAX_RESULTS];
                System.arraycopy(results, 0, returnArray, 0, results.length);
                return returnArray;
            }
            i++;
        }
        return getErrorResult();
    }

    private static SearchResult[] getErrorResult() {
        return (new SearchResult[] {
                new SearchResult(
                        SearchResultType.LOCATION,
                        Double.NaN,
                        Double.NaN,
                        false,
                        "",
                        Component.translatable("omm.notification.something-wrong").getString(),
                        0
                ),
                null, null, null, null, null, null, null
        });
    }

}

class SearchHistoryEntry {
    final String term;
    final boolean bounded;
    final SearchResult[] results;

    public SearchHistoryEntry(String term, SearchResult[] results, boolean bounded) {
        this.term = term;
        this.results = results;
        this.bounded = bounded;
    }
}