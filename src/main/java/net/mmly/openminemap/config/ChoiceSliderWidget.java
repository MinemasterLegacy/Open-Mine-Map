package net.mmly.openminemap.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ConfigFile;

public class ChoiceSliderWidget extends SliderWidget implements ConfigChoice {

    String[] options;
    int selection;
    Text message;
    ConfigOptions configOption;
    ConfigAnchorWidget anchor;
    boolean optionIsLiteral;

    public ChoiceSliderWidget(String[] options, ConfigOptions configOption, boolean optionIsLiteral) {
        super(0, -100, 200, 20, Text.empty(), 0);
        this.options = options;
        this.message = Text.translatable(configOption.message);
        this.configOption = configOption;
        selection = getSelectedOption();
        this.value = (1F / (options.length - 1)) * selection;
        this.setTooltip(Tooltip.of(Text.translatable(configOption.tooltip)));
        this.optionIsLiteral = optionIsLiteral;
        updateMessage();
    }

    public ChoiceSliderWidget(String[] options, ConfigOptions configOption) {
        this(options, configOption, false);
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Text.of(message.getString() + ": " + getTranslatedOption(options[selection])));
        //System.out.println(message.getString() + ": " + options[selection]);
    }

    private int getSelectedOption() {
        String selectedOption = ConfigFile.readParameter(configOption);
        for (int i = 0; i <= options.length; i++) {
            if (selectedOption.equalsIgnoreCase(options[i])) return i;
        }
        return 0;
    }

    private String getTranslatedOption(String option) {
        if (optionIsLiteral) return option;
        else return Text.translatable("omm.config.state." + (option.toLowerCase())).getString();
    }

    @Override
    protected void applyValue() {
        selection = Math.max(0, (int) Math.ceil(this.value * options.length) - 1);
        //System.out.println(selection);
    }

    @Override
    public void setAnchor(ConfigAnchorWidget anchor) {
        this.anchor = anchor;
    }

    public void writeParameterToFile() {
        ConfigFile.writeParameter(configOption, options[selection].toLowerCase());
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!anchor.drawNow) return;
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();
        super.renderWidget(context, mouseX, mouseY, delta);
    }
}