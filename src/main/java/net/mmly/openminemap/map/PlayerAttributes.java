package net.mmly.openminemap.map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Direction;
import net.mmly.openminemap.projection.Projection;

public class PlayerAttributes {
    public static double longitude;
    public static double latitude;
    public static double yaw;
    public static double geoYaw;
    public static double altitude;
    private static boolean isValidPosition = true;
    private static Identifier identifier;

    //private static MinecraftClient mClient = MinecraftClient.getInstance();

    public static void updatePlayerAttributes(MinecraftClient minecraftClient) {

        identifier = MinecraftClient.getInstance().player.getSkin().body().texturePath();

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
                altitude = minecraftClient.player.getY();
                if (!Double.isNaN(longitude) && !Double.isNaN(latitude)) isValidPosition = true;
                else isValidPosition = false;
            }
            return;
        } catch (CoordinateValueError ignored) {}

        longitude = Double.NaN;
        latitude = Double.NaN;
        altitude = Double.NaN;
        isValidPosition = false;
    }

    public static double getLongitude() {
        return longitude;
    }

    public static double getLatitude() {
        return latitude;
    }

    public static double getAltitude() {
        return altitude;
    }

    public static boolean positionIsValid() {
        return isValidPosition;
    }

    public static double getYaw() {
        return yaw;
    }

    public static double getGeoYaw() {
        return geoYaw;
    }

    public static Identifier getIdentifier() {
        return identifier;
    }
}
