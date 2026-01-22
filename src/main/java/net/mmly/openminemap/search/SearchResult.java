package net.mmly.openminemap.search;

public class SearchResult {

    public SearchResultType resultType;
    public double longitude;
    public double latitude;
    public String name;
    public String context = "";
    public double zoom = -1;
    public boolean historic;
    public double[] bounds = null;

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name, String context, double[] bounds) {
        this(type, latitude, longitude, historic, name, context);
        this.bounds = bounds;
    }

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name, double[] bounds) {
        this(type, latitude, longitude, historic, name);
        this.bounds = bounds;
    }

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name, String context) {
        this(type, latitude, longitude, historic, name);
        this.context = context;
    }

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name, double zoom) {
        this(type, latitude, longitude, historic, name);
        this.zoom = zoom;
    }

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name, String context, double zoom) {
        this(type, latitude, longitude, historic, name, context);
        this.zoom = zoom;
    }

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name) {
        this.resultType = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.historic = historic;
    }
}
