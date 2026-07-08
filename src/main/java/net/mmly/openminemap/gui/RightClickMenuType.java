package net.mmly.openminemap.gui;

public enum RightClickMenuType {
    HIDDEN(false),
    DEFAULT(false),
    WAYPOINT(true),
    PINNED_WAYPOINT(true),
    SCREEN_WAYPOINT(true),
    SEARCH_LOCATION(true);

    public final boolean isLocationType;

    RightClickMenuType(boolean isWaypointType) {
        this.isLocationType = isWaypointType;
    }
}
