package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ButtonState;

import java.util.Locale;

public enum MicroButtonFunction {
    EDIT,
    UP,
    DOWN,
    REMOVE,
    VISIBILITY,
    INFO;

    public Identifier getTexture(ButtonState state) {
        return Identifier.of("openminemap", "buttons/micro/" + state.toString().toLowerCase(Locale.US) + "/" + this.toString().toLowerCase(Locale.US) + ".png");
    }


}
