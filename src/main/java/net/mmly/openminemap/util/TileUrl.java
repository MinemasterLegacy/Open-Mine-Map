package net.mmly.openminemap.util;

import net.mmly.openminemap.raster.LayerType;

public class TileUrl {
    public final String source_url;
    public final String attribution;
    public final String name;
    public final String[] attribution_links;
    public final LayerType layerType;
    public final int presetID;
    //TODO check if preset when attempting to load from

    public TileUrl(String name, String source_url, String attribution, String[] attribution_links, String layerType) {
        this(name, source_url, attribution, attribution_links, typeFromString(layerType));
    }

    public TileUrl(String name, String source_url, String attribution, String[] attribution_links, LayerType layerType) {
        this.name = name;
        this.attribution = attribution;
        this.attribution_links = attribution_links;
        this.source_url = source_url;
        this.layerType = LayerType.BASE;
        presetID = -1;
    }

    public TileUrl(int templateID, String token) {
        this.name = "---";
        this.source_url = "https://a.com/{x}/{y}/{z}";
        this.attribution = "{e}";
        this.attribution_links = new String[] {"https://e.e.com"};
        this.layerType = LayerType.BASE;
        presetID = -1;
    }

    public TileUrl(int templateId, String name, String source_url, String attribution, String[] attribution_links, String layerType) {
        this.name = name;
        this.attribution = attribution;
        this.attribution_links = attribution_links;
        this.source_url = source_url;
        this.layerType = LayerType.BASE; //TODO will have to be changed if overlay presets are added
        presetID = templateId;
    }

    public boolean isPreset() {
        return presetID >= 0;
    }

    private static LayerType typeFromString(String layerTypeString) {
        return switch (layerTypeString) {
            case "base" -> LayerType.BASE;
            case "overlay" -> LayerType.OVERLAY;
            default -> LayerType.OVERLAY;
        };
    }


}