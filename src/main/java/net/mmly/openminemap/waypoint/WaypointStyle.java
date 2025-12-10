package net.mmly.openminemap.waypoint;

public enum WaypointStyle {
    DIAMOND,
    X,
    STAR,
    HOUSE,
    CITY,
    CROSS,
    PLANE;

    public static WaypointStyle getByOrdinal(int o) {
        o = ((o % 7) + 7) % 7;
        return switch (o) {
            case 0 -> DIAMOND;
            case 1 -> X;
            case 2 -> STAR;
            case 3 -> HOUSE;
            case 4 -> CITY;
            case 5 -> CROSS;
            case 6 -> PLANE;
            default -> DIAMOND;
        };
    }

    public static WaypointStyle getByString(String s) {
        return switch (s) {
            case "diamond" -> DIAMOND;
            case "x" -> X;
            case "star" -> STAR;
            case "house" -> HOUSE;
            case "city" -> CITY;
            case "cross" -> CROSS;
            case "plane" -> PLANE;
            default -> DIAMOND;
        };
    }

}
