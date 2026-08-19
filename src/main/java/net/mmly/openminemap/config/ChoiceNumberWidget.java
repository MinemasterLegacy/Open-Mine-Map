package net.mmly.openminemap.config;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.util.ConfigFile;

public class ChoiceNumberWidget extends EditBox implements ConfigChoice{

    AnchorWidget anchor;
    Component placeholder;
    Font textRenderer;

    public ChoiceNumberWidget(Font textRenderer) {
        super(textRenderer, 0, -100, 20, 20, Component.empty());
        this.setMaxLength(50);
        this.setValue(ConfigOptions.SNAP_ANGLE.getAsString());
        setTooltip(Tooltip.create(Component.translatable(ConfigOptions.SNAP_ANGLE.tooltip)));
        this.setEditable(true);
        this.placeholder = Component.translatable(ConfigOptions.SNAP_ANGLE.message);
        this.textRenderer = textRenderer;
        this.moveCursorToStart(false);
    }

    @Override
    public void setAnchor(AnchorWidget anchor) {
        this.anchor = anchor;
    }

    @Override
    public void writeParameterToFile() {
        String snapAngle;
        try {
            snapAngle = Double.toString(Double.parseDouble(getValue())); //will ensure that the snap angle is a number
        } catch (NumberFormatException e) {
            snapAngle = "";
        }
        ConfigFile.writeParameter(ConfigOptions.SNAP_ANGLE, snapAngle);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (!anchor.drawNow) return;
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();
        super.extractWidgetRenderState(context, mouseX, mouseY, delta);
        if (getValue().isEmpty() && !isFocused()) {
            context.text(textRenderer, placeholder, getX() + 4, getY() + 6, 0xFF404040);
        }
    }
}
