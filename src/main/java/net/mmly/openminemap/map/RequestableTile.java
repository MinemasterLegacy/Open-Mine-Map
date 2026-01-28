package net.mmly.openminemap.map;

public class RequestableTile {

    int x;
    int y;
    int zoom;
    int proximityScore; //the lower the score, the closer the tile is to the center and the more it should be prioritised
    String cacheName;

    public RequestableTile(int x, int y, int tileZoom, int proximityScore, String cacheName) {
        this.x = x;
        this.y = y;
        this.zoom = tileZoom;
        this.proximityScore = proximityScore;
        this.cacheName = cacheName;
        clampToZoom18();
    }

    private void clampToZoom18() {
        if (zoom <= 18) return;
        int scaleFactor = (int) Math.pow(2, zoom - 18);
        x /= scaleFactor;
        y /= scaleFactor;
        zoom = 18;
    }

    public boolean sameTileAs(RequestableTile tile) {
        if (tile == null) return false;
        return (tile.x == this.x && tile.y == this.y && tile.zoom == this.zoom);
    }
}
