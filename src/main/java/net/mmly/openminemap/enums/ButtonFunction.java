package net.mmly.openminemap.enums;

public enum ButtonFunction {
    ZOOMIN,
    ZOOMOUT,
    RESET,
    FOLLOW,
    CONFIG,
    EXIT,
    WAYPOINTS,
    CHECKMARK,
    RESETCONFIG;

    public static ButtonFunction getEnumOf(int i) {
        return switch (i) {
            case 0 -> ZOOMIN;
            case 1 -> ZOOMOUT;
            case 2 -> RESET;
            case 3 -> FOLLOW;
            case 4 -> CONFIG;
            case 5 -> EXIT;
            case 6 -> WAYPOINTS;
            case 7 -> CHECKMARK;
            case 8 -> RESETCONFIG;
            default -> null;
        };
    }

    public String getTextureFileName() {
        return switch (this) {
            case ZOOMIN -> "zoomin.png";
            case ZOOMOUT -> "zoomout.png";
            case RESET -> "reset.png";
            case FOLLOW -> "follow.png";
            case CONFIG -> "config.png";
            case EXIT -> "exit.png";
            case WAYPOINTS -> "waypoint.png";
            case CHECKMARK -> "check.png";
            case RESETCONFIG -> "resetconfig.png";
        };
    }
}
