package net.mmly.openminemap.map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;

public class TileManager {

    private static MinecraftClient mc = MinecraftClient.getInstance();
    private static HashMap<String, Identifier> dyLoadedTiles = new HashMap<>();
    public static int tileScaledSize = 128; //should only be a power of 2
    public static int hudTileScaledSize = 64; //should only be a power of 2


    public static final String OSM_MTYPE_STREET = "openstreetmap";
    //public static final String OSM_MTYPE_TOPO = "opentopomap";
    private static int centerTileArrayIndexX;
    private static int centerTileArrayIndexY;

    public static String getRootFile() { //returns directory of .minecraft (or equivalent folder)
        return System.getProperty("user.dir") + File.separator;
    }

    public static Identifier getErrorTile() { //tile used when there was an error getting an expected tile
        return Identifier.of("openminemap", "errortile.png");
    }

    public static Identifier getBlankTile() { //tile used for out of bounds tiles
        return Identifier.of("openminemap", "blanktile.png");
    }

    public static Identifier getLoadingTile() { //tile used for out of bounds tiles
        return Identifier.of("openminemap", "loadingtile.png");
    }

    public static int[] getTopLeftData() {
        return new int[] {leftMostX, topMostY};
    }

    private static int leftMostX;
    private static int topMostY;

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
                identifiers[j][k] = getOsmTile(leftMostX + j, topMostY + k, zoom, TileManager.OSM_MTYPE_STREET);
            }
        }

        return identifiers;
    }

    public static boolean isTileOutOfBounds(int x, int y, int zoom) { //checks if a given tile is out of bounds
        return (x < 0 || y < 0 || x > Math.pow(2, zoom) - 1 || y > Math.pow(2, zoom) - 1);
    }

    public static int[] getCenterTileArrayIndexes() {
        return new int[] {centerTileArrayIndexX, centerTileArrayIndexY};
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

    public static Identifier getOsmTile(int x, int y, int zoom, String type) { //get an osm map tile. It should be retrievd from cache if it is cached and retreived from the web otherwise
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

            //System.out.println("x = "+x+", eq = "+(Math.pow(2.0, zoom) - 1));
            if (isTileOutOfBounds(x, y, zoom)) { //if tile is out of bounds of the possible tile spaces
                return getBlankTile();
            }

            BufferedImage osmTile;
            try { //if image is found in cache
                osmTile = ImageIO.read(new File(getRootFile() + "openminemap/"+zoom+"/"+x+"-"+y+".png")); //get an image from /run/openminemap;
            } catch (IIOException e) { //else, request image from osm
                //System.out.println("Requesting OSM Tile...");
                //osmTile = RequestManager.tileGetRequest(x, y, zoom, type);
                RequestManager.trySetRequest(x, y, zoom);
                return getLoadingTile();
            }

            if (osmTile == null) { //if file was not found AND tileGetRequest failed
                return getErrorTile();
            }

            String thisKey = Arrays.toString(new int[] {zoom, x, y});
            if (!dyLoadedTiles.containsKey(thisKey)) {
                //convert to NaitiveImage
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(osmTile, "png", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                NativeImage nImage = NativeImage.read(is);
                //System.out.println("new texture saved");
                //register new dynamic texture and store it again to be referenced later
                dyLoadedTiles.put(thisKey, mc.getTextureManager().registerDynamicTexture("osmtile", new NativeImageBackedTexture(nImage)));
            }

            return dyLoadedTiles.get(thisKey);

        } catch (Exception e) {
            System.out.println("Error while getting tile: " + e);
            e.printStackTrace();
            return TileManager.getErrorTile();
        }
    }
}