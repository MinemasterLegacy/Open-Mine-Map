package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ButtonState;
import net.mmly.openminemap.util.ColorUtil;

import javax.swing.*;

public class MicroButton extends ClickableWidget {

    private final MicroButtonFunction buttonFunction;
    private final LayerType layerType;

    public MicroButton(int x, int y, MicroButtonFunction function, LayerType layerType) {
        super(x, y, 12, 12, Text.of(""));
        this.buttonFunction = function;
        this.layerType = layerType;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    public void draw(int mouseX, int mouseY) {
        UContext.drawTexture(
                buttonFunction.getTexture(isMouseOver(mouseX, mouseY) ? ButtonState.HOVER : ButtonState.DEFAULT),
                getX(), getY(), width, height, 12, 12);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    private void drawGlow() {
        //TODO test
        int color = ColorUtil.setAlpha(255, 0xFFFFFFFF);
        UContext.fillZone(getX() + 1, getY(), getWidth() - 2, getHeight(), color);
        UContext.fillZone(getX(), getY() + 1, 1, getHeight() - 2, color);
        UContext.fillZone(getRight() - 1, getY() + 1, 1, getHeight() - 2, color);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        switch (buttonFunction) {
            case EDIT: {
                if (layerType == LayerType.BASE) client.setScreen(new BaseRasterScreen());
                break;
            }
            case UP: {
                //TODO
                break;
            }
            case DOWN: {
                //TODO
                break;
            }
            case VISIBILITY: {
                //TODO
                break;
            }
            case REMOVE: {
                //TODO
                break;
            }
        }
    }
}
