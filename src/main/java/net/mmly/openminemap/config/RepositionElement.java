package net.mmly.openminemap.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.hud.HudMap;

public class RepositionElement extends ClickableWidget {

    int type;

    public RepositionElement(int type) {
        super(0, 0, 0, 0, Text.empty());
        this.type = type; //0 is for map, 1 is for compass
        if (type == 0) {
            this.setDimensionsAndPosition(HudMap.hudMapWidth, HudMap.hudMapHeight, HudMap.hudMapX, HudMap.hudMapY);
        } else if (type == 1) {
            this.setDimensionsAndPosition(HudMap.hudCompassWidth, 16, HudMap.hudCompassX, HudMap.hudCompassY);
        }
    }

    double subDeltaX = 0;
    double subDeltaY = 0;

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (type == 0) {
            this.setDimensionsAndPosition(HudMap.hudMapWidth, HudMap.hudMapHeight, HudMap.hudMapX, HudMap.hudMapY);
        } else if (type == 1) {
            this.setDimensionsAndPosition(HudMap.hudCompassWidth, 16, HudMap.hudCompassX, HudMap.hudCompassY);
        }
        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x00000000);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        subDeltaX += deltaX;
        subDeltaY += deltaY;
        if (type == 0) {
            HudMap.hudMapX += (int) subDeltaX;
            HudMap.hudMapY += (int) subDeltaY;
        } else if (type == 1) {
            HudMap.hudCompassX += (int) subDeltaX;
            HudMap.hudCompassY += (int) subDeltaY;
        }
        subDeltaX %= 1;
        subDeltaY %= 1;
        MapConfigScreen.updateResizePos();
    }
}
