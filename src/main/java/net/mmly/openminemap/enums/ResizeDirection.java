package net.mmly.openminemap.enums;

import net.minecraft.util.Identifier;

public enum ResizeDirection {
    UP_MAP(true),
    RIGHT_MAP(false),
    DOWN_MAP(true),
    LEFT_MAP(false),
    RIGHT_COMPASS(false),
    LEFT_COMPASS(false);

    public final Identifier identifier;
    private final boolean isVertical;

    ResizeDirection(boolean isVertical) {
        this.isVertical = isVertical;
        identifier = isVertical ?
                Identifier.of("openminemap", "resizevertical.png") :
                Identifier.of("openminemap", "resizehorizontal.png");

    }

    public boolean isVertical() {
        return this.isVertical;
    }

    public boolean isHorizontal() {
        return !this.isVertical;
    }

    public int width() {
        return isVertical ? 20 : 7;
    }

    public int height() {
        return isVertical ? 7 : 20;
    }

}
