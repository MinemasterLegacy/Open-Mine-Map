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
    RESET_CONFIG;

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
            case 8 -> RESET_CONFIG;
            default -> null;
        };
    }
}
