package net.mmly.openminemap.map;

import net.minecraft.client.MinecraftClient;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.projection.Projection;

public class PlayerAttributes {
    public static double longitude;
    public static double latitude;
    public static double yaw;

    //private static MinecraftClient mClient = MinecraftClient.getInstance();

    public static void updatePlayerAttributes(MinecraftClient minecraftClient) {
        try {
            if (minecraftClient.player != null) {
                yaw = minecraftClient.player.getYaw() % 360;
                if (yaw < 0) {
                    yaw = yaw + 360;
                }
                double[] c = Projection.to_geo(minecraftClient.player.getX(), minecraftClient.player.getZ());
                longitude = c[1];
                latitude = c[0];
                FullscreenMapScreen.playerLon = c[1];
                FullscreenMapScreen.playerLat = c[0];
                HudMap.playerLon = c[1];
                HudMap.playerLat = c[0];
            }
            return;
        } catch (Exception ignored) {}

        longitude = Double.NaN;
        latitude = Double.NaN;
        FullscreenMapScreen.playerLon = Double.NaN;
        FullscreenMapScreen.playerLat = Double.NaN;
        HudMap.playerLon = Double.NaN;
        HudMap.playerLat = Double.NaN;
    }
}
