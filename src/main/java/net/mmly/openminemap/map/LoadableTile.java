package net.mmly.openminemap.map;

import net.mmly.openminemap.util.TileUrl;

public class LoadableTile {

    final int x;
    final int y;
    final int zoom;
    final TileUrl raster;
    final String key;

    public LoadableTile(int tileX, int tileY, int tileZoom, TileUrl raster, String tileKey) {
        this.x = tileX;
        this.y = tileY;
        this.zoom = tileZoom;
        this.raster = raster;
        this.key = tileKey;
    }

}
