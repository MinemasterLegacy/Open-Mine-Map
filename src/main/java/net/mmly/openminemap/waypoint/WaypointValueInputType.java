package net.mmly.openminemap.waypoint;

import net.minecraft.text.Text;

public enum WaypointValueInputType {
    NAME,
    LATITUDE,
    LONGITUDE,
    SNAP_ANGLE;

    public boolean isNumber() {
        return !this.equals(NAME);
    }

    public boolean isCoordinate() {
        return (this.equals(LATITUDE) || this.equals(LONGITUDE));
    }

    public String getTranslatedString() {
        return switch (this) {
            case NAME -> Text.translatable("omm.text.name").getString();
            case LATITUDE -> Text.translatable("omm.text.latitude").getString();
            case LONGITUDE -> Text.translatable("omm.text.longitude").getString();
            case SNAP_ANGLE -> Text.translatable("omm.config.option.snap-angle").getString();
        };
    }
}
