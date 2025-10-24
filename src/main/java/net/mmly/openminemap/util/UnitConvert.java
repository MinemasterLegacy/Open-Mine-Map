package net.mmly.openminemap.util;

import net.mmly.openminemap.gui.FullscreenMapScreen;

import java.text.DecimalFormat;

public class UnitConvert {

    // long/lat and x/y equations can be found at https://www.desmos.com/calculator/x8chytp9bq

    //mx and my = mapx and mapy, they refer to the mercator coordinate system
    public static double mxToLong(double x, int zoom) {
        double mapSize = (128 * Math.pow(2, zoom));
        return x / (mapSize / 360) - 180;
    }

    public static double myToLat(double y, int zoom) {
        //following conversion is due to how the mapTilePosY numbers go top to bottom, while the equation was made for bottom to top numbers
        y = -1 * (y - Math.pow(2, FullscreenMapScreen.trueZoomLevel + 7));

        double mapSize = (128 * Math.pow(2, zoom));
        return ((360 * (Math.atan(Math.pow(Math.E, (2 * Math.PI * (-y + (mapSize / 2))) / mapSize)) - (Math.PI/4))) / -Math.PI);
    }

    public static double longToMx(double longitude, int zoom, int scaledSize) {
        double mapSize = (scaledSize * Math.pow(2, zoom));
        return (longitude + 180) * (mapSize / 360);
    }

    public static double latToMy(double latitude, int zoom, int scaledSize) {
        double mapSize = (scaledSize * Math.pow(2, zoom));
        return (mapSize / 2) - (((Math.log(Math.tan((Math.PI / 4) + (((latitude * Math.PI)/180) / 2)))/Math.log(Math.E)) * mapSize) / (2 * Math.PI));
    }

    public static String floorToPlace(double n, int place) { //rounds a decimal number to a certain digit, always rounding down
        DecimalFormat df = new DecimalFormat("0."+ ("0".repeat(place)));
        return df.format(n);
    }

    public static float scaledToPixelCoords(float x) {
        return (x*((float)FullscreenMapScreen.windowHeight/FullscreenMapScreen.windowScaledHeight));
    }

    public static float pixelToScaledCoords(float x) {
        return (x*((float)FullscreenMapScreen.windowScaledHeight/FullscreenMapScreen.windowHeight));
    }

}
