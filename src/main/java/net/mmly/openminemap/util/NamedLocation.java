package net.mmly.openminemap.util;

public class NamedLocation {
    public double longitude;
    public double latitude;
    public String name;
    public double angle;

    public NamedLocation(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
