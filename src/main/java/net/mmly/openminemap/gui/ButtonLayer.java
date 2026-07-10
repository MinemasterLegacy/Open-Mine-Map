package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
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

public class ButtonLayer extends ClickableWidget {

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
        super(x, y, BUTTONSIZE, BUTTONSIZE, Text.empty());
        function = f;
        if (disableCondition == null) this.disableCondition = () -> false;
        else this.disableCondition = disableCondition;
    }

    public void drawWidget(DrawContext context) {
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
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000); //0x00000000
        drawWidget(context);
        if (this.isHovered()) {
            context.setCursor(!disableCondition.getAsBoolean() ? StandardCursors.POINTING_HAND : StandardCursors.NOT_ALLOWED);
        }
    }

    @Override
    public void onClick(Click click, boolean doubled) {
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
                MinecraftClient.getInstance().setScreen(
                        new ConfigScreen()
                );
                break;
            case EXIT: //exit
                MinecraftClient.getInstance().currentScreen.close();
                break;
            case WAYPOINTS:
                MinecraftClient.getInstance().setScreen(
                        new WaypointScreen()
                );
                break;
            case CHECKMARK:
                if (MinecraftClient.getInstance().currentScreen instanceof ConfigScreen) {
                    ConfigScreen.getInstance().saveChanges();
                    MinecraftClient.getInstance().setScreen(
                            new MapScreen()
                    );
                    MapScreen.updateAltScreenMap(ConfigScreen.getInstance());
                    break;
                } else if (MinecraftClient.getInstance().currentScreen instanceof MapConfigScreen) {
                    MapConfigScreen.saveChanges();
                    MapScreen.updateAltScreenMap(MinecraftClient.getInstance().currentScreen, null);
                } else {
                    break;
                }
                MinecraftClient.getInstance().setScreen(null);
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
                MinecraftClient.getInstance().setScreen(
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
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }
}
