package net.mmly.openminemap.config;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ConfigFile;

public class ChoiceNumberWidget extends TextFieldWidget implements ConfigChoice{

    ConfigAnchorWidget anchor;
    Text placeholder;
    TextRenderer textRenderer;

    public ChoiceNumberWidget(TextRenderer textRenderer, Tooltip tooltip, Text placeholder) {
        super(textRenderer, 0, -100, 20, 20, Text.of(""));
        this.setMaxLength(50);
        this.setText(ConfigFile.readParameter(ConfigOptions.SNAP_ANGLE));
        setTooltip(tooltip);
        this.setEditable(true);
        this.placeholder = placeholder;
        this.textRenderer = textRenderer;
        this.setCursorToStart(false);
    }

    @Override
    public void setAnchor(ConfigAnchorWidget anchor) {
        this.anchor = anchor;
    }

    @Override
    public void writeParameterToFile() {
        String snapAngle;
        try {
            snapAngle = Double.toString(Double.parseDouble(getText())); //will ensure that the snap angle is a number
        } catch (NumberFormatException e) {
            snapAngle = "";
        }
        ConfigFile.writeParameter(ConfigOptions.SNAP_ANGLE, snapAngle);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!anchor.drawNow) return;
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();
        super.renderWidget(context, mouseX, mouseY, delta);
        if (getText().isEmpty() && !isFocused()) {
            context.drawTextWithShadow(textRenderer, placeholder, getX() + 4, getY() + 6, 0xFF404040);
        }
    }
}
