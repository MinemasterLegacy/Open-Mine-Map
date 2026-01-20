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

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;

public class TileManager {

    private static MinecraftClient mc = MinecraftClient.getInstance();
    private static HashMap<String, Identifier> dyLoadedTiles = new HashMap<>();
    public static int tileScaledSize = 128; //should only be a power of 2
    public static int hudTileScaledSize = 128; //should only be a power of 2 (was 64 in v1.0.0)
    public static boolean doArtificialZoom;
    public static boolean doReverseScroll;
    public static double mouseZoomStrength;
    public static OverlayVisibility showPlayers;
    public static OverlayVisibility showDirectionIndicators;
    public static int themeColor = 0xFF808080;
    public static boolean oldFilesDetected = false;
    public static String cacheName;

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

    public static DrawableMapTile[][] getRangeOfDrawableTiles(int mapPosX, int mapPosY, int mapZoom, int tileRenderSize, int renderAreaWidth, int renderAreaHeight, boolean isHudMap) {
        /*  mapTileXY: the map coorinates of the center of the screen | map coordinate range is 128 * 2^(zoom+1)
         *  mapZoom: the zoom level of the map
         *  windowHeightXY: [scaled] height and width of window
         *  tileRenderSize: the size of each tile, usually 128 but can change with artificial zoom */

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
        purgeOldFiles();
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
        getDrawableTile(0, 0, 0, 128, true);
    }

    private static DrawableMapTile getDrawableTile(int tileX, int tileY, int mapZoom, int tileRenderSize, boolean isHudMap) {
        //tileXY do not refer to their pixel positions, they refer to their tile grid positions
        try {
            String thisKey = Arrays.toString(new int[] {mapZoom, tileX, tileY});

            if (isTileOutOfBounds(tileX, tileY, mapZoom)) return new DrawableMapTile( //if tile is out of bounds of the possible tile spaces
                    getBlankIdentifier(),
                    tileX * tileRenderSize,
                    tileY * tileRenderSize,
                    tileRenderSize
            );

            if (dyLoadedTiles.containsKey(thisKey)) return new DrawableMapTile(
                    dyLoadedTiles.get(thisKey),
                    tileX * tileRenderSize,
                    tileY * tileRenderSize,
                    tileRenderSize
            );

            BufferedImage osmTile = null;
            try { //if image is found in cache
                osmTile = ImageIO.read(new File(getRootFile() + "openminemap/"+cacheName+"/"+mapZoom+"/"+tileX+"-"+tileY+".png")); //get an image from /run/openminemap;
                registerDynamicIdentifier(osmTile, thisKey);
                return new DrawableMapTile(
                        dyLoadedTiles.get(thisKey),
                        tileX * tileRenderSize,
                        tileY * tileRenderSize,
                        tileRenderSize
                );
            } catch (IIOException e) {
                //else, request image from osm. Tile will be loaded eventually, but
                //RequestManager.trySetRequest(tileX, tileY, mapZoom);
                RequestManager.consider(tileX, tileY, mapZoom, tileRenderSize, isHudMap);
            }

            int zoomToTry = mapZoom - 1;
            int xToTry = (tileX / 2);
            int yToTry = (tileY / 2);
            String keyToTry = Arrays.toString(new int[] {zoomToTry, xToTry, yToTry});
            boolean foundTile = false;
            while (zoomToTry >= 0) {
                if (dyLoadedTiles.containsKey(keyToTry)) { //if a higher tile is loaded
                    foundTile = true;
                    break;
                }
                try { //if a higher tile on disk
                    osmTile = ImageIO.read(new File(getRootFile() + "openminemap/"+cacheName+"/"+zoomToTry+"/"+xToTry+"-"+yToTry+".png")); //get an image from /run/openminemap;
                    registerDynamicIdentifier(osmTile, keyToTry);
                    foundTile = true;
                    break;
                } catch (IIOException e) { //if neither, check even higher
                    zoomToTry -= 1;
                    xToTry /= 2;
                    yToTry /= 2;
                    keyToTry = Arrays.toString(new int[] {zoomToTry, xToTry, yToTry});
                }

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

    private static Identifier registerDynamicIdentifier(BufferedImage image, String key) throws IOException {
        //convert to NaitiveImage
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        NativeImage nImage = NativeImage.read(is);
        //register new dynamic texture and store it again to be referenced later
        dyLoadedTiles.put(key, mc.getTextureManager().registerDynamicTexture("osmtile", new NativeImageBackedTexture(nImage)));
        //System.out.println("New Dynamic tile");
        if (key.equals(Arrays.toString(new int[] {0, 0, 0}))) themeColor = image.getRGB(3, 3);

        is.close();
        nImage.close();
        os.close();

        return dyLoadedTiles.get(key);
    }

    public static void initializeConfigParameters() {
        doArtificialZoom = ConfigFile.readParameter(ConfigOptions.ARTIFICIAL_ZOOM).equals("on");
        mouseZoomStrength = Double.parseDouble(ConfigFile.readParameter(ConfigOptions.ZOOM_STRENGTH));
        doReverseScroll = ConfigFile.readParameter(ConfigOptions.REVERSE_SCROLL).equals("on");
        showPlayers = OverlayVisibility.fromString(ConfigFile.readParameter(ConfigOptions.SHOW_PLAYERS));
        showDirectionIndicators = OverlayVisibility.fromString(ConfigFile.readParameter(ConfigOptions.SHOW_DIRECTION_INDICATORS));
    }

}