package net.mmly.openminemap.config;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.enums.RepositionType;
import net.mmly.openminemap.hud.HudMap;

public class RepositionElement extends AbstractWidget {

    RepositionType type;

    public RepositionElement(RepositionType type) {
        super(0, 0, 0, 0, Component.empty());
        this.type = type; //0 is for map, 1 is for compass
        updateDimensionsAndPosition();
    }

    double subDeltaX = 0;
    double subDeltaY = 0;

    private void updateDimensionsAndPosition() {
        if (type == RepositionType.MAP) {
            this.setRectangle(
                    HudMap.map.getRenderAreaWidth(),
                    HudMap.map.getRenderAreaHeight(),
                    HudMap.map.getRenderAreaX(),
                    HudMap.map.getRenderAreaY()
            );
        } else if (type == RepositionType.COMPASS) {
            this.setRectangle(HudMap.hudCompassWidth, 16, HudMap.hudCompassX, HudMap.hudCompassY);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        updateDimensionsAndPosition();
        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x00000000);
        if (isHovered()) context.requestCursor(CursorTypes.RESIZE_ALL);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

    @Override
    protected void onDrag(MouseButtonEvent click, double offsetX, double offsetY) {
        subDeltaX += offsetX;
        subDeltaY += offsetY;
        if (type == RepositionType.MAP) {
            HudMap.map.setRenderPosition(
                    HudMap.map.getRenderAreaX() + (int) subDeltaX,
                    HudMap.map.getRenderAreaY() + (int) subDeltaY
            );
        } else if (type == RepositionType.COMPASS) {
            HudMap.hudCompassX += (int) subDeltaX;
            HudMap.hudCompassY += (int) subDeltaY;
        }
        subDeltaX %= 1;
        subDeltaY %= 1;
        MapConfigScreen.updateResizePos();
    }
}
