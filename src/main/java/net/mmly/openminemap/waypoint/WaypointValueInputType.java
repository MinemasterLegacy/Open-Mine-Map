package net.mmly.openminemap.waypoint;

import net.minecraft.text.Text;

public enum WaypointValueInputType {
    NAME("omm.text.name"),
    LATITUDE("omm.text.latitude"),
    LONGITUDE("omm.text.longitude"),
    SNAP_ANGLE("omm.config.option.snap-angle");

    private final String translationKey;

    WaypointValueInputType(String translationKey) {
        this.translationKey = translationKey;
    }

    public boolean isNumber() {
        return !this.equals(NAME);
    }

    public boolean isCoordinate() {
        return (this.equals(LATITUDE) || this.equals(LONGITUDE));
    }

    public String getTranslatedString() {
        return Text.translatable(translationKey).getString();
    }
}
