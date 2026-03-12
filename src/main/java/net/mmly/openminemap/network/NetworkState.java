package net.mmly.openminemap.network;

import net.minecraft.util.Identifier;

public enum NetworkState {
    NOT_CONNECTED("disconnected"),
    BAD_CONNECTION("warning"),
    CONNECTED("connected");

    public final Identifier identifier;
    public final Identifier selectionIdentifier;
    public final String translationKey; //TODO translate (x3)

    NetworkState(String baseName) {
        this.identifier = Identifier.of("openminemap", "network/" + baseName + ".png");
        this.selectionIdentifier = Identifier.of("openminemap", "network/" + baseName + "selection.png");
        this.translationKey = "omm.network." + baseName;
    }

}
