package net.mmly.openminemap.waypoint;

public enum WaypointStyle {
    DIAMOND,
    STAR,
    HOUSE,
    CITY,
    CROSS;

    private static final WaypointStyle defaultStyle = DIAMOND;

    public static WaypointStyle getByOrdinal(int o) {
        o = ((o % 5) + 5) % 5;
        for (WaypointStyle enu : WaypointStyle.values()) {
            if (enu.ordinal() == o) return enu;
        }
        return defaultStyle;
    }

    public static WaypointStyle getByString(String s) {
        for (WaypointStyle enu : WaypointStyle.values()) {
            if (enu.toString().toLowerCase().equals(s)) return enu;
        }
        return defaultStyle;
    }

}
