package net.mmly.openminemap.raster;

import net.minecraft.resources.Identifier;
import net.mmly.openminemap.enums.ButtonState;
import net.mmly.openminemap.gui.ButtonLayer;

import java.util.Locale;

public enum MicroButtonFunction {
    EDIT,
    UP,
    DOWN,
    REMOVE,
    DELETE,
    VISIBILITY,
    INFO;

    public Identifier getTexture(ButtonState state) {
        if (!ButtonLayer.texturedButtons) {
            if (state == ButtonState.LOCKED) return Identifier.fromNamespaceAndPath("openminemap", "buttons/micro/generated/" + this.toString().toLowerCase(Locale.US) + "-dark.png");
            else return Identifier.fromNamespaceAndPath("openminemap", "buttons/micro/generated/" + this.toString().toLowerCase(Locale.US) + ".png");
        }
        return Identifier.fromNamespaceAndPath("openminemap", "buttons/micro/" + state.toString().toLowerCase(Locale.US) + "/" + this.toString().toLowerCase(Locale.US) + ".png");
    }

}
