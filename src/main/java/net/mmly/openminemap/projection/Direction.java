package net.mmly.openminemap.projection;

import net.minecraft.client.MinecraftClient;
import net.mmly.openminemap.map.PlayerAttributes;

public class Direction {
    static int delay = 0;

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
}
