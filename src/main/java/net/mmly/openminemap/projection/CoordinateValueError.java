package net.mmly.openminemap.projection;

public class CoordinateValueError extends Exception { //done
    public CoordinateValueError(double lat, double lon) {
        super("Coordnates " + lat + ", " + lon + " out of range for projection.");
    }
}
