package net.mmly.openminemap.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.hud.HudMap;

public class ResizeElement extends ClickableWidget {
    int direction;
    /*
    int[][] dims = {
        {20, 7},
        {7, 20},
        {20, 7},
        {7, 20},
        {7, 20},
        {7, 20}
    };
     */
    double startMouseX;
    double startMouseY;

    //for parameter direction - up:0 right:1 down:2 left:3
    public ResizeElement(int x, int y, int width, int height, int direction) {
        super(x, y, width, height, Text.empty());
        this.direction = direction;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        //context.fill(this.getX(), this.getY(), this.getX() + dims[direction][0], this.getY() + dims[direction][1], 0xFF0000FF);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public void onClick(double mouseX, double mouseY) {
        startMouseX = mouseX;
        startMouseY = mouseY;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        switch (direction) {
            case 0: {
                if (HudMap.hudMapHeight + (int) (startMouseY - mouseY) < 20) {
                    HudMap.hudMapY = (HudMap.hudMapY + HudMap.hudMapHeight - 20);
                    HudMap.hudMapHeight = 20;
                } else {
                    HudMap.hudMapY -= (int) (startMouseY - mouseY);
                    HudMap.hudMapHeight += (int) (startMouseY - mouseY);
                }
                break;
            } case 1: {
                if (HudMap.hudMapWidth + (int) (mouseX - startMouseX) < 20) {
                    HudMap.hudMapWidth = 20;
                } else {
                    HudMap.hudMapWidth += (int) (mouseX - startMouseX);
                }
                break;
            } case 2: {
                if (HudMap.hudMapHeight + (int) (mouseY - startMouseY) < 20) {
                    HudMap.hudMapHeight = 20;
                } else {
                    HudMap.hudMapHeight += (int) (mouseY - startMouseY);
                }
                break;
            } case 3: {
                if (HudMap.hudMapWidth + (int) (startMouseX - mouseX) < 20) {
                    HudMap.hudMapX = (HudMap.hudMapX + HudMap.hudMapWidth - 20);
                    HudMap.hudMapWidth = 20;
                } else {
                    HudMap.hudMapX -= (int) (startMouseX - mouseX);
                    HudMap.hudMapWidth += (int) (startMouseX - mouseX);
                }
                break;
            } case 4: {
                if (HudMap.hudCompassWidth + (int) (startMouseX - mouseX) < 20) {
                    HudMap.hudCompassX = (HudMap.hudCompassX + HudMap.hudCompassWidth - 20);
                    HudMap.hudCompassWidth = 20;
                } else {
                    HudMap.hudCompassX -= (int) (startMouseX - mouseX);
                    HudMap.hudCompassWidth += (int) (startMouseX - mouseX);
                }
                break;
            } case 5: {
                if (HudMap.hudCompassWidth + (int) (mouseX - startMouseX) < 20) {
                    HudMap.hudCompassWidth = 20;
                } else {
                    HudMap.hudCompassWidth += (int) (mouseX - startMouseX);
                }
                break;
            }
        }
        MapConfigScreen.updateResizePos();
    }
}
