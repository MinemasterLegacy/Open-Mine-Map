package net.mmly.openminemap.util;

public class TileUrl {
    public String source_url;
    public String attribution;
    public String name;
    public String[] attribution_links;

    public TileUrl(String name, String source_url, String attribution, String[] attribution_links) {
        this.name = name;
        this.source_url = source_url;
        this.attribution = attribution;
        this.attribution_links = attribution_links;
    }
}