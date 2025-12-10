package net.mmly.openminemap.waypoint;

public enum WaypointValueInputType {
    NAME,
    LATITUDE,
    LONGITUDE,
    SNAP_ANGLE;

    public boolean isNumber() {
        return !this.equals(NAME);
    }

    public boolean isCoordinate() {
        return (this.equals(LATITUDE) || this.equals(LONGITUDE));
    }
}
