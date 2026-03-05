package net.mmly.openminemap.enums;

public enum TileUrlErrorType {
    NO_ERROR(null),
    MALFORMED_JSON_FILE("omm.error.tile-source-json-formatting"),
    NULL_TILE_URL("omm.error.blank-tile-url"),
    NULL_VALUE("omm.error.blank-field"),
    MALFORMED_SOURCE_URL("omm.error.source-link-invalid"),
    MALFORMED_ATTRIBUTION_LINK("omm.error.attribution-link-invalid"),
    INVALID_SOURCE_URL_BRACKET_PLACEMENT("omm.error.source-bracket-placement"),
    INVALID_ATTRIBUTION_BRACKET_PLACEMENT("omm.error.attribution-bracket-placement"),
    MISMATCHED_ATTRIBUTION_LINKS("omm.error.link-number-mismatch"),
    MISSING_X_POSITION_FIELD("omm.error.field-missing-x"),
    MISSING_Y_POSITION_FIELD("omm.error.field-missing-y"),
    MISSING_ZOOM_FIELD("omm.error.field-missing-zoom");

    public final String translationKey;

    TileUrlErrorType(String translationKey) {
        this.translationKey = translationKey;
    }
}