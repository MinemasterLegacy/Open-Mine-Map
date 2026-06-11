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
    private static TileUrl currentRaster = TileUrlFile.defaultUrl;
    private static ArrayList<TileUrl> currentOverlays = new ArrayList<>();
    private static HashMap<TileUrl, Float> overlayOpacities = new HashMap<>();
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

    public static TileUrl getPresetById(int i) {
        return presetRasters.get(i);
    }

    protected static void initWithFailedLoad() {

    }

    protected static void finishInitialization() {
        currentOverlays.add(TileUrl.generatedLayerUrl);
        readConfiguration();
        TileManager.setTileUrl(currentRaster, true); //TODO temp, read config instead
    }

    public static void readConfiguration() {
        determineBaseRaster();

        String[] configValue = ConfigOptions.RASTER_VISIBILITIES.getAsString().split(",");
        ArrayList<String> values = new ArrayList<>(List.of(configValue));
        values.remove("");
        if (values.size() < currentOverlays.size() - 1) {
            OpenMineMap.LOGGER.warn("Mismatched number of raster visibility settings (not enough), appending extra to match");
            while (values.size() != currentOverlays.size() - 1) {
                values.addLast("true");
            }
        }
        else if (values.size() > currentOverlays.size() - 1) {
            OpenMineMap.LOGGER.warn("Mismatched number of raster visibility settings (too much), extra values will be ignored");
        }
        int i = 1;
        for (TileUrl url : currentOverlays) {
            if (url.layerType == LayerType.LOCAL_GEN) continue;
            overlayVisibilities.put(url, Boolean.parseBoolean(values.get(i)));
            i++;
        }

        configValue = ConfigOptions.RASTER_OPACITIES.getAsString().split(",");
        values = new ArrayList<>(List.of(configValue));
        values.remove("");
        if (values.size() < currentOverlays.size() - 1) {
            OpenMineMap.LOGGER.warn("Mismatched number of raster opacity settings (not enough), appending extra to match");
            while (values.size() != currentOverlays.size() - 1) {
                values.addLast("1.0");
            }
        }
        else if (values.size() > currentOverlays.size() - 1) {
            OpenMineMap.LOGGER.warn("Mismatched number of raster opacity settings (too much), extra values will be ignored");
        }
        i = 1;
        for (TileUrl url : currentOverlays) {
            if (url.layerType == LayerType.LOCAL_GEN) continue;
            try {
                overlayOpacities.put(url, Float.parseFloat(values.get(i)));
            } catch (NumberFormatException e) {
                OpenMineMap.LOGGER.warn("Opacity setting for raster \"" + url.name + "\" was unparseable, defaulting to 1");
            }
            i++;
        }


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

    public static ArrayList<TileUrl> getCustomRasters() {
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

    public static void insertOverlayOnTop(TileUrl raster) {
        if (!raster.isOverlay()) return;
        if (currentOverlays.contains(raster)) return;
        currentOverlays.addLast(raster);
        overlayVisibilities.put(raster, true);
        overlayOpacities.put(raster, 1f);
    }

    public static void extractOverlay(TileUrl url) {
        currentOverlays.remove(url);
        overlayVisibilities.remove(url);
        overlayOpacities.remove(url);
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

}
