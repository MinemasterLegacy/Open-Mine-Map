package net.mmly.openminemap.network;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.Identifier;
import net.mmly.openminemap.map.PlayersManager;

public enum NetworkState {
    NOT_CONNECTED("disconnected"),
    BAD_CONNECTION("warning"),
    CONNECTED("connected");

    public final Identifier identifier;
    public final Identifier selectionIdentifier;
    public final String translationKey;

    private static NetworkState currentNetworkState = NetworkState.NOT_CONNECTED;

    NetworkState(String baseName) {
        this.identifier = Identifier.fromNamespaceAndPath("openminemap", "network/" + baseName + ".png");
        this.selectionIdentifier = Identifier.fromNamespaceAndPath("openminemap", "network/" + baseName + "selection.png");
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

    public static void resetNetworkState(ClientPacketListener clientPlayNetworkHandler, Minecraft client) {
        PlayersManager.lastReceivedData = NetworkPlayerData.empty();
        currentNetworkState = NOT_CONNECTED;
    }

    public ChatFormatting getTranslationTextColor() {
        return switch (this) {
            case CONNECTED -> ChatFormatting.GREEN;
            case BAD_CONNECTION -> ChatFormatting.YELLOW;
            case NOT_CONNECTED -> ChatFormatting.RED;
        };
    }

}
