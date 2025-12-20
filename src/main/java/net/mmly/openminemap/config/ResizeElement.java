package net.mmly.openminemap.config;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ResizeDirection;
import net.mmly.openminemap.enums.ResizePlane;
import net.mmly.openminemap.hud.HudMap;

public class ResizeElement extends ClickableWidget {

    Identifier texture;
    ResizeDirection direction;
    ResizePlane plane;
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
    double moveRemainder;

    //for parameter direction - up:0 right:1 down:2 left:3
    public ResizeElement(int x, int y, ResizeDirection direction) {
        super(x, y, 0, 0, Text.empty());
        this.direction = direction;

        if (direction == ResizeDirection.UP_MAP || direction == ResizeDirection.DOWN_MAP) {
            plane = ResizePlane.VERTICAL;
            texture = Identifier.of("openminemap", "resizevertical.png");
            setWidth(20);
            setHeight(7);
        } else {
            plane = ResizePlane.HORIZONTAL;
            texture = Identifier.of("openminemap", "resizehorizontal.png");
            setWidth(7);
            setHeight(20);
        }

    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        //context.fill(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), 0xFF0000FF);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (plane == ResizePlane.HORIZONTAL) {
            switch (direction) {
                case ResizeDirection.RIGHT_MAP: {
                    int change = (int) (deltaX + moveRemainder);
                    HudMap.map.setWidthFromRight(HudMap.map.getRenderAreaWidth() + change, HudMap.MIN_SIZE);
                    MapConfigScreen.updateResizePos();
                    break;
                } case ResizeDirection.LEFT_MAP: {
                    int change = (int) (deltaX + moveRemainder);
                    HudMap.map.setWidthFromLeft(HudMap.map.getRenderAreaWidth() - change, HudMap.MIN_SIZE);
                    MapConfigScreen.updateResizePos();
                    break;
                } case ResizeDirection.LEFT_COMPASS: {
                    int change = (int) (deltaX + moveRemainder);
                    if (HudMap.hudCompassWidth - change < HudMap.MIN_SIZE) {
                        HudMap.hudCompassX = HudMap.hudCompassX + HudMap.hudCompassWidth - HudMap.MIN_SIZE;
                        HudMap.hudCompassWidth = HudMap.MIN_SIZE;
                    } else {
                        HudMap.hudCompassX += change;
                        HudMap.hudCompassWidth -= change;
                    }
                    MapConfigScreen.updateResizePos();
                    break;
                } case ResizeDirection.RIGHT_COMPASS: {
                    int change = (int) (deltaX + moveRemainder);
                    if (HudMap.hudCompassWidth + change < HudMap.MIN_SIZE) HudMap.hudCompassWidth = HudMap.MIN_SIZE;
                    else HudMap.hudCompassWidth += change;
                    MapConfigScreen.updateResizePos();
                    break;
                }
            }
            moveRemainder = (deltaX + moveRemainder) % 1;
        } else /*(plane == ResizePlane.VERTICAL)*/ {
            switch (direction) {
                case ResizeDirection.UP_MAP: {
                    int change = (int) (deltaY + moveRemainder);
                    HudMap.map.setHeightFromTop(HudMap.map.getRenderAreaHeight() - change, HudMap.MIN_SIZE);
                    MapConfigScreen.updateResizePos();
                    break;
                } case ResizeDirection.DOWN_MAP: {
                    int change = (int) (deltaY + moveRemainder);
                    HudMap.map.setHeightFromBottom(HudMap.map.getRenderAreaHeight() + change, HudMap.MIN_SIZE);
                    MapConfigScreen.updateResizePos();
                    break;
                }
            }
            moveRemainder = (deltaY + moveRemainder) % 1;
        }
    }

    public void drawWidget(DrawContext context) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, texture, getX(), getY(), 0, 0, getWidth(), getHeight(), getWidth(), getHeight());
    }

    /*
    //unused, element position resetting is handled by MapConfigScreen.updateResizePos()
    public void resetPosition() {
        switch (direction) {
            case ResizeDirection.UP_MAP: setPosition((HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY - 4);
            case ResizeDirection.DOWN_MAP: setPosition((HudMap.hudMapX + HudMap.hudMapX2) / 2 - 10, HudMap.hudMapY2 - 3);
            case ResizeDirection.RIGHT_MAP: setPosition(HudMap.hudMapX2 - 3, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10);
            case ResizeDirection.LEFT_MAP: setPosition(HudMap.hudMapX - 4, (HudMap.hudMapY2 + HudMap.hudMapY) / 2 - 10);
            case ResizeDirection.RIGHT_COMPASS: setPosition(HudMap.hudCompassX + HudMap.hudCompassWidth - 3, HudMap.hudCompassY - 2);
            case ResizeDirection.LEFT_COMPASS: setPosition(HudMap.hudCompassX - 4, HudMap.hudCompassY - 2);
        }
    }

     */

    //code fo versions 1.1.0 and earlier (onDrag was not used)
    /*
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

     */
}
