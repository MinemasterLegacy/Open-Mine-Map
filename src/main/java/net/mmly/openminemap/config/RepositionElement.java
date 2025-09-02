package net.mmly.openminemap.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.hud.HudMap;

public class RepositionElement extends ClickableWidget {
    public RepositionElement() {
        super(HudMap.hudMapX, HudMap.hudMapY, HudMap.hudMapWidth, HudMap.hudMapHeight, Text.empty());
    }

    double subDeltaX = 0;
    double subDeltaY = 0;

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        setX(HudMap.hudMapX);
        setY(HudMap.hudMapY);
        setWidth(HudMap.hudMapWidth);
        setHeight(HudMap.hudMapHeight);
        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x00000000);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        System.out.println("drag");
        subDeltaX += deltaX;
        subDeltaY += deltaY;
        HudMap.hudMapX += (int) subDeltaX;
        HudMap.hudMapY += (int) subDeltaY;
        subDeltaX %= 1;
        subDeltaY %= 1;
        HudMap.updateX2Y2();
        MapConfigScreen.updateResizePos();
    }
}
