package net.mmly.openminemap.util;

import net.minecraft.util.Identifier;

public class BufferedPlayer {
    public int offsetX;
    public int offsetY;
    public Identifier texture;
    public double y;

    public int upCrop;
    public int downCrop;
    public int leftCrop;
    public int rightCrop;

    public BufferedPlayer(int offsetX, int offsetY, Identifier texture, double y) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.texture = texture;
        this.y = y;
    }

    public BufferedPlayer(int offsetX, int offsetY, Identifier texture, double y, int upCrop, int downCrop, int leftCrop, int rightCrop) {
        this(offsetX, offsetY, texture, y);
        this.upCrop = upCrop;
        this.downCrop = downCrop;
        this.leftCrop = leftCrop;
        this.rightCrop = rightCrop;
    }
}