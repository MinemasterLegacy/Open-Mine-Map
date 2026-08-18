package net.mmly.openminemap.http;

import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.search.SearchBoxLayer;
import net.mmly.openminemap.search.SearchHistoryFile;
import net.mmly.openminemap.search.SearchResult;
import net.mmly.openminemap.util.NamedLocation;

import java.io.InputStream;

public class SearchRequester extends LocationJsonRequester<NamedLocation> {
    public SearchRequester() {
        super("Search");
    }

    @Override
    public void request() {
        SearchResult[] searchResults = searchResultRequest(pendingRequest.name, pendingRequest.latitude, pendingRequest.longitude);
        if (searchResults == null) {
            RequestManager.searchResults = getErrorResult();
        } else {
            RequestManager.searchResults = searchResults;
            SearchHistoryFile.addHistoricResult(pendingRequest.name, searchResults, !Double.isNaN(pendingRequest.latitude));
        }
    }

    SearchResult[] searchResultRequest(String query, double latFocus, double lonFocus) {
        String urlPattern = "https://photon.komoot.io/api/?q=" + query.replaceAll("[^a-zA-Z0-9 ]", "").replaceAll(" ", "+") + "&limit=" + SearchBoxLayer.MAX_RESULTS;
        if (!OmmMap.geoCoordsOutOfBounds(latFocus, lonFocus) && !Double.isNaN(latFocus) && !Double.isNaN(lonFocus)) {
            urlPattern += "&lat=" + latFocus + "&lon=" + lonFocus;
        }

        InputStream stream = get(urlPattern);
        if (stream == null) return null;

        SearchResult[] results = parseLocationJson(stream);
        if (results == null) return null;

        if (results.length == 0) results = getBlankResult();
        else if (results[0] == null) results = getBlankResult();

        return results;
    }

}
