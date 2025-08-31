package net.mmly.openminemap.map;

import net.minecraft.client.MinecraftClient;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.projection.Projection;

public class PlayerAttributes {
    public static double playerLon;
    public static double playerLat;

    //private static MinecraftClient mClient = MinecraftClient.getInstance();

    public static void updatePlayerLocations(MinecraftClient minecraftClient) {
        try {
            double[] c = Projection.to_geo(minecraftClient.player.getX(), minecraftClient.player.getZ());
            playerLon = c[1];
            playerLat = c[0];
            FullscreenMapScreen.playerLon = c[1];
            FullscreenMapScreen.playerLat = c[0];
            HudMap.playerLon = c[1];
            HudMap.playerLat = c[0];
            return;
        } catch (Exception ignored) {}

        playerLon = Double.NaN;
        playerLat = Double.NaN;
        FullscreenMapScreen.playerLon = Double.NaN;
        FullscreenMapScreen.playerLat = Double.NaN;
        HudMap.playerLon = Double.NaN;
        HudMap.playerLat = Double.NaN;
    }
}
