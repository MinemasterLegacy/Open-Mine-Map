package net.mmly.openminemap.map;

public class LoadableTile {

    final int x;
    final int y;
    final int zoom;
    final String cache;
    final String key;

    public LoadableTile(int tileX, int tileY, int tileZoom, String tileCache, String tileKey) {
        this.x = tileX;
        this.y = tileY;
        this.zoom = tileZoom;
        this.cache = tileCache;
        this.key = tileKey;
    }

}
