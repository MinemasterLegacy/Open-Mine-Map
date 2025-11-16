package net.mmly.openminemap.map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.OverlayVisibility;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.DrawableMapTile;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class TileManager {

    private static MinecraftClient mc = MinecraftClient.getInstance();
    private static HashMap<String, Identifier> dyLoadedTiles = new HashMap<>();
    public static int tileScaledSize = 128; //should only be a power of 2
    public static int hudTileScaledSize = 128; //should only be a power of 2 (was 64 in v1.0.0)
    public static boolean doArtificialZoom;
    public static boolean doReverseScroll;
    public static OverlayVisibility showPlayers;
    public static OverlayVisibility showDirectionIndicators;
    public static int themeColor = 0xFF808080;

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

    public static DrawableMapTile[][] getRangeOfDrawableTiles(int mapPosX, int mapPosY, int mapZoom, int windowWidth, int windowHeight, int tileRenderSize) {
        /*  mapTileXY: the map coorinates of the center of the screen | map coordinate range is 128 * 2^(zoom+1)
         *  mapZoom: the zoom level of the map
         *  windowHeightXY: [scaled] height and width of window
         *  tileRenderSize: the size of each tile, usually 128 but can change with artificial zoom */

        int leftBorder = (-windowWidth / 2) + mapPosX;
        int rightBorder = (windowWidth / 2) - mapPosX;
        int topBorder = (-windowHeight / 2) + mapPosY;
        int bottomBorder = (windowHeight / 2) - mapPosY;

        int firstTileX = (int) Math.floor((double) leftBorder / tileRenderSize);
        int firstTileY = (int) Math.floor((double) topBorder / tileRenderSize);

        int tileCountX = (int) Math.ceil((double) windowWidth / tileRenderSize) + 1;
        int tileCountY = (int) Math.ceil((double) windowHeight / tileRenderSize) + 1;

        DrawableMapTile[][] tiles = new DrawableMapTile[tileCountX][tileCountY];
        for (int j = 0; j < tileCountX; j++) {
            for (int k = 0; k < tileCountY; k++) {
                tiles[j][k] = /*new DrawableMapTile(*/
                        getDrawableTile(firstTileX + j, firstTileY + k, mapZoom, tileRenderSize)/*.identifier,
                        (firstTileX + j) * tileRenderSize,
                        (firstTileY + k) * tileRenderSize,
                        mapZoom,
                        1,
                        1
                )*/;
            }
        }

        return tiles;
    }

    //a method used to provide data used to render every tile that needs to be on the screen
    public static Identifier[][] getRangeOfTiles(int x, int y, int zoom, int windowWidth, int windowHeight, int scaledSize) {

        //x = 0 , bnx = -960
        //x = 200, bnx = -760
        int borderNegX = (-windowWidth / 2) + x;
        int borderX = (windowWidth / 2) - x;
        int borderNegY = (-windowHeight / 2) + y;
        int borderY = (windowHeight / 2) - y;

        leftMostX = (int) Math.floor((double) borderNegX / scaledSize); //tile x id of leftmost (negative-most) tile
        topMostY = (int) Math.floor((double) borderNegY / scaledSize); //tile y id of topmost (negative-most) tile


        //leftMostX = (int) Math.floor((borderNegX + Math.pow(2, zoom + 7)) / 256); //tile x id of leftmost (negative-most) tile
        //topMostY = (int) Math.floor((borderNegY + Math.pow(2, zoom + 7)) / 256); //tile y id of topmost (negative-most) tile

        int horzTileCount = (int) Math.ceil((double) windowWidth / scaledSize) + 1;
        int vertTileCount = (int) Math.ceil((double) windowHeight / scaledSize) + 1;

        Identifier[][] identifiers = new Identifier[horzTileCount][vertTileCount];
        for (int j = 0; j < horzTileCount; j++) {
            for (int k = 0; k < vertTileCount; k++) {
                identifiers[j][k] = getOsmTile(leftMostX + j, topMostY + k, zoom);
            }
        }

        return identifiers;
    }

    public static boolean isTileOutOfBounds(int x, int y, int zoom) { //checks if a given tile is out of bounds
        return (x < 0 || y < 0 || x > Math.pow(2, zoom) - 1 || y > Math.pow(2, zoom) - 1);
    }

    public static void createCacheDir() {
        try { // create or open the base openminemap file for caching
            File cacheDirectory = new File(TileManager.getRootFile() + "openminemap");
            if (cacheDirectory.mkdir()) { //if directory does not exist
                System.out.println("OMM Directory Creation Success: " + cacheDirectory.getAbsolutePath());
            } else { //if directory does exist
                //System.out.println(Text.literal("OMM Directory Exists: " + cacheDirectory.getAbsolutePath()));
            }
        } catch (Exception e) {
            System.out.println(Text.literal("OMM Directory Error: " + e));
        }
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

    private static DrawableMapTile getDrawableTile(int tileX, int tileY, int mapZoom, int tileRenderSize) {
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
                osmTile = ImageIO.read(new File(getRootFile() + "openminemap/"+mapZoom+"/"+tileX+"-"+tileY+".png")); //get an image from /run/openminemap;
                registerDynamicIdentifier(osmTile, thisKey);
                return new DrawableMapTile(
                        dyLoadedTiles.get(thisKey),
                        tileX * tileRenderSize,
                        tileY * tileRenderSize,
                        tileRenderSize
                );
            } catch (IIOException e) {
                //else, request image from osm. Tile will be loaded eventually, but
                RequestManager.trySetRequest(tileX, tileY, mapZoom);
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
                    osmTile = ImageIO.read(new File(getRootFile() + "openminemap/"+zoomToTry+"/"+xToTry+"-"+yToTry+".png")); //get an image from /run/openminemap;
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
                        tileRenderSize / (int) Math.pow(2, mapZoom - zoomToTry)
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
        return dyLoadedTiles.get(key);
    }

    public static Identifier getOsmTile(int x, int y, int zoom) { //get an osm map tile. It should be retrievd from cache if it is cached and retreived from the web otherwise
        try {
            /*
            osm tile url format: http://[abc].tile.openstreetmap.org/zoom/x/y.png
             - [abc]: either "a", "b", or "c" (there are 3 subdomains)
             - zoom: integer 0 through 18, inclusive
             - x: x position (not longitude) of tile. range is 0 through ((zoom+1)^2)-1, inclusive
             - y: y position (not latitude) of tile. range is 0 through ((zoom+1)^2)-1, inclusive
            more info:
            https://stackoverflow.com/questions/3238611/how-can-i-get-tile-count-tile-x-tile-y-details-without-specifying-zoom-level/3238960#3238960
            https://stackoverflow.com/questions/17434226/fetch-openstreetmap-image-for-specified-latitude-longitude
             */

            String thisKey = Arrays.toString(new int[] {zoom, x, y});
            if (dyLoadedTiles.containsKey(thisKey)) {
                return dyLoadedTiles.get(thisKey);
            }

            //System.out.println("x = "+x+", eq = "+(Math.pow(2.0, zoom) - 1));
            if (isTileOutOfBounds(x, y, zoom)) { //if tile is out of bounds of the possible tile spaces
                return getBlankIdentifier();
            }

            BufferedImage osmTile;
            try { //if image is found in cache
                osmTile = ImageIO.read(new File(getRootFile() + "openminemap/"+zoom+"/"+x+"-"+y+".png")); //get an image from /run/openminemap;
            } catch (IIOException e) { //else, request image from osm
                //System.out.println("Requesting OSM Tile...");
                //osmTile = RequestManager.tileGetRequest(x, y, zoom, type);
                RequestManager.trySetRequest(x, y, zoom);
                return getLoadingIdentifier();
            }

            if (osmTile == null) { //if file was not found AND tileGetRequest failed
                return getErrorIdentifier();
            }

            if (!dyLoadedTiles.containsKey(thisKey)) {
                //convert to NaitiveImage
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(osmTile, "png", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                NativeImage nImage = NativeImage.read(is);
                //register new dynamic texture and store it again to be referenced later
                dyLoadedTiles.put(thisKey, mc.getTextureManager().registerDynamicTexture("osmtile", new NativeImageBackedTexture(nImage)));
                //System.out.println("New Dynamic tile");
                return dyLoadedTiles.get(thisKey);
            }

            return getErrorIdentifier();

        } catch (Exception e) {
            System.out.println("Error while getting tile: " + e);
            e.printStackTrace();
            return TileManager.getErrorIdentifier();
        }
    }

    public static void initializeConfigParameters() {
        doArtificialZoom = ConfigFile.readParameter(ConfigOptions.ARTIFICIAL_ZOOM).equals("on");
        doReverseScroll = ConfigFile.readParameter(ConfigOptions.REVERSE_SCROLL).equals("on");
        showPlayers = OverlayVisibility.fromString(ConfigFile.readParameter(ConfigOptions.SHOW_PLAYERS));
        showDirectionIndicators = OverlayVisibility.fromString(ConfigFile.readParameter(ConfigOptions.SHOW_DIRECTION_INDICATORS));
    }

}