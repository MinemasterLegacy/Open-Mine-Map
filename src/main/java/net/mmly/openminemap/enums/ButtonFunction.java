package net.mmly.openminemap.enums;

public enum ButtonFunction {
    ZOOMIN("zoomin.png"),
    ZOOMOUT("zoomout.png"),
    RESET("reset.png"),
    FOLLOW("follow.png"),
    WAYPOINTS("waypoint.png"),
    CONFIG("config.png"),
    EXIT("exit.png"),
    CHECKMARK("check.png"),
    RESETCONFIG("resetconfig.png");

    public final String textureFileName;

    ButtonFunction(String textureFileName) {
        this.textureFileName = textureFileName;
    }

    public static ButtonFunction getEnumOf(int i) {
        for (ButtonFunction enu : ButtonFunction.values()) {
            if (enu.ordinal() == i) return enu;
        }
        return null;
    }
}
