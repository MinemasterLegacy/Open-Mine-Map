package net.mmly.openminemap.util;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.raster.LayerType;

import java.util.Arrays;
import java.util.Locale;

public class TileUrl {
    public final String source_url;
    public final String attribution;
    public final String name;
    public final String identifierString;
    public final String[] attribution_links;
    public final LayerType layerType;
    public final int presetID;
    public final Identifier presetIdentifier;
    public static final TileUrl generatedLayerUrl = new TileUrl();
    //TODO check if preset when attempting to load from

    private TileUrl() {
        this.name = "Generated Overlays";
        this.identifierString = "generated-overlays";
        this.source_url = "";
        this.attribution_links = null;
        this.attribution = "";
        this.layerType = LayerType.LOCAL_GEN;
        this.presetID = -1;
        this.presetIdentifier = null;
    }

    public TileUrl(String name, String source_url, String attribution, String[] attribution_links, String layerType) {
        this(name, source_url, attribution, attribution_links, typeFromString(layerType));
    }

    public TileUrl(String name, String source_url, String attribution, String[] attribution_links, LayerType layerType) {
        this.name = name;
        this.identifierString = name == null ? "" : name.toLowerCase(Locale.US).replace(" ", "-");
        this.attribution = attribution;
        this.attribution_links = attribution_links;
        this.source_url = source_url;
        this.layerType = layerType;
        presetID = -1;
        presetIdentifier = null;
    }

    public TileUrl(int templateId, String name, String source_url, String attribution, String[] attribution_links, String layerType) {
        this.name = name;
        this.identifierString = name.toLowerCase(Locale.US).replace(" ", "-");
        this.attribution = attribution;
        this.attribution_links = attribution_links;
        this.source_url = source_url;
        this.layerType = LayerType.BASE; //TODO will have to be changed if overlay presets are added
        presetID = templateId;
        presetIdentifier = Identifier.of("openminemap", "rastertiles/" + name
                .replace("Ö", "O")
                .toLowerCase(Locale.US)
                .replace(" ", "")
                + ".png"
        );
    }

    public boolean isPreset() {
        return presetID >= 0;
    }

    public boolean hasKeyField() {
        return presetID >= 5;
    }

    private static LayerType typeFromString(String layerTypeString) {
        return switch (layerTypeString) {
            case "base" -> LayerType.BASE;
            case "overlay" -> LayerType.OVERLAY;
            default -> LayerType.OVERLAY;
        };
    }

    public boolean isOverlay() {
        return layerType == LayerType.OVERLAY;
    }

    public boolean dataIsEqual(JsonObject raster) {
        if (!raster.get("name").getAsString().equals(name)) return false;
        if (!raster.get("source_url").getAsString().equals(source_url)) return false;
        if (!raster.get("attribution").getAsString().equals(attribution)) return false;
        if (!Arrays.equals(
                TileUrlFile.arrayOf(raster.get("attribution_links").getAsJsonArray()),
                attribution_links
        )) return false;
        return true;
    }

}