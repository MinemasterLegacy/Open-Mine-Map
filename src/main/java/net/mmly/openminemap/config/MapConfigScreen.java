package net.mmly.openminemap.config;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.RepositionType;
import net.mmly.openminemap.enums.ResizeDirection;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.util.ConfigFile;

public class MapConfigScreen extends Screen {

    private static ResizeElement upResize;
    private static ResizeElement rightResize;
    private static ResizeElement downResize;
    private static ResizeElement leftResize;
    private static ResizeElement compassLeftResize;
    private static ResizeElement compassRightResize;
    private static RepositionElement repositionElement;
    private static RepositionElement compassRepositionElement;
    private static ButtonLayer exitButton;
    private static ButtonLayer saveButton;
    private static ButtonLayer resetConfigButton;
    private static Window window;
    private static int windowWidth = 640;
    private static int windowHeight = 480;

    protected MapConfigScreen() {
        super(Component.nullToEmpty("OMM Map Config"));
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.input() == 256 && this.shouldCloseOnEsc()) {
            revertChanges();
            Minecraft.getInstance().setScreen(
                    new ConfigScreen()
            );
            return true;
        }
        return true;
    }

    public static void updateResizePos() {
        rightResize.setPosition(HudMap.map.getRenderAreaX2() - 3, (int) (HudMap.map.getHeightMidpoint() - 10));
        leftResize.setPosition(HudMap.map.getRenderAreaX() - 4, (int) (HudMap.map.getHeightMidpoint() - 10));
        downResize.setPosition((int) (HudMap.map.getWidthMidpoint() - 10), HudMap.map.getRenderAreaY2() - 3);
        upResize.setPosition((int) (HudMap.map.getWidthMidpoint() - 10), HudMap.map.getRenderAreaY() - 4);
        compassRightResize.setPosition(HudMap.hudCompassX + HudMap.hudCompassWidth - 3, HudMap.hudCompassY - 2);
        compassLeftResize.setPosition(HudMap.hudCompassX - 4, HudMap.hudCompassY - 2);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {}

    public static void saveChanges() {
        ConfigFile.writeParameter(ConfigOptions.HUD_MAP_X, Integer.toString(HudMap.map.getRenderAreaX()));
        ConfigFile.writeParameter(ConfigOptions.HUD_MAP_Y, Integer.toString(HudMap.map.getRenderAreaY()));
        ConfigFile.writeParameter(ConfigOptions.HUD_MAP_WIDTH, Integer.toString(HudMap.map.getRenderAreaWidth()));
        ConfigFile.writeParameter(ConfigOptions.HUD_MAP_HEIGHT, Integer.toString(HudMap.map.getRenderAreaHeight()));
        ConfigFile.writeParameter(ConfigOptions.HUD_COMPASS_X, Integer.toString(HudMap.hudCompassX));
        ConfigFile.writeParameter(ConfigOptions.HUD_COMPASS_Y, Integer.toString(HudMap.hudCompassY));
        ConfigFile.writeParameter(ConfigOptions.HUD_COMPASS_WIDTH, Integer.toString(HudMap.hudCompassWidth));
        ConfigFile.writeToFile();
    }

    public static void revertChanges() {
        HudMap.map.setRenderPositionAndSize(
                ConfigOptions.HUD_MAP_X.getAsInt(),
                ConfigOptions.HUD_MAP_Y.getAsInt(),
                ConfigOptions.HUD_MAP_WIDTH.getAsInt(),
                ConfigOptions.HUD_MAP_HEIGHT.getAsInt()
        );
        HudMap.hudCompassX = ConfigOptions.HUD_COMPASS_X.getAsInt();
        HudMap.hudCompassY = ConfigOptions.HUD_COMPASS_Y.getAsInt();
        HudMap.hudCompassWidth = ConfigOptions.HUD_COMPASS_WIDTH.getAsInt();
    }

    private void updateScreenDims() {
        window = Minecraft.getInstance().getWindow();
        windowWidth = window.getGuiScaledWidth();
        windowHeight = window.getGuiScaledHeight();
    }

    @Override
    protected void init() {
        super.init();

        if (!HudMap.hudEnabled) HudMap.toggleEnabled();
        if (!HudMap.renderHud) HudMap.toggleRendering();
        updateScreenDims();

        saveButton = new ButtonLayer(ButtonFunction.CHECKMARK);
        saveButton.setTooltip(Tooltip.create(Component.translatable("omm.config.gui.save-and-exit")));
        this.addRenderableWidget(saveButton);

        exitButton = new ButtonLayer(ButtonFunction.EXIT);
        exitButton.setTooltip(Tooltip.create(Component.translatable("omm.config.gui.exit-without-saving")));
        this.addRenderableWidget(exitButton);

        resetConfigButton = new ButtonLayer(ButtonFunction.RESETCONFIG);
        resetConfigButton.setTooltip(Tooltip.create(Component.translatable("omm.config.gui.reset-to-default")));
        this.addRenderableWidget(resetConfigButton);

        rightResize = new ResizeElement(0, 0, ResizeDirection.RIGHT_MAP);
        leftResize = new ResizeElement(0, 0, ResizeDirection.LEFT_MAP);
        downResize = new ResizeElement(0, 0, ResizeDirection.DOWN_MAP);
        upResize = new ResizeElement(0, 0, ResizeDirection.UP_MAP);
        compassLeftResize = new ResizeElement(0, 0, ResizeDirection.LEFT_COMPASS);
        compassRightResize = new ResizeElement(0, 0, ResizeDirection.RIGHT_COMPASS);
        updateResizePos();

        this.addRenderableWidget(rightResize);
        this.addRenderableWidget(leftResize);
        this.addRenderableWidget(downResize);
        this.addRenderableWidget(upResize);
        if (HudMap.showCompass) {
            this.addRenderableWidget(compassLeftResize);
            this.addRenderableWidget(compassRightResize);
        }

        repositionElement = new RepositionElement(RepositionType.MAP);
        compassRepositionElement = new RepositionElement(RepositionType.COMPASS);
        this.addRenderableWidget(repositionElement);
        if (HudMap.showCompass) this.addRenderableWidget(compassRepositionElement);

    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        HudMap.render(context, null);
        saveButton.setPosition((windowWidth / 2 - 10), (windowHeight / 2 - 10));
        exitButton.setPosition((windowWidth / 2 - 10), (windowHeight / 2 - 10 + 24));
        resetConfigButton.setPosition((windowWidth / 2 - 10), (windowHeight / 2 - 10 - 24));

        super.extractRenderState(context, mouseX, mouseY, delta);
        updateScreenDims();
        //Blue rectangle overlay
        //context.fill(HudMap.hudMapX, HudMap.hudMapY, HudMap.hudMapX2, HudMap.hudMapY2, 0xFFCEE1E4);

        rightResize.drawWidget(context);
        leftResize.drawWidget(context);
        upResize.drawWidget(context);
        downResize.drawWidget(context);

        if (!HudMap.showCompass) return;
        compassLeftResize.drawWidget(context);
        compassRightResize.drawWidget(context);
    }

    @Override
    public void onClose() {
        MapConfigScreen.revertChanges();
        Minecraft.getInstance().setScreen(new ConfigScreen());
        MapScreen.updateAltScreenMap(this);
    }
}
