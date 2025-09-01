package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.util.UnitConvert;

public class MapConfigScreen extends Screen {

    private final static Identifier horzAdjust = Identifier.of("openminemap", "resizehorizontal.png");
    private final static Identifier vertAdjust = Identifier.of("openminemap", "resizevertical.png");
    private static ResizeElement upResize;
    private static ResizeElement rightResize;
    private static ResizeElement downResize;
    private static ResizeElement leftResize;
    private static RepositionElement repositionElement;
    private static ButtonLayer exitButton;
    private static Identifier[] exitIdentifiers;
    private static Window window;

    protected MapConfigScreen() {
        super(Text.empty());
    }

    protected static void updateResizePos() {
        HudMap.updateX2Y2();
        rightResize.setPosition(HudMap.hudMapX2 - 3, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10);
        leftResize.setPosition(HudMap.hudMapX - 4, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10);
        downResize.setPosition((HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY2 - 3);
        upResize.setPosition((HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY - 4);
    }

    @Override
    protected void renderDarkening(DrawContext context) {}
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    protected void init() {
        super.init();

        window = MinecraftClient.getInstance().getWindow();

        exitIdentifiers = new Identifier[] {
                Identifier.of("openminemap", "buttons/vanilla/default/check.png"),
                Identifier.of("openminemap", "buttons/vanilla/hover/check.png")
        };
        exitButton = new ButtonLayer(0, 0,20, 20, 5);
        this.addDrawableChild(exitButton);

        rightResize = new ResizeElement(0, 0, 7, 20, 1);
        leftResize = new ResizeElement(0, 0, 7, 20, 3);
        downResize = new ResizeElement(0, 0, 20, 7, 2);
        upResize = new ResizeElement(0, 0, 20, 7, 0);
        updateResizePos();

        this.addDrawableChild(rightResize);
        this.addDrawableChild(leftResize);
        this.addDrawableChild(downResize);
        this.addDrawableChild(upResize);

        repositionElement = new RepositionElement();
        this.addDrawableChild(repositionElement);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        //Blue rectangle overlay
        //context.fill(HudMap.hudMapX, HudMap.hudMapY, HudMap.hudMapX2, HudMap.hudMapY2, 0xFFCEE1E4);
        exitButton.setPosition((int) (UnitConvert.pixelToScaledCoords(window.getWidth()) / 2 - 10), (int) (UnitConvert.pixelToScaledCoords(window.getHeight()) / 2 - 10));
        context.drawTexture(exitButton.isHovered() ? exitIdentifiers[1] : exitIdentifiers[0], exitButton.getX(), exitButton.getY(), 0, 0, 20, 20, 20, 20);

        context.drawTexture(horzAdjust, HudMap.hudMapX2 - 3, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10, 0, 0, 7, 20, 7, 20); //right
        context.drawTexture(horzAdjust, HudMap.hudMapX - 4, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10, 0, 0, 7, 20, 7, 20); //left
        context.drawTexture(vertAdjust, (HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY2 - 3, 0, 0, 20, 7, 20, 7); //bottom
        context.drawTexture(vertAdjust, (HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY - 4, 0, 0, 20, 7, 20, 7); //top
    }
}
