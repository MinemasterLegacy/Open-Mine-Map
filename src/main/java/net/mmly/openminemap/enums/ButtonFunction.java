package net.mmly.openminemap.enums;

import net.minecraft.util.Identifier;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.gui.ToggleButtonLayer;
import net.mmly.openminemap.util.ColorUtil;

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
    RASTER("raster.png", 9),
    ADD("add.png", 10),
    ADDRASTER("add.png", 11),
    REMOVERASTER("remove.png", 12);

    public final Identifier defaultIdentifier;
    public final Identifier highlightedIdentifer;
    public final Identifier disabledIdentifer;
    public final Identifier generatedIdentifier;
    public final Identifier generatedDarkIdentifier;
    public final Identifier generatedShadowIdentifier;
    public final int id;

    public Identifier getIdentifier(boolean disabled, boolean highlighted) {
        if (!ButtonLayer.texturedButtons) {
            if (disabled) return generatedDarkIdentifier;
            return generatedIdentifier;
        }
        if (disabled) return disabledIdentifer;
        if (highlighted) return highlightedIdentifer;
        return defaultIdentifier;
    }

    ButtonFunction(String textureFileName, int id) {
        this.id = id;

        defaultIdentifier = Identifier.of("openminemap", "buttons/vanilla/default/" + textureFileName);
        highlightedIdentifer = Identifier.of("openminemap", "buttons/vanilla/hover/" + textureFileName);
        disabledIdentifer = Identifier.of("openminemap", "buttons/vanilla/locked/" + textureFileName);
        generatedIdentifier = Identifier.of("openminemap", "buttons/vanilla/generated/" + textureFileName);
        generatedShadowIdentifier = ColorUtil.getColoredIdentifier(generatedIdentifier,0x00003e);
        generatedDarkIdentifier = ColorUtil.getColoredIdentifier(generatedIdentifier, 0x00007f);
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

    public static ToggleButtonLayer.Type[] getRightShelf() {
        //TODO
        return null;
    }

}
