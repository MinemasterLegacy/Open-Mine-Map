package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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
    private static Identifier[] exitIdentifiers;
    private static Identifier[] saveIdentifiers;
    private static Window window;

    protected MapConfigScreen() {
        super(Text.of("OMM Map Config"));
    }

    protected static void updateResizePos() {
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
        ConfigFile.writeParameter("HudMapX", Integer.toString(HudMap.hudMapX));
        ConfigFile.writeParameter("HudMapY", Integer.toString(HudMap.hudMapY));
        ConfigFile.writeParameter("HudMapWidth", Integer.toString(HudMap.hudMapWidth));
        ConfigFile.writeParameter("HudMapHeight", Integer.toString(HudMap.hudMapHeight));
        ConfigFile.writeParameter("HudCompassX", Integer.toString(HudMap.hudCompassX));
        ConfigFile.writeParameter("HudCompassY", Integer.toString(HudMap.hudCompassY));
        ConfigFile.writeParameter("HudCompassWidth", Integer.toString(HudMap.hudCompassWidth));
        ConfigFile.writeToFile();
    }

    public static void revertChanges() {
        HudMap.hudMapX = Integer.parseInt(ConfigFile.readParameter("HudMapX"));
        HudMap.hudMapY = Integer.parseInt(ConfigFile.readParameter("HudMapY"));
        HudMap.hudMapWidth = Integer.parseInt(ConfigFile.readParameter("HudMapWidth"));
        HudMap.hudMapHeight = Integer.parseInt(ConfigFile.readParameter("HudMapHeight"));
        HudMap.hudCompassX = Integer.parseInt(ConfigFile.readParameter("HudCompassX"));
        HudMap.hudCompassY = Integer.parseInt(ConfigFile.readParameter("HudCompassY"));
        HudMap.hudCompassWidth = Integer.parseInt(ConfigFile.readParameter("HudCompassWidth"));
        HudMap.updateX2Y2();
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
        saveButton = new ButtonLayer(0, 0,20, 20, 7);
        this.addDrawableChild(saveButton);

        exitIdentifiers = new Identifier[] {
                Identifier.of("openminemap", "buttons/vanilla/default/exit.png"),
                Identifier.of("openminemap", "buttons/vanilla/hover/exit.png")
        };
        exitButton = new ButtonLayer(0, 0,20, 20, 5);
        this.addDrawableChild(exitButton);

        rightResize = new ResizeElement(0, 0, 7, 20, 1);
        leftResize = new ResizeElement(0, 0, 7, 20, 3);
        downResize = new ResizeElement(0, 0, 20, 7, 2);
        upResize = new ResizeElement(0, 0, 20, 7, 0);
        compassLeftResize = new ResizeElement(0, 0, 7, 20, 4);
        compassRightResize = new ResizeElement(0, 0, 7, 20, 5);
        updateResizePos();

        this.addDrawableChild(rightResize);
        this.addDrawableChild(leftResize);
        this.addDrawableChild(downResize);
        this.addDrawableChild(upResize);
        this.addDrawableChild(compassLeftResize);
        this.addDrawableChild(compassRightResize);

        repositionElement = new RepositionElement(0);
        compassRepositionElement = new RepositionElement(1);
        this.addDrawableChild(repositionElement);
        this.addDrawableChild(compassRepositionElement);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        //Blue rectangle overlay
        //context.fill(HudMap.hudMapX, HudMap.hudMapY, HudMap.hudMapX2, HudMap.hudMapY2, 0xFFCEE1E4);

        context.drawTexture(RenderPipelines.GUI_TEXTURED, horzAdjust, HudMap.hudMapX2 - 3, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10, 0, 0, 7, 20, 7, 20); //right
        context.drawTexture(RenderPipelines.GUI_TEXTURED, horzAdjust, HudMap.hudMapX - 4, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10, 0, 0, 7, 20, 7, 20); //left
        context.drawTexture(RenderPipelines.GUI_TEXTURED, vertAdjust, (HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY2 - 3, 0, 0, 20, 7, 20, 7); //bottom
        context.drawTexture(RenderPipelines.GUI_TEXTURED, vertAdjust, (HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY - 4, 0, 0, 20, 7, 20, 7); //top
        context.drawTexture(RenderPipelines.GUI_TEXTURED, horzAdjust,HudMap.hudCompassX + HudMap.hudCompassWidth - 3, HudMap.hudCompassY - 2,0, 0, 7, 20, 7, 20);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, horzAdjust, HudMap.hudCompassX - 4, HudMap.hudCompassY - 2,0, 0, 7, 20, 7, 20);

        saveButton.setPosition((int) (UnitConvert.pixelToScaledCoords(window.getWidth()) / 2 - 10), (int) (UnitConvert.pixelToScaledCoords(window.getHeight()) / 2 - 10));
        context.drawTexture(RenderPipelines.GUI_TEXTURED, saveButton.isHovered() ? saveIdentifiers[1] : saveIdentifiers[0], saveButton.getX(), saveButton.getY(), 0, 0, 20, 20, 20, 20);

        exitButton.setPosition((int) (UnitConvert.pixelToScaledCoords(window.getWidth()) / 2 - 10), (int) (UnitConvert.pixelToScaledCoords(window.getHeight()) / 2 - 10 + 24));
        context.drawTexture(RenderPipelines.GUI_TEXTURED, exitButton.isHovered() ? exitIdentifiers[1] : exitIdentifiers[0], exitButton.getX(), exitButton.getY(), 0, 0, 20, 20, 20, 20);
    }
}
