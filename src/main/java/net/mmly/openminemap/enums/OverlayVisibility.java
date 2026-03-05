package net.mmly.openminemap.enums;

public enum OverlayVisibility {
    ALL(3),
    LOCAL(2),
    SELF(1),
    NONE(0);

    public final int numericId;
    public final String stringOf;
    private static final OverlayVisibility defaultEnum = LOCAL;

    OverlayVisibility(int numericId) {
        this.numericId = numericId;
        this.stringOf = this.toString().toLowerCase();
    }

    public static OverlayVisibility fromString(String s) {
        for (OverlayVisibility enu : OverlayVisibility.values()) {
            if (enu.stringOf.equals(s.toLowerCase())) return enu;
        }
        return defaultEnum;
    }

    public static boolean checkPermissionFor(OverlayVisibility currentPerm, OverlayVisibility requiredPerm) {
        return currentPerm.numericId >= requiredPerm.numericId;
    }

}
