package net.mmly.openminemap.util;

import net.mmly.openminemap.gui.FullscreenMapScreen;

import java.text.DecimalFormat;

public class UnitConvert {

    // long/lat and x/y equations can be found at https://www.desmos.com/calculator/x8chytp9bq
    private static double getMapSize(double zoom, int tileScaledSize) {
        return tileScaledSize * Math.pow(2, Math.round(zoom));
    }

    //mx and my = mapx and mapy, they refer to the mercator coordinate system
    public static double mxToLong(double x, double zoom, int scaledSize) {
        double mapSize = getMapSize(zoom, scaledSize);
        return x / (mapSize / 360) - 180;
    }

    public static double myToLat(double y, double zoom, int scaledSize) {
        double mapSize = getMapSize(zoom, scaledSize);
        //following conversion is due to how the mapTilePosY numbers go top to bottom, while the equation was made for bottom to top numbers

        y = -1 * (y - mapSize);

        return ((360 * (Math.atan(Math.pow(Math.E, (2 * Math.PI * (-y + (mapSize / 2))) / mapSize)) - (Math.PI/4))) / -Math.PI);
    }

    public static double longToMapX(double longitude, double zoom, int scaledSize) {
        double mapSize = getMapSize(zoom, scaledSize);
        return (longitude + 180) * (mapSize / 360);
    }

    public static double latToMapY(double latitude, double zoom, int scaledSize) {
        double mapSize = getMapSize(zoom, scaledSize);
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

    public static double[] toDecimalDegrees(String lat, String lon) {
        /*
            Converts: (various other coordinate formats)
                #°#'#''D  #°#'#''D
                #°#'#"D   #°#'#"D
                #°#.#'D   #°#.#'D
                #.#°D     #.#°D
                #.#°      #.#°
            To: (decimal degrees w/ negatives instead of direction)
                #.#       #.#
         */
        if (lon.length() < 3 || lat.length() < 3) {
            try {
                return new double[] {Double.parseDouble(lat), Double.parseDouble(lon)};
            } catch (NumberFormatException e) {
                return null;
            }
        };

        lat = lat.trim().toUpperCase();
        lon = lon.trim().toUpperCase();
        String testSuffix = lat.substring(lat.length()-3);
        double[] convertedCoords = new double[2];
        try {
            if (testSuffix.matches("[0-9]\"[NSEW]") || testSuffix.matches("''[NSEW]")) {
                //System.out.println("Detected DMS");
                //degrees minutes seconds
                lat = lat.replace("''", "\"");
                lon = lon.replace("''", "\"");
                String[] splitLat = lat.split("[\"°']");
                String[] splitLon = lon.split("[\"°']");
                Double[] degMinSec = new Double[6];
                for (int i = 0; i < 3; i++) {
                    degMinSec[i] = Double.parseDouble(splitLat[i]);
                }
                for (int i = 3; i < 6; i++) {
                    degMinSec[i] = Double.parseDouble(splitLon[i-3]);
                }
                convertedCoords[0] = (degMinSec[0] + degMinSec[1] / 60 + degMinSec[2] / 3600) * (splitLat[3].equals("S") ? -1 : 1);
                convertedCoords[1] = (degMinSec[3] + degMinSec[4] / 60 + degMinSec[5] / 3600) * (splitLon[3].equals("W") ? -1 : 1);
            } else if (testSuffix.matches("[0-9]'[NSEW]")) {
                //degrees, decimal minutes
                //System.out.println("Detected DDM");
                String[] splitLat = lat.split("['°]");
                String[] splitLon = lon.split("['°]");
                Double[] degMin = new Double[4];
                for (int i = 0; i < 2; i++) {
                    degMin[i] = Double.parseDouble(splitLat[i]);
                }
                for (int i = 2; i < 4; i++) {
                    degMin[i] = Double.parseDouble(splitLon[i-2]);
                }
                convertedCoords[0] = (degMin[0] + degMin[1] / 60) * (splitLon[2].equals("S") ? -1 : 1);
                convertedCoords[1] = (degMin[2] + degMin[3] / 60) * (splitLon[2].equals("W") ? -1 : 1);
            } else if (testSuffix.matches("[0-9]°[NSEW]")) {
                //decimal degrees w/ direction
                //System.out.println("Detected DDwD");
                convertedCoords[0] = Double.parseDouble(lat.substring(0, lat.length() - 2));
                convertedCoords[1] = Double.parseDouble(lon.substring(0, lon.length() - 2));
                if (lat.endsWith("S")) convertedCoords[0] *= -1;
                if (lon.endsWith("W")) convertedCoords[1] *= -1;
            } else if (lat.endsWith("°")) {
                //System.out.println("Detected DDwO");
                convertedCoords[0] = Double.parseDouble(lat.substring(0, lat.length()-1));
                convertedCoords[1] = Double.parseDouble(lon.substring(0, lon.length()-1));
            } else {
                //System.out.println("Detected DD");
                //will assume its already correct and attempt to parse as double
                convertedCoords[0] = Double.parseDouble(lat);
                convertedCoords[1] = Double.parseDouble(lon);
            }
            //System.out.println(convertedCoords[0]);
            //System.out.println(convertedCoords[1]);
            return convertedCoords;
        } catch (NumberFormatException e) {
            //System.out.println("Coordinate conversion error for: " + lat + " , " + lon);
            return null;
        }
    }

    public static int argb(int alpha, int green, int blue, int red) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
