package net.mmly.openminemap.util;

import net.minecraft.client.texture.Scaling;
import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.map.TileManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

//TODO move all raster determination logic here (including from tileManager)
//TODO load/save current configuration from config file (base, overlays, opacity, visibility)

public class RasterProvider {

    /// 0 is back of the overlays, length is front

    private static ArrayList<TileUrl> presetRasters = new ArrayList<>();
    private static ArrayList<TileUrl> customRasters = new ArrayList<>();
    private static TileUrl currentRaster = TileUrlFile.defaultUrl;
    private static ArrayList<TileUrl> currentOverlays = new ArrayList<>();
    private static HashMap<TileUrl, Integer> overlayOpacities = new HashMap<>();
    private static HashMap<TileUrl, Boolean> overlayVisibilities = new HashMap<>();
    private static boolean doMapboxAttribution = false;

    public static boolean doMapboxAttribution() {
        return doMapboxAttribution;
    }

    protected static void initPresetRasters(TileUrl[] presetRasterArray) {
        presetRasters = new ArrayList<>(Arrays.stream(presetRasterArray).toList());
    }

    protected static void initCustomRasters(TileUrl[] customRasterArray) {
        customRasters = new ArrayList<>(Arrays.stream(customRasterArray).toList());
        while (customRasters.contains(null)) {
            customRasters.remove(null);
        }
    }

    protected static void initWithFailedLoad() {

    }

    protected static void finishInitialization() {
        readConfiguration();
        currentOverlays.add(TileUrl.generatedLayerUrl);
        TileManager.setTileUrl(currentRaster, true); //TODO temp, read config instead
    }

    public static void readConfiguration() {
        determineBaseRaster();
        //TODO for overlay, transparency, opacity
    }

    private static void determineBaseRaster() {
        String configRaster = ConfigOptions.TILE_MAP_URL.getAsString();

        for (TileUrl url : presetRasters) {
            if (url.name.equals(configRaster)) {
                currentRaster = url;
                return;
            }
        }
        for (TileUrl url : customRasters) {
            if (url.name.equals(configRaster)) {
                currentRaster = url;
                return;
            }
        }

        currentRaster = TileUrlFile.defaultUrl;
        OpenMineMap.LOGGER.warn("Could not find base raster provider \"" + configRaster + "\", reverting to default.");
    }

    public static ArrayList<TileUrl> getPresetRasters() {
        return presetRasters;
    }

    public static ArrayList<TileUrl> getCustomBaseRasters() {
        return customRasters;
    }

    public static TileUrl getCurrentBaseRaster() {
        return currentRaster;
    }

    public static void setCurrentBaseRaster(TileUrl raster) {
        currentRaster = raster;
        doMapboxAttribution = raster.source_url.contains("mapbox.com");
    }

    public static ArrayList<TileUrl> getCurrentOverlays() {
        return currentOverlays;
    }

    public static boolean isBottomOverlay(TileUrl raster) {
        if (!currentOverlays.contains(raster)) return false;
        return currentOverlays.indexOf(raster) == 0;
    }

    public static boolean isTopOverlay(TileUrl raster) {
        if (!currentOverlays.contains(raster)) return false;
        return currentOverlays.indexOf(raster) == currentOverlays.size() - 1;
    }

    private static void moveOverlayRaster(TileUrl raster, int direction) {
        if (!currentOverlays.contains(raster)) return;
        int index = currentOverlays.indexOf(raster);
        currentOverlays.remove(raster);
        currentOverlays.add(index + direction, raster);
    }

    public static void moveForward(TileUrl raster) {
        if (isTopOverlay(raster)) return;
        moveOverlayRaster(raster, 1);
    }

    public static void moveBackwards(TileUrl raster) {
        if (isBottomOverlay(raster)) return;
        moveOverlayRaster(raster, -1);
    }

    public static void setOpacityOf(TileUrl raster, int opacity) {
        if (!raster.isOverlay()) return;
        overlayOpacities.put(raster, opacity);
    }

    public static int getOpacityOf(TileUrl raster) {
        if (!raster.isOverlay()) return 255;
        if (!overlayOpacities.containsKey(raster)) return 255;
        return overlayOpacities.get(raster);
    }

    public static boolean getVisibilityOf(TileUrl raster) {
        if (!raster.isOverlay()) return true;
        if (!overlayVisibilities.containsKey(raster)) return true;
        return overlayVisibilities.get(raster);
    }

    public static void setVisibilityOf(TileUrl raster, boolean visibility) {
        if (!raster.isOverlay()) return;
        overlayVisibilities.put(raster, visibility);
    }

    public static boolean overlayInUse(TileUrl raster) {
        return currentOverlays.contains(raster);
    }

    public static void insertOverlayOnTop(TileUrl raster) {
        if (!raster.isOverlay()) return;
        if (currentOverlays.contains(raster)) return;
        currentOverlays.addLast(raster);
    }

    public static void addCustomRaster(TileUrl raster) {
        customRasters.add(raster);
    }

    public static void extractOverlay(TileUrl url) {
        currentOverlays.remove(url);
    }

}
