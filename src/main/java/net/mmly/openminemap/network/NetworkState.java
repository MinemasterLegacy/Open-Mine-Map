package net.mmly.openminemap.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public enum NetworkState {
    NOT_CONNECTED("disconnected"),
    BAD_CONNECTION("warning"),
    CONNECTED("connected");

    public final Identifier identifier;
    public final Identifier selectionIdentifier;
    public final String translationKey;

    private static NetworkState currentNetworkState = NetworkState.NOT_CONNECTED;

    NetworkState(String baseName) {
        this.identifier = Identifier.of("openminemap", "network/" + baseName + ".png");
        this.selectionIdentifier = Identifier.of("openminemap", "network/" + baseName + "selection.png");
        this.translationKey = "omm.network." + baseName;
    }

    public static NetworkState getNetworkState() {
        return currentNetworkState;
    }

    public static void connectionEstablished() {
        if (currentNetworkState != BAD_CONNECTION) currentNetworkState = CONNECTED;
    }

    public static void connectionErrored() {
        currentNetworkState = BAD_CONNECTION;
    }

    public static void resetNetworkState(ClientPlayNetworkHandler clientPlayNetworkHandler, MinecraftClient client) {
        currentNetworkState = NOT_CONNECTED;
    }

    public Formatting getTranslationTextColor() {
        return switch (this) {
            case CONNECTED -> Formatting.GREEN;
            case BAD_CONNECTION -> Formatting.YELLOW;
            case NOT_CONNECTED -> Formatting.RED;
        };
    }

}
