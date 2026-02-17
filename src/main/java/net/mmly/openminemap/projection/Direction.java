package net.mmly.openminemap.projection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.mmly.openminemap.map.PlayerAttributes;

public class Direction {
    static int delay = 0;

    @Deprecated //use getGeoAzimuth
    public static double calcDymaxionAngleDifference() { //returns the change in agle between mine
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        double playerX = minecraftClient.player.getX();
        double playerZ = minecraftClient.player.getZ();
        double playerLon = PlayerAttributes.longitude;
        double playerLat = PlayerAttributes.latitude;
        double sampleLon = playerLon;
        double sampleLat = playerLat + 0.001;
        double[] sampleXY;

        try {
            sampleXY = Projection.from_geo(sampleLat, sampleLon);
        } catch (CoordinateValueError error) {
            //System.out.println("error");
            return Double.NaN;
        }
        double sampleX = sampleXY[0];
        double sampleZ = sampleXY[1];

        double minecraftFacing = Math.toDegrees(Math.atan2(sampleZ - playerZ, sampleX - playerX));
        //System.out.println("changed");

        //I have no idea how this quadrant shit works, but it does so oh well
        int quadrant = playerZ - sampleZ < 0 ? 4 : 1; //determine quadrant
        if (playerX - sampleX < 0) {
            quadrant = quadrant == 4 ? 3 : 2;
        }

        switch (quadrant) {
            case 1: {
                minecraftFacing += 180;
                break;
            }
            case 2: {
                //nothing
                break;
            }
            case 3: {
                //nothing?
                break;
            }
            case 4: {
                minecraftFacing -= 360;
                break;
            }
        }

        if (playerX - sampleX < 0) { //if in second or third quadrant
            minecraftFacing += 180;
        } else if (playerZ - sampleZ < 0) { //if in fourth quadrant
            minecraftFacing += 180;
        }

        minecraftFacing += 90;

        //System.out.println("Qu: "+quadrant);
        //System.out.println("XY: "+minecraftFacing);

        return minecraftFacing;
    }

    public static double getGeoAzimuth(Entity entity) {
        if (entity == null) return 0;
        return getGeoAzimuth(entity.getX(), entity.getZ(), entity.getYaw());
    }

    public static double getGeoAzimuth(double mX, double mZ, double mAngle) {
        double x2 = mX - 1E-5F * Math.sin(Math.toRadians(mAngle));
        double y2 = mZ + 1E-5F * Math.cos(Math.toRadians(mAngle));
        double[] geo1;
        double[] geo2;
        try {
            geo1 = Projection.to_geo(mX, mZ);
            geo2 = Projection.to_geo(x2, y2);
        } catch (CoordinateValueError e) {
            return Double.NaN;
        }
        geo1[0] = Math.toRadians(geo1[0]);
        geo1[1] = Math.toRadians(geo1[1]);
        geo2[0] = Math.toRadians(geo2[0]);
        geo2[1] = Math.toRadians(geo2[1]);
        double dlon = geo2[1] - geo1[1];
        double dlat = geo2[0] - geo1[0];
        double a = Math.toDegrees(Math.atan2(dlat, dlon*Math.cos(geo1[0])));
        a = 90 - a;
        if (a < 0) {
            a += 360;
        }
        return a;
    }
}
