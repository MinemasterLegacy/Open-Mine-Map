package net.mmly.openminemap.map;

import net.mmly.openminemap.util.UnitConvert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class TileLoader extends Thread {

    private final LoadableTile[] tilesToLoad;
    private static long memoryCacheSize;

    TileLoader(LoadableTile[] tilesToLoad) {
        this.tilesToLoad = tilesToLoad;
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
                TileManager.tileRegisteringQueue.addLast(new RegisterableTile(in, tile.key, tile.cache));
            }
        }
    }

    private static InputStream loadTileFromDisk(LoadableTile tile) {
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
            System.out.println("Error while getting tile: " + e);
            e.printStackTrace();
            return null;
        }

    }

}