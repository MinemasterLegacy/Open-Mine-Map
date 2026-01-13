package net.mmly.openminemap.util;

import net.mmly.openminemap.enums.SearchResultType;

public class SearchResult {

    SearchResultType resultType;
    double longitude;
    double latitude;
    String name;
    String context;

    public SearchResult(SearchResultType type, double latitude, double longitude, String name, String context) {
        this(type, latitude, longitude, name);
        this.context = context;
    }

    public SearchResult(SearchResultType type, double latitude, double longitude, String name) {
        this.resultType = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.context = "";
    }
}
