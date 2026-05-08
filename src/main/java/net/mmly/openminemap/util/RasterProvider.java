package net.mmly.openminemap.util;

import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.raster.LayerType;

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

    protected static void initPresetRasters(TileUrl[] presetRasters) {
        RasterProvider.presetRasters = new ArrayList<>(Arrays.stream(presetRasters).toList());
    }

    protected static void initCustomRasters(TileUrl[] customRasters) {
        RasterProvider.customRasters = new ArrayList<>(Arrays.stream(customRasters).toList());
    }

    protected static void initWithFailedLoad() {

    }

    protected static void finishInitialization() {
        TileManager.setTileUrl(currentRaster, true); //TODO temp, read config instead
    }

    public static void readConfiguration() {
        //TODO
    }

    public static ArrayList<TileUrl> getPresetRasters() {
        return presetRasters;
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
        return currentOverlays.indexOf(raster) == 0;
    }

    public static boolean isTopOverlay(TileUrl raster) {
        return currentOverlays.indexOf(raster) == currentOverlays.size() - 1;
    }

    private static void moveOverlayRaster(TileUrl raster, int direction) {
        if (!raster.isOverlay()) return;
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

    public static void insertOverlayOnTop(TileUrl raster) {
        if (!raster.isOverlay()) return;
        if (!currentOverlays.contains(raster)) return;
        currentOverlays.addFirst(raster);
    }

    public static void addCustomRaster(TileUrl raster) {
        customRasters.add(raster);
    }

}
