package net.mmly.openminemap.map;

import net.mmly.openminemap.raster.RasterScreen;

import java.io.InputStream;

public class RegisterableTile {

    public static final Queuer TILE_MANAGER = (tile) -> {
        TileManager.tileRegisteringQueue.addLast(tile);
    };
    public static final Queuer RASTER_SCREEN = (tile) -> {
        RasterScreen.tileRegisteringQueue.addLast(tile);
    };

    public final InputStream image;
    public final String key;
    public final String cacheName;
    private final Queuer queuer;

    public RegisterableTile(InputStream image, String key, String cacheName) {
        this(image, key, cacheName, TILE_MANAGER);
    }

    public RegisterableTile(InputStream image, String key, String cacheName, Queuer destination) {
        this.image = image;
        this.key = key;
        this.cacheName = cacheName;
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
