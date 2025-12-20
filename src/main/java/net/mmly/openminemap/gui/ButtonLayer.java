package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.config.ConfigScreen;
import net.mmly.openminemap.config.MapConfigScreen;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.waypoint.WaypointScreen;

public class ButtonLayer extends ClickableWidget {

    private ButtonFunction function;

    public ButtonLayer(int x, int y, int width, int height, ButtonFunction f) {
        super(x, y, width, height, Text.empty());
        function = f;
    }


    //function - 0 is zoomin, 1 is zoomout, 2 is reset, 3 is follow, 4 is config, 5 is exit

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000); //0x00000000
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        FullscreenMapScreen.disableRightClickMenu();
        switch (function) {
            case ButtonFunction.ZOOMIN: //zoom in
                FullscreenMapScreen.zoomIn();
                break;
            case ButtonFunction.ZOOMOUT: //zoom out
                FullscreenMapScreen.zoomOut();
                break;
            case ButtonFunction.RESET: //reset
                FullscreenMapScreen.resetMap();
                break;
            case ButtonFunction.FOLLOW: //follow
                if (PlayerAttributes.positionIsValid()) FullscreenMapScreen.followPlayer();
                break;
            case ButtonFunction.CONFIG: //config
                MinecraftClient.getInstance().setScreen(
                        new ConfigScreen()
                );
                break;
            case ButtonFunction.EXIT: //exit
                if (MinecraftClient.getInstance().currentScreen.getTitle().equals(Text.of("OMM Map Config"))) {
                    MapConfigScreen.revertChanges();
                    MinecraftClient.getInstance().setScreen(
                            new ConfigScreen()
                    );
                    break;
                }
                if (MinecraftClient.getInstance().currentScreen.getTitle().equals(Text.of("OMM Config"))) {
                    MinecraftClient.getInstance().setScreen(
                            new FullscreenMapScreen()
                    );
                    break;
                }
                MinecraftClient.getInstance().currentScreen.close();
                break;
            case ButtonFunction.WAYPOINTS:
                MinecraftClient.getInstance().setScreen(
                        new WaypointScreen()
                );
                break;
            case ButtonFunction.CHECKMARK:
                if (MinecraftClient.getInstance().currentScreen.getTitle().equals(Text.of("OMM Config"))) {
                    ConfigScreen.getInstance().saveChanges();
                    MinecraftClient.getInstance().setScreen(
                            new FullscreenMapScreen()
                    );
                    break;
                } else if (MinecraftClient.getInstance().currentScreen.getTitle().equals(Text.of("OMM Map Config"))) {
                    MapConfigScreen.saveChanges();
                } else {
                    break;
                }
                MinecraftClient.getInstance().setScreen(null);
                break;
            case ButtonFunction.RESET_CONFIG:
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
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }
}
