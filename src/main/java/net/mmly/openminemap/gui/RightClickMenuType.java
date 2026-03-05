package net.mmly.openminemap.gui;

public enum RightClickMenuType {
    HIDDEN(false),
    DEFAULT(false),
    WAYPOINT(true),
    PINNED_WAYPOINT(true),
    SCREEN_WAYPOINT(true);

    public final boolean isWaypointType;

    RightClickMenuType(boolean isWaypointType) {
        this.isWaypointType = isWaypointType;
    }
}
