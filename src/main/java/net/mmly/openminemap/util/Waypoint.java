package net.mmly.openminemap.util;

import net.minecraft.util.Identifier;
import net.mmly.openminemap.maps.OmmMap;

public class Waypoint {

    public double longitude;
    public double latitude;
    public Identifier identifier;

    //mapxy here refer to the position at the lowest possible zoom level (18)
    private int mapX;
    private int mapY;

    public Waypoint(Identifier identifier, int mapX, int mapY, int zoom) {
        this.identifier = identifier;
        this.mapX = (int) (mapX * Math.pow(2, 18 - zoom));
        this.mapY = (int) (mapY * Math.pow(2, 18 - zoom));
        this.latitude = UnitConvert.myToLat(mapY, zoom);
        this.longitude = UnitConvert.mxToLong(mapX, zoom);
    }

    public Waypoint(Identifier identifier, double latitude, double longitude) {
        this.identifier = identifier;
        this.longitude = longitude;
        this.latitude = latitude;
        this.mapX = (int) UnitConvert.longToMapX(longitude, 18, OmmMap.tileSize);
        this.mapY = (int) UnitConvert.latToMapY(latitude, 18, OmmMap.tileSize);
    }

    public int getMapX(int zoom) {
        return (int) (mapX / Math.pow(2, 18 - zoom));
    }

    public int getMapY(int zoom) {
        return (int) (mapY / Math.pow(2, 18 - zoom));
    }

}
