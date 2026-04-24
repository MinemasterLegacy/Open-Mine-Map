package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.mmly.openminemap.config.ConfigScreen;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ButtonState;
import net.mmly.openminemap.util.ColorUtil;

public class MicroButton extends ClickableWidget {

    private final MicroButtonFunction buttonFunction;
    private RasterLayerWidget parentWidget;
    private final LayerType layerType;
    private boolean flash = false;

    public MicroButton(int x, int y, MicroButtonFunction function, LayerType layerType) {
        super(x, y, 12, 12, Text.of(""));
        this.buttonFunction = function;
        this.layerType = layerType;
    }

    public void setParentWidget(RasterLayerWidget parentWidget) {
        this.parentWidget = parentWidget;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    public void draw(int mouseX, int mouseY) {
        UContext.drawTexture(
                buttonFunction.getTexture(isMouseOver(mouseX, mouseY) ? ButtonState.HOVER : ButtonState.DEFAULT),
                getX(), getY(), width, height, 12, 12);
        if (flash) drawFlash();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    public void setFlash(boolean flash) {
        this.flash = flash;
    }

    private void drawFlash() {
        int alpha = Math.abs(((int) (Util.getEpochTimeMs() >>> 3) % 256) + 128);
        int color = ColorUtil.setAlpha(alpha, 0xFFFFFFFF);
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
                if (layerType == LayerType.LOCAL_GEN) client.setScreen(new ConfigScreen());
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
            case INFO: {
                MinecraftClient.getInstance().setScreen(new CreateRasterScreen(parentWidget.url));
                //TODO
                break;
            }
        }
    }
}
