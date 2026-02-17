package net.mmly.openminemap.config;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.draw.Justify;
import net.mmly.openminemap.draw.UContext;

public class CategoryLabelWidget extends TextWidget implements ConfigChoice {

    ConfigAnchorWidget anchor;

    public CategoryLabelWidget( Text message, TextRenderer textRenderer) {
        super(0, -100, 200, 20, message, textRenderer);
    }

    @Override
    public void setAnchor(ConfigAnchorWidget anchor) {
        this.anchor = anchor;
    }

    @Override
    public void writeParameterToFile() {
        //nothing
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!anchor.drawNow) return;
        setY(anchor.getY());
        setX(anchor.getX());
        setWidth(anchor.getWidth());
        //context.fill(getX(), getY(), getX() + width, getY() + height, 0x7000FF00);
        //super.renderWidget(context, mouseX, mouseY, delta);
        UContext.drawJustifiedText(this.getMessage(), Justify.CENTER, getX() + (width / 2), getY() + 8, 0xFFFFFFFF, true);
    }
}
