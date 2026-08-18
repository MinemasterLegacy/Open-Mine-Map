package net.mmly.openminemap.map;

import net.mmly.openminemap.raster.RasterScreen;
import net.mmly.openminemap.util.TileUrl;

import java.io.InputStream;

public class RegisterableTile {

    public static final Queuer TILE_MANAGER = (tile) -> {
        TileManager.tileRegisteringQueue.addLast(tile);
    };
    public static final Queuer RASTER_SCREEN = (tile) -> {
        RasterScreen.tileRegisteringQueue.addLast(tile);
    };

    public final InputStream image;
    public final String identifierString;
    public final TileUrl raster;
    private final Queuer queuer;

    public RegisterableTile(InputStream image, String key, TileUrl raster) {
        this(image, key, raster, TILE_MANAGER);
    }

    public RegisterableTile(InputStream image, String key, TileUrl raster, Queuer destination) {
        this.image = image;
        this.identifierString = key;
        this.raster = raster;
        this.queuer = destination;
    }

    public void queue() {
        queuer.queue(this);
    }

    @FunctionalInterface
    public interface Queuer {
        void queue(RegisterableTile tile);
    }

}
