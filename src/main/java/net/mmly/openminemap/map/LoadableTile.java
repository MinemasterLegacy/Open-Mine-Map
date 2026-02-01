package net.mmly.openminemap.map;

public class LoadableTile {

    int x;
    int y;
    int zoom;
    String cache;
    String key;

    LoadableTile(int tileX, int tileY, int tileZoom, String tileCache, String tileKey) {
        this.x = tileX;
        this.y = tileY;
        this.zoom = tileZoom;
        this.cache = tileCache;
        this.key = tileKey;
    }

}
