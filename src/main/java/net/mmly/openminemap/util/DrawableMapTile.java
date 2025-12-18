package net.mmly.openminemap.util;

import net.minecraft.util.Identifier;
import net.mmly.openminemap.map.TileManager;

public class DrawableMapTile {
    public Identifier identifier;
    public double x;
    public double y;
    public int tileRenderSize;
    public int subSectionX = 0;
    public int subSectionY = 0;
    public double subSectionSize;

    public DrawableMapTile(Identifier identifier, double x, double y, int tileRenderSize, int subSectionX, int subSectionY, double subSectionSize) {
        this(identifier, x, y, tileRenderSize);
        this.subSectionX = subSectionX;
        this.subSectionY = subSectionY;
        this.subSectionSize = subSectionSize;
    }

    public DrawableMapTile(Identifier identifier, double x, double y, int tileRenderSize) {
        this.identifier = identifier;
        this.x = x;
        this.y = y;
        this.tileRenderSize = tileRenderSize;
        this.subSectionSize = tileRenderSize;
    }
}
