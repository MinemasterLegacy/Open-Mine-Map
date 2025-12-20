package net.mmly.openminemap.waypoint;

public enum WaypointStyle {
    DIAMOND,
    STAR,
    HOUSE,
    CITY,
    CROSS;

    public static WaypointStyle getByOrdinal(int o) {
        o = ((o % 5) + 5) % 5;
        return switch (o) {
            case 0 -> DIAMOND;
            case 1 -> STAR;
            case 2 -> HOUSE;
            case 3 -> CITY;
            case 4 -> CROSS;
            default -> DIAMOND;
        };
    }

    public static WaypointStyle getByString(String s) {
        return switch (s) {
            case "diamond" -> DIAMOND;
            case "star" -> STAR;
            case "house" -> HOUSE;
            case "city" -> CITY;
            case "cross" -> CROSS;
            default -> DIAMOND;
        };
    }

}
