package net.mmly.openminemap.util;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BufferedPlayer {
    public int offsetX;
    public int offsetY;
    public Identifier texture;
    public double y = Double.NaN;
    public Text name;

    public int upCrop;
    public int downCrop;
    public int leftCrop;
    public int rightCrop;

    public BufferedPlayer(int offsetX, int offsetY, Identifier texture) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.texture = texture;
    }

    public BufferedPlayer(int offsetX, int offsetY, Identifier texture, double y, Text name) {
        this(offsetX, offsetY, texture);
        this.y = y;
        this.name = name;
    }
}