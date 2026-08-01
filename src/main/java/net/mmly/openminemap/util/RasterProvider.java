package net.mmly.openminemap.util;

import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.raster.LayerType;

import java.util.*;

//TODO move all raster determination logic here (including from tileManager)
//TODO load/save current configuration from config file (base, overlays, opacity, visibility)

public class RasterProvider {

    /// 0 is back of the overlays, length is front
    private static ArrayList<TileUrl> presetRasters = new ArrayList<>();
    private static ArrayList<TileUrl> customRasters = new ArrayList<>();
    private static TileUrl currentBaseRaster = TileUrlFile.defaultUrl;
    private static final ArrayList<TileUrl> currentOverlays = new ArrayList<>();
    private static final HashMap<TileUrl, Float> overlayOpacities = new HashMap<>();
    private static final HashMap<TileUrl, Boolean> overlayVisibilities = new HashMap<>();
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

    public static TileUrl getPresetById(int i) {
        return presetRasters.get(i);
    }

    protected static void initWithFailedLoad() {
        setCurrentBaseRaster(TileUrlFile.defaultUrl); //set raster to the default (OpenStreetMap) url

        //clear any other loaded data; if the load failed, it should not be used
        presetRasters.clear();
        customRasters.clear();
        currentOverlays.clear();

        presetRasters.add(TileUrlFile.defaultUrl);
    }

    protected static void finishInitialization() {
        readConfiguration();
    }

    public static void readConfiguration() {
        determineBaseRaster();

        String[] configValue = ConfigOptions.RASTER_OVERLAYS.getAsString().split(",");
        for (String overlayName : configValue) {
            if (overlayName.equals(TileUrl.generatedLayerUrl.name)) {
                currentOverlays.add(TileUrl.generatedLayerUrl);
                continue;
            }
            for (TileUrl overlay : customRasters) {
                if (overlay.layerType == LayerType.BASE) continue;
                if (overlay.name.equals(overlayName)) {
                    currentOverlays.add(overlay);
                    TileManager.establishRasterDirectory(overlay);
                    break;
                }
            }
        }

        if (!currentOverlays.contains(TileUrl.generatedLayerUrl)) {
            currentOverlays.add(TileUrl.generatedLayerUrl);
        }

        TileManager.refreshRasterTileMap();

        configValue = ConfigOptions.RASTER_VISIBILITIES.getAsString().split(",");
        ArrayList<String> values = new ArrayList<>(List.of(configValue));
        values.remove("");
        if (values.size() < currentOverlays.size()) {
            OpenMineMap.LOGGER.warn("Mismatched number of raster visibility settings (not enough), appending extra to match");
            while (values.size() != currentOverlays.size()) {
                values.addLast("true");
            }
        }
        else if (values.size() > currentOverlays.size()) {
            OpenMineMap.LOGGER.warn("Mismatched number of raster visibility settings (too much), extra values will be ignored");
        }
        int i = 0;
        for (TileUrl url : currentOverlays) {
            //if (url.layerType == LayerType.LOCAL_GEN) continue;
            overlayVisibilities.put(url, Boolean.parseBoolean(values.get(i)));
            i++;
        }

        configValue = ConfigOptions.RASTER_OPACITIES.getAsString().split(",");
        values = new ArrayList<>(List.of(configValue));
        values.remove("");
        if (values.size() < currentOverlays.size()) {
            OpenMineMap.LOGGER.warn("Mismatched number of raster opacity settings (not enough), appending extra to match");
            while (values.size() != currentOverlays.size()) {
                values.addLast("1.0");
            }
        }
        else if (values.size() > currentOverlays.size()) {
            OpenMineMap.LOGGER.warn("Mismatched number of raster opacity settings (too much), extra values will be ignored");
        }
        i = 0;
        for (TileUrl url : currentOverlays) {
            //if (url.layerType == LayerType.LOCAL_GEN) continue;
            try {
                overlayOpacities.put(url, Float.parseFloat(values.get(i)));
            } catch (NumberFormatException e) {
                OpenMineMap.LOGGER.warn("Opacity setting for raster \"" + url.name + "\" was unparseable, defaulting to 1");
            }
            i++;
        }


    }

    public static void writeOverlayInfo() {
        if (TileUrlFile.loadFailed) return;
        String overlays = "";
        String visibilities = "";
        String opacities = "";
        for (TileUrl overlay : currentOverlays) {
            overlays += overlay.name + ",";
            visibilities += getVisibilityOf(overlay) + ",";
            opacities += getOpacityOf(overlay) + ",";
        }
        ConfigFile.writeParameter(ConfigOptions.RASTER_OVERLAYS, overlays);
        ConfigFile.writeParameter(ConfigOptions.RASTER_VISIBILITIES, visibilities);
        ConfigFile.writeParameter(ConfigOptions.RASTER_OPACITIES, opacities);
    }

    private static void determineBaseRaster() {
        String configRaster = ConfigOptions.TILE_MAP_URL.getAsString();

        for (TileUrl url : presetRasters) {
            if (url.name.equals(configRaster)) {
                setCurrentBaseRaster(url);
                return;
            }
        }
        for (TileUrl url : customRasters) {
            if (url.name.equals(configRaster)) {
                setCurrentBaseRaster(url);
                return;
            }
        }

        currentBaseRaster = TileUrlFile.defaultUrl;
        OpenMineMap.LOGGER.warn("Could not find base raster provider \"" + configRaster + "\", reverting to default.");
    }

    public static ArrayList<TileUrl> getPresetRasters() {
        return presetRasters;
    }

    public static ArrayList<TileUrl> getCustomRasters() {
        return customRasters;
    }

    public static TileUrl getCurrentBaseRaster() {
        return currentBaseRaster;
    }

    //todo attribution for overlays
    public static void setCurrentBaseRaster(TileUrl raster) {
        currentBaseRaster = raster;
        TileManager.establishRasterDirectory(currentBaseRaster);
        doMapboxAttribution = raster.source_url.contains("mapbox.com");
        ConfigFile.writeParameter(ConfigOptions.TILE_MAP_URL, raster.name);
        TileManager.setThemeColor(0xFF808080);
        TileManager.refreshRasterTileMap();
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

    public static void setOpacityOf(TileUrl raster, float opacity) {
        if (!raster.isOverlay()) return;
        overlayOpacities.put(raster, opacity);
    }

    public static float getOpacityOf(TileUrl raster) {
        if (!raster.isOverlay()) return 1f;
        if (!overlayOpacities.containsKey(raster)) return 1f;
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

    public static void pushOverlayOnTop(TileUrl raster) {
        if (!raster.isOverlay()) return;
        if (currentOverlays.contains(raster)) return;
        currentOverlays.addLast(raster);
        overlayVisibilities.put(raster, true);
        overlayOpacities.put(raster, 1f);
        TileManager.establishRasterDirectory(raster);
        TileManager.refreshRasterTileMap();
    }

    public static void popOverlay(TileUrl url) {
        currentOverlays.remove(url);
        overlayVisibilities.remove(url);
        overlayOpacities.remove(url);
        TileManager.refreshRasterTileMap();
    }

    public static void addCustomRaster(TileUrl raster) {
        customRasters.add(raster);
    }

    public void saveSettings(boolean writeToFile) {
        StringBuilder visibilities = new StringBuilder();
        StringBuilder opacities = new StringBuilder();
        for (TileUrl overlay : currentOverlays) {
            visibilities.append(getVisibilityOf(overlay)).append(",");
            opacities.append(getOpacityOf(overlay)).append(",");
        }
        if (!visibilities.isEmpty()) visibilities.deleteCharAt(visibilities.length() - 1);
        if (!opacities.isEmpty()) opacities.deleteCharAt(visibilities.length() - 1);
        ConfigOptions.RASTER_OPACITIES.write(opacities.toString());
        ConfigOptions.RASTER_VISIBILITIES.write(visibilities.toString());
        if (writeToFile) ConfigFile.writeToFile();
    }

    public static boolean rasterInUse(TileUrl raster) {
        if (currentBaseRaster == raster) return true;
        return currentOverlays.contains(raster);
    }

}
