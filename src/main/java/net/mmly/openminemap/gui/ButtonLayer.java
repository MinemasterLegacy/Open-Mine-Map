package net.mmly.openminemap.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.config.ConfigScreen;
import net.mmly.openminemap.config.MapConfigScreen;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.raster.CreateRasterScreen;
import net.mmly.openminemap.raster.ViewSetRastersScreen;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.waypoint.WaypointScreen;

import java.util.function.BooleanSupplier;

public class ButtonLayer extends AbstractWidget {

    private final ButtonFunction function;
    private BooleanSupplier disableCondition;
    private static final int BUTTONSIZE = 20;
    public static boolean texturedButtons = ConfigOptions.BUTTON_STYLE.getAsBooleanFromValues(ConfigOptions.Values.BUTTON_STYLES);

    public ButtonLayer(ButtonFunction f) {
        this(0, 0, f);
    }

    public ButtonLayer(int x, int y, ButtonFunction f) {
        this(x, y, f, null);
    }

    public ButtonLayer(int x, int y, ButtonFunction f, BooleanSupplier disableCondition) {
        super(x, y, BUTTONSIZE, BUTTONSIZE, Component.empty());
        function = f;
        if (disableCondition == null) this.disableCondition = () -> false;
        else this.disableCondition = disableCondition;
    }

    public void drawWidget(GuiGraphicsExtractor context) {
        if (MapScreen.semiTransparentUi && !isHovered()) UContext.setTextureAlpha(UContext.SEMI_TRANSPARENT_UI_ALPHA);
        if (!texturedButtons) {
            UContext.drawButtonOnWidget(this, disableCondition.getAsBoolean(), isHovered());
            UContext.drawTexture(function.generatedShadowIdentifier, getX() + 1, getY() + 1, BUTTONSIZE, BUTTONSIZE);
        }
        UContext.drawTexture(
                function.getIdentifier(disableCondition.getAsBoolean(), isHovered()),
                getX(),
                getY(),
                BUTTONSIZE,
                BUTTONSIZE
        );
        UContext.resetTextureAlpha();
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000); //0x00000000
        drawWidget(context);
        if (this.isHovered()) {
            context.requestCursor(!disableCondition.getAsBoolean() ? CursorTypes.POINTING_HAND : CursorTypes.NOT_ALLOWED);
        }
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        RightClickMenu.disableMenu();
        switch (function) {
            case ZOOMIN: //zoom in
                MapScreen.zoomIn();
                break;
            case ZOOMOUT: //zoom out
                MapScreen.zoomOut();
                break;
            case RESET: //reset
                MapScreen.resetMap();
                break;
            case FOLLOW: //follow
                if (PlayerAttributes.positionIsValid()) MapScreen.followPlayer(true);
                break;
            case CONFIG: //config
                Minecraft.getInstance().setScreen(
                        new ConfigScreen()
                );
                break;
            case EXIT: //exit
                Minecraft.getInstance().screen.onClose();
                break;
            case WAYPOINTS:
                Minecraft.getInstance().setScreen(
                        new WaypointScreen()
                );
                break;
            case CHECKMARK:
                if (Minecraft.getInstance().screen instanceof ConfigScreen) {
                    ConfigScreen.getInstance().saveChanges();
                    Minecraft.getInstance().setScreen(
                            new MapScreen()
                    );
                    MapScreen.updateAltScreenMap(ConfigScreen.getInstance());
                    break;
                } else if (Minecraft.getInstance().screen instanceof MapConfigScreen) {
                    MapConfigScreen.saveChanges();
                    MapScreen.updateAltScreenMap(Minecraft.getInstance().screen, null);
                } else {
                    break;
                }
                Minecraft.getInstance().setScreen(null);
                break;
            case RESETCONFIG:
                HudMap.map.setRenderPositionAndSize(
                        Integer.parseInt(ConfigFile.readDefaultParameter(ConfigOptions.HUD_MAP_X)),
                        Integer.parseInt(ConfigFile.readDefaultParameter(ConfigOptions.HUD_MAP_Y)),
                        Integer.parseInt(ConfigFile.readDefaultParameter(ConfigOptions.HUD_MAP_WIDTH)),
                        Integer.parseInt(ConfigFile.readDefaultParameter(ConfigOptions.HUD_MAP_HEIGHT))
                );
                HudMap.hudCompassX = Integer.parseInt(ConfigFile.readDefaultParameter(ConfigOptions.HUD_COMPASS_X));
                HudMap.hudCompassY = Integer.parseInt(ConfigFile.readDefaultParameter(ConfigOptions.HUD_COMPASS_Y));
                HudMap.hudCompassWidth = Integer.parseInt(ConfigFile.readDefaultParameter(ConfigOptions.HUD_COMPASS_WIDTH));
                MapConfigScreen.updateResizePos();
                break;
            case RASTER:
                Minecraft.getInstance().setScreen(
                        new ViewSetRastersScreen(true)
                );
                break;
            case ADD:
                //unused
                break;
            case ADDRASTER:
                CreateRasterScreen.instance.addRasterField();
                break;
            case REMOVERASTER:
                CreateRasterScreen.instance.removeRasterField();
                break;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }
}
