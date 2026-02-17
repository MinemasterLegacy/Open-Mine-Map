package net.mmly.openminemap.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.text.Text;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ConfigFile;

public class ChoiceButtonWidget extends ButtonWidget implements ConfigChoice {

    String[] options;
    int selection;
    Text message;
    ConfigOptions configOption;
    ConfigAnchorWidget anchor;

    protected ChoiceButtonWidget(Text message, Text tooltip, String[] options, ConfigOptions configOption) {
        super(0, -100, 120, 20, message, (buttonWidget) -> {buttonWidget.onPress(null);}, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.options = options;
        this.message = message;
        this.setTooltip(Tooltip.of(tooltip));
        this.configOption = configOption;
        selection = getSelectedOption();
        refreshMessage();
    }

    private int getSelectedOption() {
        String selectedOption = ConfigFile.readParameter(configOption);
        for (int i = 0; i < options.length; i++) {
            if (selectedOption.equals(options[i].toLowerCase())) return i;
        }
        return 0;
    }

    private static String getTranslatedOption(String option) {
        if (option.contains("/")) option = option.substring(1);
        return Text.translatable("omm.config.state."+(option.toLowerCase())).getString();
    }

    @Override
    public void onPress(AbstractInput input) {
        cycleOption();
    }

    private void refreshMessage() {
        this.setMessage(Text.of(message.getString() + ": " + getTranslatedOption(options[selection])));
    }

    private void cycleOption() {
        selection++;
        selection %= options.length;
        refreshMessage();
    }

    @Deprecated
    protected ButtonWidget getButtonWidget() {
        return this;
    }

    @Override
    public void setAnchor(ConfigAnchorWidget anchor) {
        this.anchor = anchor;
    }

    public void writeParameterToFile() {
        ConfigFile.writeParameter(configOption, options[selection].toLowerCase());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!anchor.drawNow) return;
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();
        super.renderWidget(context, mouseX, mouseY, delta);
    }

}
