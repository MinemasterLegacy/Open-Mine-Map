package net.mmly.openminemap.enums;

public enum ButtonFunction {
    ZOOMIN("zoomin.png", 0),
    ZOOMOUT("zoomout.png", 1),
    RESET("reset.png", 2),
    FOLLOW("follow.png", 3),
    WAYPOINTS("waypoint.png", 4),
    CONFIG("config.png", 5),
    EXIT("exit.png", 6),
    CHECKMARK("check.png", 7),
    RESETCONFIG("resetconfig.png", 8),
    RASTER("raster.png", 9);

    public final String textureFileName;
    public final int id;

    ButtonFunction(String textureFileName, int id) {
        this.textureFileName = textureFileName;
        this.id = id;
    }

    public static ButtonFunction getEnumOf(int i) {
        for (ButtonFunction enu : ButtonFunction.values()) {
            if (enu.ordinal() == i) return enu;
        }
        return null;
    }

    public static ButtonFunction[] getCenterShelf() {
        return new ButtonFunction[] {
                ZOOMIN,
                ZOOMOUT,
                RESET,
                FOLLOW,
                WAYPOINTS,
                EXIT
        };
    }

    public static ButtonFunction[] getLeftShelf() {
        return new ButtonFunction[] {
                CONFIG,
                RASTER
        };
    }

}
