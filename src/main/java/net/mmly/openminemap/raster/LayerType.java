package net.mmly.openminemap.raster;

public enum LayerType {
    BASE,
    OVERLAY,
    LOCAL_GEN;

    private static final MicroButtonFunction[] baseFunctions = new MicroButtonFunction[] {
        MicroButtonFunction.EDIT,
    };

    private static final MicroButtonFunction[] overlayFunctions = new MicroButtonFunction[] {
        MicroButtonFunction.EDIT,
        MicroButtonFunction.UP,
        MicroButtonFunction.DOWN,
        MicroButtonFunction.REMOVE,
        MicroButtonFunction.VISIBILITY
    };

    private static final MicroButtonFunction[] localFunctions = new MicroButtonFunction[] {
        MicroButtonFunction.EDIT,
        MicroButtonFunction.UP,
        MicroButtonFunction.DOWN
    };

    private static final MicroButtonFunction[] nullFunctions = new MicroButtonFunction[] {
        MicroButtonFunction.INFO
    };

    public static MicroButtonFunction[] getMicroButtons(LayerType layerType) {
        return switch (layerType) {
            case null -> nullFunctions;
            case BASE -> baseFunctions;
            case OVERLAY -> overlayFunctions;
            case LOCAL_GEN -> localFunctions;
        };
    }

}
