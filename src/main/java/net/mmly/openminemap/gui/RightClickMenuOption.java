package net.mmly.openminemap.gui;

public enum RightClickMenuOption {
    TELEPORT_HERE,
    COPY_COORDINATES,
    OPEN_IN,
    CREATE_WAYPOINT,
    EDIT_WAYPOINT,
    VIEW_ON_MAP,
    UNPIN,
    SET_SNAP_ANGLE,
    REVERSE_SEARCH;

    public String getTranslationKey() {
        return "omm.rcm." + switch (this) {
            case TELEPORT_HERE -> "teleport-here";
            case COPY_COORDINATES -> "copy-coordinates";
            case OPEN_IN -> "open-in";
            case CREATE_WAYPOINT -> "create-waypoint";
            case EDIT_WAYPOINT -> "edit-waypoint";
            case VIEW_ON_MAP -> "view-on-map";
            case UNPIN -> "unpin";
            case SET_SNAP_ANGLE -> "set-snap-angle";
            case REVERSE_SEARCH -> "reverse-search";
        };
    }



}
