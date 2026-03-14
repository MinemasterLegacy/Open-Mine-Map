package net.mmly.openminemap.enums;

public enum OverlayVisibility {
    ALL(3),
    LOCAL(2),
    SELF(1),
    NONE(0);

    public final int id;
    public final String stringOf;
    private static final OverlayVisibility defaultEnum = ALL;

    OverlayVisibility(int id) {
        this.id = id;
        this.stringOf = this.toString().toLowerCase();
    }

    public static OverlayVisibility fromString(String s) {
        for (OverlayVisibility enu : OverlayVisibility.values()) {
            if (enu.stringOf.equals(s.toLowerCase())) return enu;
        }
        return defaultEnum;
    }

}
