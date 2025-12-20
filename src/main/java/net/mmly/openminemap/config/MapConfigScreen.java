package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.RepositionType;
import net.mmly.openminemap.enums.ResizeDirection;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.util.ConfigFile;

public class MapConfigScreen extends Screen {

    private final static Identifier horzAdjust = Identifier.of("openminemap", "resizehorizontal.png");
    private final static Identifier vertAdjust = Identifier.of("openminemap", "resizevertical.png");
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
    private static Identifier[] exitIdentifiers;
    private static Identifier[] saveIdentifiers;
    private static Identifier[] resetConfigIdentifiers;
    private static Window window;
    private static int windowWidth = 640;
    private static int windowHeight = 480;

    protected MapConfigScreen() {
        super(Text.of("OMM Map Config"));
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            revertChanges();
            MinecraftClient.getInstance().setScreen(
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
    protected void renderDarkening(DrawContext context) {}
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

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
                Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_X)),
                Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_Y)),
                Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_WIDTH)),
                Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_HEIGHT))
        );
        HudMap.hudCompassX = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_X));
        HudMap.hudCompassY = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_Y));
        HudMap.hudCompassWidth = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_WIDTH));
    }

    private void updateScreenDims() {
        window = MinecraftClient.getInstance().getWindow();
        windowWidth = window.getScaledWidth();
        windowHeight = window.getScaledHeight();
    }

    @Override
    protected void init() {
        super.init();

        if (!HudMap.hudEnabled) HudMap.toggleEnabled();
        if (!HudMap.renderHud) HudMap.toggleRendering();
        updateScreenDims();

        saveIdentifiers = new Identifier[] {
                Identifier.of("openminemap", "buttons/vanilla/default/check.png"),
                Identifier.of("openminemap", "buttons/vanilla/hover/check.png")
        };
        saveButton = new ButtonLayer(0, 0,20, 20, ButtonFunction.CHECKMARK);
        saveButton.setTooltip(Tooltip.of(Text.translatable("omm.config.gui.save-and-exit")));
        this.addDrawableChild(saveButton);

        exitIdentifiers = new Identifier[] {
                Identifier.of("openminemap", "buttons/vanilla/default/exit.png"),
                Identifier.of("openminemap", "buttons/vanilla/hover/exit.png")
        };
        exitButton = new ButtonLayer(0, 0,20, 20, ButtonFunction.EXIT);
        exitButton.setTooltip(Tooltip.of(Text.translatable("omm.config.gui.exit-without-saving")));
        this.addDrawableChild(exitButton);

        resetConfigIdentifiers = new Identifier[] {
                Identifier.of("openminemap", "buttons/vanilla/default/resetconfig.png"),
                Identifier.of("openminemap", "buttons/vanilla/hover/resetconfig.png")
        };
        resetConfigButton = new ButtonLayer(0, 0, 20, 20, ButtonFunction.RESET_CONFIG);
        resetConfigButton.setTooltip(Tooltip.of(Text.translatable("omm.config.gui.reset-to-default")));
        this.addDrawableChild(resetConfigButton);

        rightResize = new ResizeElement(0, 0, ResizeDirection.RIGHT_MAP);
        leftResize = new ResizeElement(0, 0, ResizeDirection.LEFT_MAP);
        downResize = new ResizeElement(0, 0, ResizeDirection.DOWN_MAP);
        upResize = new ResizeElement(0, 0, ResizeDirection.UP_MAP);
        compassLeftResize = new ResizeElement(0, 0, ResizeDirection.LEFT_COMPASS);
        compassRightResize = new ResizeElement(0, 0, ResizeDirection.RIGHT_COMPASS);
        updateResizePos();

        this.addDrawableChild(rightResize);
        this.addDrawableChild(leftResize);
        this.addDrawableChild(downResize);
        this.addDrawableChild(upResize);
        this.addDrawableChild(compassLeftResize);
        this.addDrawableChild(compassRightResize);

        repositionElement = new RepositionElement(RepositionType.MAP);
        compassRepositionElement = new RepositionElement(RepositionType.COMPASS);
        this.addDrawableChild(repositionElement);
        this.addDrawableChild(compassRepositionElement);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        updateScreenDims();
        //Blue rectangle overlay
        //context.fill(HudMap.hudMapX, HudMap.hudMapY, HudMap.hudMapX2, HudMap.hudMapY2, 0xFFCEE1E4);

        rightResize.drawWidget(context);
        leftResize.drawWidget(context);
        upResize.drawWidget(context);
        downResize.drawWidget(context);
        compassLeftResize.drawWidget(context);
        compassRightResize.drawWidget(context);

        saveButton.setPosition((windowWidth / 2 - 10), (windowHeight / 2 - 10));
        context.drawTexture(RenderPipelines.GUI_TEXTURED, saveButton.isHovered() ? saveIdentifiers[1] : saveIdentifiers[0], saveButton.getX(), saveButton.getY(), 0, 0, 20, 20, 20, 20);

        exitButton.setPosition((windowWidth / 2 - 10), (windowHeight / 2 - 10 + 24));
        context.drawTexture(RenderPipelines.GUI_TEXTURED, exitButton.isHovered() ? exitIdentifiers[1] : exitIdentifiers[0], exitButton.getX(), exitButton.getY(), 0, 0, 20, 20, 20, 20);

        resetConfigButton.setPosition((windowWidth / 2 - 10), (windowHeight / 2 - 10 - 24));
        context.drawTexture(RenderPipelines.GUI_TEXTURED, resetConfigButton.isHovered() ? resetConfigIdentifiers[1] : resetConfigIdentifiers[0], resetConfigButton.getX(), resetConfigButton.getY(), 0, 0, 20, 20, 20, 20);


    }
}
