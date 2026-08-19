package net.mmly.openminemap.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class BufferedPlayer {
    public double mapX;
    public double mapY;
    public Identifier texture;
    public double y = Double.NaN;
    public Component name;

    public BufferedPlayer(double mapX, double mapY, Identifier texture) {
        this.mapX = mapX;
        this.mapY = mapY;
        this.texture = texture;
    }

    public BufferedPlayer(double mapX, double mapY, Identifier texture, double y, Component name) {
        this(mapX, mapY, texture);
        this.y = y;
        this.name = name;
        if (this.name.getString().equals("FreeCamera")) {
            this.texture = Identifier.fromNamespaceAndPath("openminemap", "freecam-skin.png");
        }
    }
}