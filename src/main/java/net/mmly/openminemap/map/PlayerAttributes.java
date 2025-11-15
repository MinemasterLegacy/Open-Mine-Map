package net.mmly.openminemap.map;

import net.minecraft.client.MinecraftClient;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Direction;
import net.mmly.openminemap.projection.Projection;

public class PlayerAttributes {
    public static double longitude;
    public static double latitude;
    public static double yaw;
    public static double geoYaw;
    public static double altitude;

    //private static MinecraftClient mClient = MinecraftClient.getInstance();

    public static void updatePlayerAttributes(MinecraftClient minecraftClient) {

        yaw = minecraftClient.player.getYaw() % 360;
        if (yaw < 0) {
            yaw = yaw + 360;
        }
        geoYaw = yaw - Direction.calcDymaxionAngleDifference(); //yaw value for use with geo-based elements, like the compass and direction indicators
        //geoYaw can be NaN

        try {
            if (minecraftClient.player != null) {
                double[] c = Projection.to_geo(minecraftClient.player.getX(), minecraftClient.player.getZ());
                longitude = c[1];
                latitude = c[0];
                FullscreenMapScreen.playerLon = c[1];
                FullscreenMapScreen.playerLat = c[0];
                HudMap.playerLon = c[1];
                HudMap.playerLat = c[0];
                altitude = minecraftClient.player.getY();
            }
            return;
        } catch (CoordinateValueError ignored) {}

        longitude = Double.NaN;
        latitude = Double.NaN;
        FullscreenMapScreen.playerLon = Double.NaN;
        FullscreenMapScreen.playerLat = Double.NaN;
        HudMap.playerLon = Double.NaN;
        HudMap.playerLat = Double.NaN;
        altitude = Double.NaN;
    }
}
