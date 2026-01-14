package net.mmly.openminemap.search;

public class SearchResult {

    public SearchResultType resultType;
    public double longitude;
    public double latitude;
    public String name;
    public String context;

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
