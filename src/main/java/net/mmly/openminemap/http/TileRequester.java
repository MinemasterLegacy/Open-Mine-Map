package net.mmly.openminemap.http;

import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.map.RequestableTile;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.RasterApiKeysFile;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

public class TileRequester extends Requester<RequestableTile> {

    private static final int TILE_REQUEST_INTERVAL_MS = 50;

    private final String[] subDomains = new String[]{"a", "b", "c"};
    private final String subDomain = subDomains[new Random().nextInt(3)];

    public TileRequester(boolean persistent) {
        super(persistent, TILE_REQUEST_INTERVAL_MS, "Raster Tile");
    }

    @Override
    public void request() {
        if (pendingRequest == null) return;
        tileGetRequest(pendingRequest);
    }

    private void tileGetRequest(RequestableTile tile) {
        if (TileManager.isTileOutOfBounds(tile.x, tile.y, tile.zoom)) return;
        if (tile.raster == null) return;

        BufferedImage image;
        String urlPattern = tile.raster.source_url
                .replace("{z}", Integer.toString(tile.zoom))
                .replace("{x}", Integer.toString(tile.x))
                .replace("{y}", Integer.toString(tile.y))
                .replace("{s}", subDomain);

        if (tile.raster.hasKeyField()) {
            if (RasterApiKeysFile.hasApiKey(tile.raster.presetID)) {
                urlPattern = urlPattern.replace("{t}", RasterApiKeysFile.readApiKey(tile.raster.presetID));
            } else {
                return;
            }
        }

        InputStream inputStream = get(urlPattern);
        if (inputStream == null) return;

        try {
            image = ImageIO.read(inputStream);
            File out = new File(TileManager.getRootFile() + "openminemap/"+tile.raster.name+"/"+tile.zoom+"/"+tile.x+"-"+tile.y+".png"); //get file for tile
            ImageIO.write(image, "png", out); //write tile to disk
        } catch (IOException | SecurityException e) {
            OpenMineMap.LOGGER.error("Error during tile write: " + e.getMessage());
        }

        pendingRequest = null;

    }
}
