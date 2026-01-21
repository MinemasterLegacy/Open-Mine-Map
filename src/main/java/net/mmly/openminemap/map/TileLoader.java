package net.mmly.openminemap.map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

public class TileLoader extends Thread{

    private final LoadableTile[] tilesToLoad;

    TileLoader(LoadableTile[] tilesToLoad) {
        this.tilesToLoad = tilesToLoad;
    }

    @Override
    public void run() {
        for (LoadableTile tile : tilesToLoad) {
            InputStream in = loadTileFromDisk(tile);
            if (in != null) {
                TileLoaderManager.tileRegisteringQueue.addLast(new RegisterableTile(in, tile.key));
            }
        }
    }

    private static InputStream loadTileFromDisk(LoadableTile tile) {
        try {
            BufferedImage tileImage = ImageIO.read(new File(TileManager.getRootFile() + "openminemap/"+tile.cache+"/"+tile.zoom+"/"+tile.x+"-"+tile.y+".png")); //get an image from /run/openminemap;
            if (tile.key.equals(Arrays.toString(new int[] {0, 0, 0}))) TileManager.themeColor = tileImage.getRGB(3, 3);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(tileImage, "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            os.close();
            return is;
        } catch(IOException e) {
            System.out.println("Error while getting tile: " + e);
            e.printStackTrace();
            return null;
        }

    }

}
