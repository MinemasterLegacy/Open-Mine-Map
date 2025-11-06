package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderLayer;
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
import net.mmly.openminemap.util.UnitConvert;

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

    protected MapConfigScreen() {
        super(Text.of("OMM Map Config"));
    }

    public static void updateResizePos() {
        HudMap.updateX2Y2();
        rightResize.setPosition(HudMap.hudMapX2 - 3, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10);
        leftResize.setPosition(HudMap.hudMapX - 4, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10);
        downResize.setPosition((HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY2 - 3);
        upResize.setPosition((HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY - 4);
        compassRightResize.setPosition(HudMap.hudCompassX + HudMap.hudCompassWidth - 3, HudMap.hudCompassY - 2);
        compassLeftResize.setPosition(HudMap.hudCompassX - 4, HudMap.hudCompassY - 2);
    }

    @Override
    protected void renderDarkening(DrawContext context) {}
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    public static void saveChanges() {
        ConfigFile.writeParameter(ConfigOptions.HUD_MAP_X, Integer.toString(HudMap.hudMapX));
        ConfigFile.writeParameter(ConfigOptions.HUD_MAP_Y, Integer.toString(HudMap.hudMapY));
        ConfigFile.writeParameter(ConfigOptions.HUD_MAP_WIDTH, Integer.toString(HudMap.hudMapWidth));
        ConfigFile.writeParameter(ConfigOptions.HUD_MAP_HEIGHT, Integer.toString(HudMap.hudMapHeight));
        ConfigFile.writeParameter(ConfigOptions.HUD_COMPASS_X, Integer.toString(HudMap.hudCompassX));
        ConfigFile.writeParameter(ConfigOptions.HUD_COMPASS_Y, Integer.toString(HudMap.hudCompassY));
        ConfigFile.writeParameter(ConfigOptions.HUD_COMPASS_WIDTH, Integer.toString(HudMap.hudCompassWidth));
        ConfigFile.writeToFile();
    }

    public static void revertChanges() {
        HudMap.hudMapX = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_X));
        HudMap.hudMapY = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_Y));
        HudMap.hudMapWidth = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_WIDTH));
        HudMap.hudMapHeight = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_HEIGHT));
        HudMap.hudCompassX = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_X));
        HudMap.hudCompassY = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_Y));
        HudMap.hudCompassWidth = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_WIDTH));
        HudMap.updateX2Y2();
    }

    private void updateScreenDims() {
        window = MinecraftClient.getInstance().getWindow();
    }

    @Override
    protected void init() {
        super.init();

        if (!HudMap.renderHud) HudMap.toggleRendering();
        window = MinecraftClient.getInstance().getWindow();

        saveIdentifiers = new Identifier[] {
                Identifier.of("openminemap", "buttons/vanilla/default/check.png"),
                Identifier.of("openminemap", "buttons/vanilla/hover/check.png")
        };
        saveButton = new ButtonLayer(0, 0,20, 20, ButtonFunction.CHECKMARK);
        saveButton.setTooltip(Tooltip.of(Text.of("Save and Exit")));
        this.addDrawableChild(saveButton);

        exitIdentifiers = new Identifier[] {
                Identifier.of("openminemap", "buttons/vanilla/default/exit.png"),
                Identifier.of("openminemap", "buttons/vanilla/hover/exit.png")
        };
        exitButton = new ButtonLayer(0, 0,20, 20, ButtonFunction.EXIT);
        exitButton.setTooltip(Tooltip.of(Text.of("Exit without Saving")));
        this.addDrawableChild(exitButton);

        resetConfigIdentifiers = new Identifier[] {
                Identifier.of("openminemap", "buttons/vanilla/default/resetconfig.png"),
                Identifier.of("openminemap", "buttons/vanilla/hover/resetconfig.png")
        };
        resetConfigButton = new ButtonLayer(0, 0, 20, 20, ButtonFunction.RESET_CONFIG);
        resetConfigButton.setTooltip(Tooltip.of(Text.of("Reset to Default")));
        this.addDrawableChild(resetConfigButton);

        rightResize = new ResizeElement(0, 0, 7, 20, ResizeDirection.RIGHT_MAP);
        leftResize = new ResizeElement(0, 0, 7, 20, ResizeDirection.LEFT_MAP);
        downResize = new ResizeElement(0, 0, 20, 7, ResizeDirection.DOWN_MAP);
        upResize = new ResizeElement(0, 0, 20, 7, ResizeDirection.UP_MAP);
        compassLeftResize = new ResizeElement(0, 0, 7, 20, ResizeDirection.LEFT_COMPASS);
        compassRightResize = new ResizeElement(0, 0, 7, 20, ResizeDirection.RIGHT_COMPASS);
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

        context.drawTexture(RenderLayer::getGuiTextured, horzAdjust, HudMap.hudMapX2 - 3, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10, 0, 0, 7, 20, 7, 20); //right
        context.drawTexture(RenderLayer::getGuiTextured, horzAdjust, HudMap.hudMapX - 4, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10, 0, 0, 7, 20, 7, 20); //left
        context.drawTexture(RenderLayer::getGuiTextured, vertAdjust, (HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY2 - 3, 0, 0, 20, 7, 20, 7); //bottom
        context.drawTexture(RenderLayer::getGuiTextured, vertAdjust, (HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY - 4, 0, 0, 20, 7, 20, 7); //top
        context.drawTexture(RenderLayer::getGuiTextured, horzAdjust,HudMap.hudCompassX + HudMap.hudCompassWidth - 3, HudMap.hudCompassY - 2,0, 0, 7, 20, 7, 20);
        context.drawTexture(RenderLayer::getGuiTextured, horzAdjust, HudMap.hudCompassX - 4, HudMap.hudCompassY - 2,0, 0, 7, 20, 7, 20);

        saveButton.setPosition((int) (UnitConvert.pixelToScaledCoords(window.getWidth()) / 2 - 10), (int) (UnitConvert.pixelToScaledCoords(window.getHeight()) / 2 - 10));
        context.drawTexture(RenderLayer::getGuiTextured, saveButton.isHovered() ? saveIdentifiers[1] : saveIdentifiers[0], saveButton.getX(), saveButton.getY(), 0, 0, 20, 20, 20, 20);

        exitButton.setPosition((int) (UnitConvert.pixelToScaledCoords(window.getWidth()) / 2 - 10), (int) (UnitConvert.pixelToScaledCoords(window.getHeight()) / 2 - 10 + 24));
        context.drawTexture(RenderLayer::getGuiTextured, exitButton.isHovered() ? exitIdentifiers[1] : exitIdentifiers[0], exitButton.getX(), exitButton.getY(), 0, 0, 20, 20, 20, 20);

        resetConfigButton.setPosition((int) (UnitConvert.pixelToScaledCoords(window.getWidth()) / 2 - 10), (int) (UnitConvert.pixelToScaledCoords(window.getHeight()) / 2 - 10 - 24));
        context.drawTexture(RenderLayer::getGuiTextured, resetConfigButton.isHovered() ? resetConfigIdentifiers[1] : resetConfigIdentifiers[0], resetConfigButton.getX(), resetConfigButton.getY(), 0, 0, 20, 20, 20, 20);


    }
}
