package net.mmly.openminemap.map;

import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.UnitConvert;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class TileLoader extends Thread {

    private final LoadableTile[] tilesToLoad;
    private static long memoryCacheSize;
    private final RegisterableTile.Queuer destination;
    private boolean updateBackgoundColor = true;
    private boolean fileMayBeNull = false;

    public TileLoader(LoadableTile[] tilesToLoad) {
        this(tilesToLoad, RegisterableTile.TILE_MANAGER);
    }

    public TileLoader(LoadableTile[] tilesToLoad, RegisterableTile.Queuer destination) {
        this.tilesToLoad = tilesToLoad;
        this.destination = destination;
    }

    public TileLoader updateBackgoundColor(boolean b) {
        updateBackgoundColor = b;
        return this;
    }

    public TileLoader setFileMayBeNull(boolean b) {
        this.fileMayBeNull = b;
        return this;
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

    //todo recreate tile memory size counter for multiple rasters
    public static void resetCacheSize() {
        memoryCacheSize = 0;
    }

    @Override
    public void run() {
        for (LoadableTile tile : tilesToLoad) {
            InputStream in = loadTileFromDisk(tile);
            if (in != null) {
                new RegisterableTile(in, tile.key, tile.raster, destination).queue();
            }
        }
    }

    private static boolean lockExists(LoadableTile tile) {
        return TileManager.lockFileOf(tile).exists();
    }

    private void waitForLock(LoadableTile tile) throws InterruptedException {
        int msPassed = 0;
        while (msPassed < 20) {
            if (!lockExists(tile)) return;
            sleep(1);
            msPassed++;
        }
        OpenMineMap.LOGGER.warn("Lock for tile '" + tile.raster.name + "#" + tile.key + "' persisted for too long, will delete and attempt to read tile anyways.");
        TileManager.lockFileOf(tile).delete();
    }

    private InputStream loadTileFromDisk(LoadableTile tile) {
        try {
            //waitForLock(tile);
            BufferedImage tileImage = ImageIO.read(new File(TileManager.getRootFile() + "openminemap/"+tile.raster.name+"/"+tile.zoom+"/"+tile.x+"-"+tile.y+".png")); //get an image from /run/openminemap;
            if (tile.key.equals(TileManager.getKey(0, 0, 0, RasterProvider.getCurrentBaseRaster().identifierString)) && updateBackgoundColor) {
                TileManager.setThemeColor(tileImage.getRGB(3, 3));
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(tileImage, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            os.close();
            memoryCacheSize += is.available();
            return is;
        } catch(IOException e) {
            if (!fileMayBeNull) OpenMineMap.LOGGER.warn("Error while loading tile '" + tile.raster.name + "#" + tile.key + "' from disk: " + e.getMessage());
            return null;
        }

    }

}