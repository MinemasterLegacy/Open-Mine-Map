package net.mmly.openminemap.util;

import net.minecraft.util.Identifier;
import net.mmly.openminemap.map.TileManager;

public class DrawableMapTile {
    public Identifier identifier;
    public double x;
    public double y;
    public int tileZoom;
    public int subSectionX = 0;
    public int subSectionY = 0;
    public int subSectionSize = TileManager.tileScaledSize;

    public DrawableMapTile(Identifier identifier, double x, double y, int zoom, int subSectionX, int subSectionY, int subSectionSize) {
        this(identifier, x, y, zoom);
        this.subSectionX = subSectionX;
        this.subSectionY = subSectionY;
        this.subSectionSize = subSectionSize;
    }

    public DrawableMapTile(Identifier identifier, double x, double y, int zoom) {
        this.identifier = identifier;
        this.x = x;
        this.y = y;
        this.tileZoom = zoom;
    }
}
