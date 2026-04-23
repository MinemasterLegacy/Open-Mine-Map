package net.mmly.openminemap.map;

import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.util.UnitConvert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class TileLoader extends Thread {

    private final LoadableTile[] tilesToLoad;
    private static long memoryCacheSize;
    private final RegisterableTile.Queuer destination;

    public TileLoader(LoadableTile[] tilesToLoad) {
        this(tilesToLoad, RegisterableTile.TILE_MANAGER);
    }

    public TileLoader(LoadableTile[] tilesToLoad, RegisterableTile.Queuer destination) {
        this.tilesToLoad = tilesToLoad;
        this.destination = destination;
    }

    public static long getMemoryCacheSize() {
        return memoryCacheSize;
    }

    public static String getStylizedCacheSize() {
        if (memoryCacheSize > 1e9) {
            return UnitConvert.floorToPlace(memoryCacheSize / 1e9, 2) + "gB";
        } else if (memoryCacheSize > 1e6) {
            return UnitConvert.floorToPlace(memoryCacheSize / 1e6, 2) + "mB";
        } else {
            return UnitConvert.floorToPlace(memoryCacheSize / 1e3, 2) + "kB";
        }
    }

    public static void resetCacheSize() {
        memoryCacheSize = 0;
    }

    @Override
    public void run() {
        for (LoadableTile tile : tilesToLoad) {
            InputStream in = loadTileFromDisk(tile);
            if (in != null) {
                new RegisterableTile(in, tile.key, tile.cache, destination).queue();
            }
        }
    }

    private InputStream loadTileFromDisk(LoadableTile tile) {
        try {
            BufferedImage tileImage = ImageIO.read(new File(TileManager.getRootFile() + "openminemap/"+tile.cache+"/"+tile.zoom+"/"+tile.x+"-"+tile.y+".png")); //get an image from /run/openminemap;
            if (tile.key.equals(TileManager.getKey(0, 0, 0))) TileManager.themeColor = tileImage.getRGB(3, 3);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(tileImage, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            os.close();
            memoryCacheSize += is.available();
            return is;
        } catch(IOException e) {
            OpenMineMap.LOGGER.warn("Error while loading tile from disk: " + e.getMessage());
            return null;
        }

    }

}