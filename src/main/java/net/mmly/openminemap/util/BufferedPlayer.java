package net.mmly.openminemap.util;

import net.minecraft.util.Identifier;

public class BufferedPlayer {
    public int offsetX;
    public int offsetY;
    public Identifier texture;

    public BufferedPlayer(int offsetX, int offsetY, Identifier texture) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.texture = texture;
    }
}