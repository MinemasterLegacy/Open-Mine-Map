package net.mmly.openminemap.enums;

public enum OverlayVisibility {
    ALL, //currently should be unused, global players will be added when I make a server side mod version
    LOCAL,
    SELF,
    NONE;

    public static int getNumericIdOf(OverlayVisibility v) {
        if (v == null) return 0;
        return switch (v) {
            case ALL -> 3;
            case LOCAL -> 2;
            case SELF -> 1;
            case NONE -> 0;
        };
    }

    public static OverlayVisibility getEnumOf(int i) {
        return switch (i) {
            case 3 -> ALL;
            case 2 -> LOCAL;
            case 1 -> SELF;
            case 0 -> NONE;
            default -> null;
        };
    }

    public static OverlayVisibility fromString(String s) {
        return switch (s.toLowerCase()) {
            case "all" -> ALL;
            case "local" -> LOCAL;
            case "self" -> SELF;
            case "none" -> NONE;
            default -> LOCAL;
        };
    }

    public static boolean checkPermissionFor(OverlayVisibility currentPerm, OverlayVisibility requiredPerm) {
        return getNumericIdOf(currentPerm) >= getNumericIdOf(requiredPerm);
    }

}
