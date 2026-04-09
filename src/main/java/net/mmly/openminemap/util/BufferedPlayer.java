package net.mmly.openminemap.util;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BufferedPlayer {
    public double mapX;
    public double mapY;
    public Identifier texture;
    public double y = Double.NaN;
    public Text name;

    public BufferedPlayer(double mapX, double mapY, Identifier texture) {
        this.mapX = mapX;
        this.mapY = mapY;
        this.texture = texture;
    }

    public BufferedPlayer(double mapX, double mapY, Identifier texture, double y, Text name) {
        this(mapX, mapY, texture);
        this.y = y;
        this.name = name;
        if (this.name.getString().equals("FreeCamera")) {
            this.texture = Identifier.of("openminemap", "freecam-skin.png");
        }
    }
}