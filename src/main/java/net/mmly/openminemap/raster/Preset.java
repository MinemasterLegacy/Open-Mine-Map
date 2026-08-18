package net.mmly.openminemap.raster;

public enum Preset {
    OPENMINEMAP(0),
    HUMANITARIAN(1),
    CYCLOSM(2),
    OPNVCARTE(3),
    OPENTOPOMAP(4),
    MAPBOX_STREETS(5),
    MAPBOX_SATELLITE(6),
    ALIDADE_SMOOTH(7),
    ALIDADE_SMOOTH_DARK(8);

    public final int templateId;

    Preset(int templateID) {
        this.templateId = templateID;
    }

}
