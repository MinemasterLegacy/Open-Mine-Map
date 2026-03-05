package net.mmly.openminemap.gui;

public enum RightClickMenuOption {
    TELEPORT_HERE("teleport-here"),
    COPY_COORDINATES("copy-coordinates"),
    OPEN_IN("open-in"),
    CREATE_WAYPOINT("create-waypoint"),
    EDIT_WAYPOINT("edit-waypoint"),
    VIEW_ON_MAP("view-on-map"),
    UNPIN("unpin"),
    SET_SNAP_ANGLE("set-snap-angle"),
    REVERSE_SEARCH("reverse-search"),
    NAME(null);

    private final String translationSubKey;

    RightClickMenuOption(String translationSubKey) {
        this.translationSubKey = translationSubKey;
    }

    public String getTranslationKey() {
        return "omm.rcm." + translationSubKey;
    }

}
