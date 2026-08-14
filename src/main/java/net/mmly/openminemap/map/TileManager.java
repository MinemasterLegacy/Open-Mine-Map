package net.mmly.openminemap.map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.OverlayVisibility;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.http.MapType;
import net.mmly.openminemap.http.RequestManager;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.raster.LayerType;
import net.mmly.openminemap.util.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TileManager {

    private static MinecraftClient client = MinecraftClient.getInstance();
    private static final HashMap<TileUrl, HashMap<String, Identifier>> rasterTiles = new HashMap<>();
    public static boolean doArtificialZoom;
    public static boolean doReverseScroll;
    public static double mouseZoomStrength;
    public static OverlayVisibility showPlayers;
    public static OverlayVisibility showDirectionIndicators;
    private static int themeColor = 0xFF808080;
    public static boolean oldFilesDetected = false;
    public static String cacheName;
    static LinkedList<LoadableTile> tileLoadQueue = new LinkedList<>();
    static LinkedList<RegisterableTile> tileRegisteringQueue = new LinkedList<>();

    public static int getThemeColor() {
        return themeColor;
    }

    public static void setThemeColor(int color) {
        themeColor = color;
        HudMap.map.setBackgroundColor(ColorUtil.darken(color, 0.0625));
    }

    /// Add to the raster tile map any in-use rasters that do not exist, and purge any that are no longer in use
    public static void refreshRasterTileMap() {
        for (Iterator<TileUrl> iterator = rasterTiles.keySet().iterator(); iterator.hasNext();) {
            TileUrl raster = iterator.next();
            if (!RasterProvider.rasterInUse(raster)) {
                purgeRasterTileData(raster);
                rasterTiles.remove(raster);
                RequestManager.popRequester(raster);
            }
        }
        for (TileUrl raster : RasterProvider.getCurrentOverlays()) {
            if (raster.layerType == LayerType.LOCAL_GEN) continue;
            rasterTiles.putIfAbsent(raster, new HashMap<>());
            RequestManager.pushRequester(raster);
        }
        rasterTiles.putIfAbsent(RasterProvider.getCurrentBaseRaster(), new HashMap<>());
        RequestManager.pushRequester(RasterProvider.getCurrentBaseRaster());
    }

    private static void purgeRasterTileData(TileUrl raster) {
        for (Identifier identifier : rasterTiles.get(raster).values()) {
            client.getTextureManager().destroyTexture(identifier);
        }
    }

    /// Get a file from the openminemap directory.
    /// The file path will be appended to the end of the openminemap directory:
    ///
    /// .../.minecraft/openminemap/ + filePath
    public static File fileFromOmmDir(String filePath) {
        return new File(System.getProperty("user.dir") + "/openminemap/" + filePath);
    }

    public static String getRootFile() { //returns directory of .minecraft (or equivalent folder)
        return System.getProperty("user.dir") + File.separator;
    }

    public static Identifier getErrorIdentifier(LayerType layerType) { //tile used when there was an error getting an expected tile
        if (layerType == LayerType.BASE) return Identifier.of("openminemap", "errortile.png");
        return Identifier.of("openminemap", "errortileoverlay.png");
    }

    public static Identifier getBlankIdentifier() { //tile used for out of bounds tiles
        return Identifier.of("openminemap", "blanktile.png");
    }

    public static Identifier getLoadingIdentifier(LayerType layerType) { //tile used for currently loading tiles
        if (layerType == LayerType.BASE) return Identifier.of("openminemap", "loadingtile.png");
        return Identifier.of("openminemap", "loadingtileoverlay.png");
    }

    public static Identifier getLoadingIdentifier() { //tile used for currently loading tiles
        return getLoadingIdentifier(LayerType.BASE);
    }

    private static void registerQueue() {
        //System.out.println("Register Queue:"+TileLoaderManager.tileRegisteringQueue.size());
        //System.out.println("Dy Length: "+dyLoadedTiles.size());
        for (int i = 0; i < tileRegisteringQueue.size(); i++) {
            RegisterableTile tile;
            try {
                tile = tileRegisteringQueue.getFirst();
            } catch (NoSuchElementException e) {
                return;
            }
            try {
                if (!rasterTiles.containsKey(tile.raster)) throw new IOException();
                //System.out.println("Registering Tile: " + tile.key);
                NativeImage nImage = NativeImage.read(tile.image);
                //register new dynamic texture and store it again to be referenced later
                rasterTiles.get(tile.raster).remove(tile.identifierString);
                Identifier identifier = Identifier.of("openminemap-tile", tile.identifierString);
                client.getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(nImage));
                rasterTiles.get(tile.raster).put(tile.identifierString, identifier);
                //System.out.println("New Dynamic tile");

                tile.image.close();
                nImage.close();
            } catch (IOException ignored) {

            } finally {
                tileRegisteringQueue.removeFirst();
            }

        }
    }

    public static DrawableMapTile[][] getRangeOfDrawableTiles(int mapPosX, int mapPosY, int mapZoom, int tileRenderSize, int renderAreaWidth, int renderAreaHeight, MapType mapType, TileUrl raster) {
        /*  mapTileXY: the map coorinates of the center of the screen | map coordinate range is 128 * 2^(zoom+1)
         *  mapZoom: the zoom level of the map
         *  windowHeightXY: [scaled] height and width of window
         *  tileRenderSize: the size of each tile, usually 128 but can change with artificial zoom */

        registerQueue();
        //OldRequestManager.setMapCenter(mapPosX, mapPosY, isHudMap);
        RequestManager.clearCandidateTile();

        int leftBorder = (-renderAreaWidth / 2) + mapPosX;
        int rightBorder = (renderAreaWidth / 2) - mapPosX;
        int topBorder = (-renderAreaHeight / 2) + mapPosY;
        int bottomBorder = (renderAreaHeight / 2) - mapPosY;

        int firstTileX = (int) Math.floor((double) leftBorder / tileRenderSize);
        int firstTileY = (int) Math.floor((double) topBorder / tileRenderSize);

        int tileCountX = (int) Math.ceil((double) renderAreaWidth / tileRenderSize) + 1;
        int tileCountY = (int) Math.ceil((double) renderAreaHeight / tileRenderSize) + 1;

        DrawableMapTile[][] tiles = new DrawableMapTile[tileCountX][tileCountY];
        for (int j = 0; j < tileCountX; j++) {
            for (int k = 0; k < tileCountY; k++) {
                tiles[j][k] = /*new DrawableMapTile(*/
                    getDrawableTile(firstTileX + j, firstTileY + k, mapZoom, tileRenderSize, mapType, raster);
            }
        }

        RequestManager.pushTileRequest(mapType, raster);
        if (!tileLoadQueue.isEmpty()) {
            new TileLoader(tileLoadQueue.toArray(new LoadableTile[0])).start();
            tileLoadQueue.clear();
        }
        return tiles;
    }

    public static boolean isTileOutOfBounds(int x, int y, int zoom) { //checks if a given tile is out of bounds
        return (x < 0 || y < 0 || x > Math.pow(2, zoom) - 1 || y > Math.pow(2, zoom) - 1);
    }

    public static void createOpenminemapDir() {
        try { // create or open the base openminemap file for caching
            File ommDirectory = new File(TileManager.getRootFile() + "openminemap");
            if (ommDirectory.mkdir()) { //if directory does not exist
                OpenMineMap.LOGGER.info("Created openminemap directory at " + ommDirectory.getAbsolutePath());
            } else { //if directory does exist
                //System.out.println(Text.literal("OMM Directory Exists: " + ommDirectory.getAbsolutePath()));
            }
        } catch (Exception e) {
            OpenMineMap.LOGGER.error("Could not create openminemap directory: " + e.getMessage());
        }

        /*
        for (int i = 0; i < 19; i++) { //create subdirectories for osm zoom levels 0-18
            try {
                File cacheDirectory = new File(TileManager.getRootFile() + "openminemap/" + i);
                if (cacheDirectory.mkdir()) { //if directory does not exist
                    System.out.println("OMM Directory Creation Success: " + cacheDirectory.getAbsolutePath());
                } else { //if directory does exist
                    //System.out.println(Text.literal("OMM Directory Exists: " + cacheDirectory.getAbsolutePath()));
                }
            } catch (Exception e) {
                System.out.println(Text.literal("OMM Directory Error: " + e));
            }
        }
         */

        for (int i = 0; i < 19; i++) { //check for old directories that can be deleted (files 0-18)
            if (Files.exists(Path.of(TileManager.getRootFile() + "openminemap/" + i))) {
                oldFilesDetected = true;
            }
        }
    }

    public static void establishRasterDirectory(TileUrl raster) {
        //purgeOldFiles();
        cacheName = raster.name;
        try { // create or open the base openminemap file for caching
            File cacheDirectory = new File(TileManager.getRootFile() + "openminemap/"+cacheName+"/");
            if (cacheDirectory.mkdir()) { //if directory does not exist
                OpenMineMap.LOGGER.info("Created tile cache directory at " + cacheDirectory.getAbsolutePath());
            } else { //if directory does exist
                //System.out.println(Text.literal("Cache Directory Exists: " + cacheDirectory.getAbsolutePath()));
            }
        } catch (Exception e) {
            OpenMineMap.LOGGER.error("Could not create tile cache directory: " + e.getMessage());
        }
        for (int i = 0; i < 19; i++) { //create subdirectories for osm zoom levels 0-18
            try {
                File zoomDirectory = new File(TileManager.getRootFile() + "openminemap/" + cacheName + "/" + i);
                if (zoomDirectory.mkdir()) { //if directory does not exist
                    OpenMineMap.LOGGER.info("Created tile cache subdirectory at " + zoomDirectory.getAbsolutePath());
                } else { //if directory does exist
                    //System.out.println(Text.literal("Zoom Directory Exists: " + zoomDirectory.getAbsolutePath()));
                }
            } catch (Exception e) {
                OpenMineMap.LOGGER.error("Could not create tile cache subdirectory: " + e.getMessage());
            }
        }
        TileLoader.resetCacheSize();
        //System.out.println("Clearing loaded tiles...");
    }

    public static boolean renameRasterDirectory(String oldDirName, String newDirName) {
        File oldDir = fileFromOmmDir(oldDirName);
        File newDir = fileFromOmmDir(newDirName);

        if (!oldDir.exists()) {
            OpenMineMap.LOGGER.error("Could not rename directory \"" + oldDirName + "\" to \"" + newDirName + "\": Old directory does not exist.");
            return false;
        }
        if (newDir.exists()) {
            OpenMineMap.LOGGER.error("Could not rename directory \"" + oldDirName + "\" to \"" + newDirName + "\": New directory already exists.");
            return false;
        }

        boolean success = oldDir.renameTo(newDir);
        if (!success) OpenMineMap.LOGGER.error("Could not rename directory \"" + oldDirName + "\" to \"" + newDirName + "\": Renaming faled.");
        return success;
    }

    public static void loadTopTile() {
        getDrawableTile(0, 0, 0, 128, RequestManager.currentPriorityMapType(), RasterProvider.getCurrentBaseRaster());
    }

    private static DrawableMapTile getDrawableTile(int tileX, int tileY, int mapZoom, int tileRenderSize, MapType mapType, TileUrl raster) {
        //tileXY do not refer to their pixel positions, they refer to their tile grid positions
        if (!rasterTiles.containsKey(raster)) { //if requested raster is not currently in use
            return new DrawableMapTile(
                    getErrorIdentifier(raster.layerType),
                    tileX * tileRenderSize,
                    tileY * tileRenderSize,
                    tileRenderSize
            );
        }

        try {
            String thisKey = getKey(mapZoom, tileX, tileY, raster.identifierString);

            //if tile is out of bounds of the possible tile spaces, return blank
            if (isTileOutOfBounds(tileX, tileY, mapZoom)) return new DrawableMapTile(
                    getBlankIdentifier(),
                    tileX * tileRenderSize,
                    tileY * tileRenderSize,
                    tileRenderSize
            );

            //If tile is loaded to memory
            if (rasterTiles.get(raster).containsKey(thisKey)) {
                if (!rasterTiles.get(raster).get(thisKey).equals(getLoadingIdentifier(raster.layerType))) return new DrawableMapTile(
                        rasterTiles.get(raster).get(thisKey),
                        tileX * tileRenderSize,
                        tileY * tileRenderSize,
                        tileRenderSize
                );
            }

            if (
                //try to load image from cache; return true when it should be loaded from the raster provider
                registerDynamicIdentifier(tileX, tileY, mapZoom, raster)
            ) {
                RequestManager.considerTile(tileX, tileY, mapZoom, tileRenderSize, mapType, raster);
            }

            int zoomToTry = mapZoom - 1;
            int xToTry = (tileX / 2);
            int yToTry = (tileY / 2);
            String keyToTry = getKey(zoomToTry, xToTry, yToTry, raster.identifierString);
            boolean foundTile = false;

            //if a higher tile is loaded
            while (zoomToTry >= 0) {
                if (rasterTiles.get(raster).containsKey(keyToTry)) {
                    if (!rasterTiles.get(raster).get(keyToTry).equals(getLoadingIdentifier(raster.layerType))) {
                        foundTile = true;
                        break;
                    }
                }
                zoomToTry -= 1;
                xToTry /= 2;
                yToTry /= 2;
                keyToTry = getKey(zoomToTry, xToTry, yToTry, raster.identifierString);
            }

            if (foundTile && mapZoom - zoomToTry < 8) {
                int subX = tileX % (int) Math.pow(2, (mapZoom - zoomToTry));
                int subY = tileY % (int) Math.pow(2, (mapZoom - zoomToTry));
                return new DrawableMapTile(
                        rasterTiles.get(raster).get(keyToTry),
                        tileX * tileRenderSize,
                        tileY * tileRenderSize,
                        tileRenderSize,
                        subX,
                        subY,
                        tileRenderSize / Math.pow(2, mapZoom - zoomToTry)
                );
            } else {
                return new DrawableMapTile(
                        getLoadingIdentifier(raster.layerType),
                        tileX * tileRenderSize,
                        tileY * tileRenderSize,
                        tileRenderSize
                );
            }

            //throw new Exception(); //to trigger the catch code and return an error tile

        } catch (Exception e) {
            OpenMineMap.LOGGER.warn("Error while getting tile: " + e);
            e.printStackTrace();
            return new DrawableMapTile(
                getErrorIdentifier(raster.layerType),
                tileX * tileRenderSize,
                tileY * tileRenderSize,
                tileRenderSize
            );
        }
    }

    public static String getKey(int mapZoom, int tileX, int tileY, String rasterIdentifierString) {
        return rasterIdentifierString + "-" + mapZoom + "-" + tileX + "-" + tileY;
        //return Arrays.toString(new int[] {mapZoom, tileX, tileY});
    }

    /// Returns: true, if the tile should be considered for loading from the raster provider
    private static boolean registerDynamicIdentifier(int tileX, int tileY, int tileZoom, TileUrl raster) {
        RequestableTile tile = new RequestableTile(tileX, tileY, tileZoom, 0, null);
        String thisKey = getKey(tile.zoom, tile.x, tile.y, raster.identifierString);
        if (!rasterTiles.containsKey(raster)) return false;
        if (rasterTiles.get(raster).containsKey(thisKey)) return false;

        //System.out.println("will test cache for tile " + thisKey + " of raster " + raster.name);

        File f = new File(getRootFile() + "openminemap/"+raster.name+"/"+tile.zoom+"/"+tile.x+"-"+tile.y+".png");

        // If true, tile is currently being requested/written, so act as if it doesn't exist and return for now
        // a placeholder will be determined by the code that follows this method call
        if (tile.sameTileAs(RequestManager.getPendingRequest(raster))) return false;

        //If file exists in cache, queue it for loading and add a placeholder to the tiles map
        if (f.exists()) {
            tileLoadQueue.addLast(new LoadableTile(tile.x, tile.y, tile.zoom, raster, thisKey));
            rasterTiles.get(raster).put(thisKey, getLoadingIdentifier(raster.layerType));
            return false;
        }

        return true;
    }

    public static void initializeConfigParameters() {
        doArtificialZoom = ConfigOptions.ARTIFICIAL_ZOOM.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF);
        mouseZoomStrength = ConfigOptions.ZOOM_STRENGTH.getAsDouble();
        doReverseScroll = ConfigOptions.REVERSE_SCROLL.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF);
        showPlayers = OverlayVisibility.fromString(ConfigOptions.SHOW_PLAYERS.getAsStringFromValues(ConfigOptions.Values.VISIBILITY));
        showDirectionIndicators = OverlayVisibility.fromString(ConfigOptions.SHOW_DIRECTION_INDICATORS.getAsStringFromValues(ConfigOptions.Values.VISIBILITY));
        MapScreen.backingColor = ColorUtil.argb((int) (ConfigOptions.INTERFACE_OPACITY.getAsDouble() * 255), 0, 0, 0);
        String textColor = ConfigOptions.TEXT_COLOR.getAsString();
        if (textColor.equals("rainbow")) MapScreen.setPlainTextColor(0xFF7f7f7f, true);
        else MapScreen.setPlainTextColor(Color.decode(textColor).getRGB(), false);
    }

    public static File lockFileOf(LoadableTile tile) {
        return new File(TileManager.getRootFile() + "openminemap/"+tile.raster.name+"/"+tile.zoom+"/"+tile.x+"-"+tile.y+".lock");
    }

}