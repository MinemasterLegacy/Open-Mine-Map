package net.mmly.openminemap.map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.OverlayVisibility;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.DrawableMapTile;
import net.mmly.openminemap.util.TileUrlFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

public class TileManager {

    private static MinecraftClient mc = MinecraftClient.getInstance();
    protected static HashMap<String, Identifier> dyLoadedTiles = new HashMap<>();
    public static boolean doArtificialZoom;
    public static boolean doReverseScroll;
    public static double mouseZoomStrength;
    public static OverlayVisibility showPlayers;
    public static OverlayVisibility showDirectionIndicators;
    public static int themeColor = 0xFF808080;
    public static boolean oldFilesDetected = false;
    public static String cacheName;
    static LinkedList<LoadableTile> tileLoadQueue = new LinkedList<>();
    static LinkedList<RegisterableTile> tileRegisteringQueue = new LinkedList<>();

    public static String getRootFile() { //returns directory of .minecraft (or equivalent folder)
        return System.getProperty("user.dir") + File.separator;
    }

    public static Identifier getErrorIdentifier() { //tile used when there was an error getting an expected tile
        return Identifier.of("openminemap", "errortile.png");
    }

    public static Identifier getBlankIdentifier() { //tile used for out of bounds tiles
        return Identifier.of("openminemap", "blanktile.png");
    }

    public static Identifier getLoadingIdentifier() { //tile used for out of bounds tiles
        return Identifier.of("openminemap", "loadingtile.png");
    }

    public static int[] getTopLeftData() {
        return new int[] {leftMostX, topMostY};
    }

    private static int leftMostX;
    private static int topMostY;

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
                //System.out.println("Registering Tile: " + tile.key);
                NativeImage nImage = NativeImage.read(tile.image);
                //register new dynamic texture and store it again to be referenced later
                dyLoadedTiles.remove(tile.key);
                Identifier identifier = Identifier.of("openminemap-tile", tile.key);
                mc.getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(new nameSupplier(), nImage));
                dyLoadedTiles.put(tile.key, identifier);
                //System.out.println("New Dynamic tile");

                tile.image.close();
                nImage.close();
            } catch (IOException ignored) {

            } finally {
                tileRegisteringQueue.removeFirst();
            }

        }
    }

    public static DrawableMapTile[][] getRangeOfDrawableTiles(int mapPosX, int mapPosY, int mapZoom, int tileRenderSize, int renderAreaWidth, int renderAreaHeight, boolean isHudMap) {
        /*  mapTileXY: the map coorinates of the center of the screen | map coordinate range is 128 * 2^(zoom+1)
         *  mapZoom: the zoom level of the map
         *  windowHeightXY: [scaled] height and width of window
         *  tileRenderSize: the size of each tile, usually 128 but can change with artificial zoom */

        registerQueue();
        RequestManager.setMapCenter(mapPosX, mapPosY, isHudMap);
        RequestManager.resetCandidate();

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
                        getDrawableTile(firstTileX + j, firstTileY + k, mapZoom, tileRenderSize, isHudMap);
            }
        }

        RequestManager.pushRequest(isHudMap);
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
                System.out.println("OMM Directory Creation Success: " + ommDirectory.getAbsolutePath());
            } else { //if directory does exist
                //System.out.println(Text.literal("OMM Directory Exists: " + ommDirectory.getAbsolutePath()));
            }
        } catch (Exception e) {
            System.out.println(Text.literal("OMM Directory Error: " + e));
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

    public static void setCacheDir() {
        //purgeOldFiles();
        cacheName = TileUrlFile.getCurrentUrl().name;
        try { // create or open the base openminemap file for caching
            File cacheDirectory = new File(TileManager.getRootFile() + "openminemap/"+cacheName+"/");
            if (cacheDirectory.mkdir()) { //if directory does not exist
                System.out.println("Cache Directory Creation Success: " + cacheDirectory.getAbsolutePath());
            } else { //if directory does exist
                //System.out.println(Text.literal("Cache Directory Exists: " + cacheDirectory.getAbsolutePath()));
            }
        } catch (Exception e) {
            System.out.println(Text.literal("Cache Directory Error: " + e));
        }
        for (int i = 0; i < 19; i++) { //create subdirectories for osm zoom levels 0-18
            try {
                File zoomDirectory = new File(TileManager.getRootFile() + "openminemap/" + cacheName + "/" + i);
                if (zoomDirectory.mkdir()) { //if directory does not exist
                    System.out.println("Zoom Directory Creation Success: " + zoomDirectory.getAbsolutePath());
                } else { //if directory does exist
                    //System.out.println(Text.literal("Zoom Directory Exists: " + zoomDirectory.getAbsolutePath()));
                }
            } catch (Exception e) {
                System.out.println(Text.literal("Zoom Directory Error: " + e));
            }
        }
        //System.out.println("Clearing loaded tiles...");
        TextureManager tm = MinecraftClient.getInstance().getTextureManager();
        for (Identifier tile : dyLoadedTiles.values()) {
            tm.destroyTexture(tile);
        }
        dyLoadedTiles.clear();
    }

    public static void purgeOldFiles() {
        File cacheDirectory = new File(TileManager.getRootFile() + "openminemap");
        for (File f : cacheDirectory.listFiles()) {
            if (isPurgeable(f.getName())) {
                for (File file : f.listFiles()) {
                    file.delete();
                }
                if (f.delete()) {
                    System.out.println("Success purging file/directory: "+f.getName());
                } else {
                    System.out.println("Error purging file/directory: "+f.getName());
                }
            }
        }
    }

    private static final String[] toPurge = new String[] {
            "0", "1", "2",
            "3", "4", "5",
            "6", "7", "8",
            "9", "10", "11",
            "13", "12", "11",
            "12", "13", "14",
            "15", "16", "17", "18"
    };

    private static boolean isPurgeable(String fileName) {
        for (String purgeName : toPurge) {
            if (purgeName.equals(fileName)) return true;
        }
        return false;
    }

    public static void clearCacheDir() {
        try {
            File cacheDirectory = new File(TileManager.getRootFile() + "openminemap");
            for (int i = 0; i < 19; i++) {
                try {
                    cacheDirectory = new File(TileManager.getRootFile() + "openminemap/" + i);
                    for (File subfile : cacheDirectory.listFiles()) {
                        subfile.delete();
                    }
                } catch (Exception e) {
                    System.out.println(Text.literal("OMM Directory Error: " + e));
                }
            }
        } catch (Exception e) {

        }
    }

    public static void loadTopTile() {
        getDrawableTile(0, 0, 0, 128, RequestManager.hudMapIsPrimary);
    }

    private static DrawableMapTile getDrawableTile(int tileX, int tileY, int mapZoom, int tileRenderSize, boolean isHudMap) {
        //tileXY do not refer to their pixel positions, they refer to their tile grid positions
        try {
            String thisKey = getKey(mapZoom, tileX, tileY);

            //if tile is out of bounds of the possible tile spaces
            if (isTileOutOfBounds(tileX, tileY, mapZoom)) return new DrawableMapTile(
                    getBlankIdentifier(),
                    tileX * tileRenderSize,
                    tileY * tileRenderSize,
                    tileRenderSize
            );

            //If tile is loaded to memory
            if (dyLoadedTiles.containsKey(thisKey)) {
                if (!dyLoadedTiles.get(thisKey).equals(getLoadingIdentifier())) return new DrawableMapTile(
                        dyLoadedTiles.get(thisKey),
                        tileX * tileRenderSize,
                        tileY * tileRenderSize,
                        tileRenderSize
                );
            }

            //if image is found in cache
            try {
                registerDynamicIdentifier(tileX, tileY, mapZoom, cacheName);
            //else, request image from osm. Tile will be loaded eventually; for now check higher scales
            } catch (IOException e) {

                //RequestManager.trySetRequest(tileX, tileY, mapZoom);
                RequestManager.consider(tileX, tileY, mapZoom, tileRenderSize, isHudMap);
            }

            int zoomToTry = mapZoom - 1;
            int xToTry = (tileX / 2);
            int yToTry = (tileY / 2);
            String keyToTry = getKey(zoomToTry, xToTry, yToTry);
            boolean foundTile = false;

            //if a higher tile is loaded
            while (zoomToTry >= 0) {
                if (dyLoadedTiles.containsKey(keyToTry)) {
                    if (!dyLoadedTiles.get(keyToTry).equals(getLoadingIdentifier())) {
                        foundTile = true;
                        break;
                    }
                }
                zoomToTry -= 1;
                xToTry /= 2;
                yToTry /= 2;
                keyToTry = getKey(zoomToTry, xToTry, yToTry);
            }

            if (foundTile && mapZoom - zoomToTry < 8) {
                int subX = tileX % (int) Math.pow(2, (mapZoom - zoomToTry));
                int subY = tileY % (int) Math.pow(2, (mapZoom - zoomToTry));
                return new DrawableMapTile(
                        dyLoadedTiles.get(keyToTry),
                        tileX * tileRenderSize,
                        tileY * tileRenderSize,
                        tileRenderSize,
                        subX,
                        subY,
                        tileRenderSize / Math.pow(2, mapZoom - zoomToTry)
                );
            } else {
                return new DrawableMapTile(
                        getLoadingIdentifier(),
                        tileX * tileRenderSize,
                        tileY * tileRenderSize,
                        tileRenderSize
                );
            }

            //throw new Exception(); //to trigger the catch code and return an error tile

        } catch (Exception e) {
            System.out.println("Error while getting tile: " + e);
            e.printStackTrace();
            return new DrawableMapTile(
                getErrorIdentifier(),
                tileX * tileRenderSize,
                tileY * tileRenderSize,
                tileRenderSize
            );
        }
    }

    public static String getKey(int mapZoom, int tileX, int tileY) {
        return mapZoom + "-" + tileX + "-" + tileY;
        //return Arrays.toString(new int[] {mapZoom, tileX, tileY});
    }

    private static void registerDynamicIdentifier(int tileX, int tileY, int tileZoom, String cacheName) throws IOException {
        RequestableTile tile = new RequestableTile(tileX, tileY, tileZoom, 0, cacheName);
        String thisKey = getKey(tile.zoom, tile.x, tile.y);
        if (dyLoadedTiles.containsKey(thisKey)) return;

        File f = new File(getRootFile() + "openminemap/"+cacheName+"/"+tile.zoom+"/"+tile.x+"-"+tile.y+".png");
        if (tile.sameTileAs(RequestManager.pendingRequest)) {
            return; // Tile is currently being requested/written, so act as if it doesn't exist and return for now
        } else if (f.exists()) { //If file does exist, load it and register it to be used
            //System.out.println("Loading Tile: " + thisKey);
            tileLoadQueue.addLast(new LoadableTile(tile.x, tile.y, tile.zoom, cacheName, thisKey));
            dyLoadedTiles.put(thisKey, getLoadingIdentifier());
        } else if (tileZoom <= 18) {
            throw new IOException();
        }
    }

    public static void initializeConfigParameters() {
        doArtificialZoom = ConfigFile.readParameter(ConfigOptions.ARTIFICIAL_ZOOM).equals("on");
        mouseZoomStrength = Double.parseDouble(ConfigFile.readParameter(ConfigOptions.ZOOM_STRENGTH));
        doReverseScroll = ConfigFile.readParameter(ConfigOptions.REVERSE_SCROLL).equals("on");
        showPlayers = OverlayVisibility.fromString(ConfigFile.readParameter(ConfigOptions.SHOW_PLAYERS));
        showDirectionIndicators = OverlayVisibility.fromString(ConfigFile.readParameter(ConfigOptions.SHOW_DIRECTION_INDICATORS));
    }

}

class nameSupplier implements Supplier<String> {
    @Override
    public String get() {
        return "osmTileName";
    }
}