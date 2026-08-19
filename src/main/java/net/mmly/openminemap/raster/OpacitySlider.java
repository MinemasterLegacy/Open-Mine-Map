package net.mmly.openminemap.raster;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.util.RasterProvider;

public class OpacitySlider extends AbstractSliderButton {

    private RasterLayerWidget parentWidget;
    private int recordedMouseX = 0;
    boolean mouseDown = false;

    public boolean dragging() {
        return mouseDown;
    }

    public OpacitySlider(int x, int y, double value) {
        super(x, y, 42, 12, Component.empty(), value);
        this.value = value;
        updateMessage();
    }

    public double getValue() {
        return value;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.nullToEmpty(
                ((int) (value * 100)) + "%"
        ));
    }

    @Override
    protected void applyValue() {
        RasterProvider.setOpacityOf(parentWidget.raster, (float) value);
    }

    public void setRecordedMouseX(int recordedMouseX) {
        this.recordedMouseX = recordedMouseX;
    }

    public void setParentWidget(RasterLayerWidget parentWidget) {
        this.parentWidget = parentWidget;
        this.value = RasterProvider.getOpacityOf(parentWidget.raster);
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        mouseDown = true;
    }

    @Override
    public void onRelease(MouseButtonEvent click) {
        mouseDown = false;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        if (mouseDown) super.onDrag(null, mouseX - recordedMouseX, 0);
        //if (mouseDown) super.onDrag(mouseX, mouseY, mouseX - recordedMouseX, 0);
    }
}
