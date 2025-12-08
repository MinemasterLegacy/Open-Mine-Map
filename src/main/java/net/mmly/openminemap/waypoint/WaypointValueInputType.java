package net.mmly.openminemap.waypoint;

public enum WaypointValueInputType {
    STRING,
    LATITUDE,
    LONGITUDE,
    ANGLE;

    public boolean isNumber() {
        return !this.equals(STRING);
    }

    public boolean isCoordinate() {
        return (this.equals(LATITUDE) || this.equals(LONGITUDE));
    }
}
