package net.mmly.openminemap.raster;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.util.UnitConvert;

public class OpacitySlider extends SliderWidget {

    private RasterLayerWidget parentWidget;
    private int recordedMouseX = 0;
    boolean mouseDown = false;

    public OpacitySlider(int x, int y, double value) {
        super(x, y, 42, 12, Text.empty(), value);
        this.value = value;
        updateMessage();
    }

    public double getValue() {
        return value;
    }

    @Override
    protected void updateMessage() {
        //TODO translate
        this.setMessage(Text.of(
                ((int) (value * 100)) + "%"
        ));
    }

    @Override
    protected void applyValue() {

    }

    public void setRecordedMouseX(int recordedMouseX) {
        this.recordedMouseX = recordedMouseX;
    }

    public void setParentWidget(RasterLayerWidget parentWidget) {
        this.parentWidget = parentWidget;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        mouseDown = true;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        mouseDown = false;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        if (mouseDown) super.onDrag(mouseX, mouseY, mouseX - recordedMouseX, 0);
    }
}
