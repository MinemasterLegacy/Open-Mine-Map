package net.mmly.openminemap.raster;

import net.mmly.openminemap.util.TileUrlFile;

public enum LayerType {
    BASE,
    OVERLAY,
    LOCAL_GEN;

    private static final MicroButtonFunction[] baseFunctions = new MicroButtonFunction[] {
        MicroButtonFunction.EDIT,
    };

    private static final MicroButtonFunction[] customFunctions = new MicroButtonFunction[] {
        MicroButtonFunction.EDIT,
        MicroButtonFunction.DELETE
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

    public static MicroButtonFunction[] getMicroButtons(LayerType layerType, boolean custom) {
        return switch (layerType) {
            case null -> custom ? customFunctions : nullFunctions;
            case BASE -> TileUrlFile.loadFailed ? nullFunctions : baseFunctions;
            case OVERLAY -> overlayFunctions;
            case LOCAL_GEN -> TileUrlFile.loadFailed ? baseFunctions : localFunctions;
        };
    }

}
