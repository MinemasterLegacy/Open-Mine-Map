package net.mmly.openminemap.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.util.ConfigFile;

import java.util.List;

public class ChoiceSliderWidget extends AbstractSliderButton implements ConfigChoice {

    List<String> options;
    int selection;
    Component message;
    ConfigOptions configOption;
    AnchorWidget anchor;
    boolean optionIsLiteral;

    public ChoiceSliderWidget(List<String> options, ConfigOptions configOption, boolean optionIsLiteral) {
        super(0, -100, 200, 20, Component.empty(), 0);
        this.options = options;
        this.message = Component.translatable(configOption.message);
        this.configOption = configOption;
        selection = getSelectedOption();
        this.value = (1F / (options.size() - 1)) * selection;
        this.setTooltip(Tooltip.create(Component.translatable(configOption.tooltip)));
        this.optionIsLiteral = optionIsLiteral;
        updateMessage();
    }

    public ChoiceSliderWidget(List<String> options, ConfigOptions configOption) {
        this(options, configOption, false);
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.nullToEmpty(message.getString() + ": " + getTranslatedOption(options.get(selection))));
        //System.out.println(message.getString() + ": " + options[selection]);
    }

    protected int getSelectedOption() {
        String selectedOption = configOption.getAsString();
        for (int i = 0; i < options.size(); i++) {
            if (selectedOption.equalsIgnoreCase(options.get(i))) return i;
        }
        return 0;
    }

    private String getTranslatedOption(String option) {
        if (optionIsLiteral) return option;
        else return Component.translatable("omm.config.state." + (option.toLowerCase())).getString();
    }

    @Override
    protected void applyValue() {
        selection = Math.max(0, (int) Math.ceil(this.value * options.size()) - 1);
        //System.out.println(selection);
    }

    @Override
    public void setAnchor(AnchorWidget anchor) {
        this.anchor = anchor;
    }

    public void writeParameterToFile() {
        ConfigFile.writeParameter(configOption, options.get(selection).toLowerCase());
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (!anchor.drawNow) return;
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();
        super.renderWidget(context, mouseX, mouseY, delta);
    }
}