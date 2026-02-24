package net.mmly.openminemap.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ConfigFile;

public class ChoiceButtonWidget extends ButtonWidget implements ConfigChoice {

    String[] options;
    int selection;
    Text message;
    ConfigOptions configOption;
    ConfigAnchorWidget anchor;
    boolean optionIsLiteral = false;

    protected ChoiceButtonWidget(Text message, Text tooltip, String[] options, ConfigOptions configOption, boolean optionIsLiteral) {
        super(0, -100, 120, 20, message, ButtonWidget::onPress, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.options = options;
        this.message = message;
        this.setTooltip(Tooltip.of(tooltip));
        this.configOption = configOption;
        selection = getSelectedOption();
        this.optionIsLiteral = optionIsLiteral;
        refreshMessage();
    }

    protected ChoiceButtonWidget(Text message, Text tooltip, String[] options, ConfigOptions configOption) {
        this(message, tooltip, options, configOption, false);
    }

    private int getSelectedOption() {
        String selectedOption = ConfigFile.readParameter(configOption);
        for (int i = 0; i < options.length; i++) {
            if (selectedOption.equals(options[i].toLowerCase())) return i;
        }
        return 0;
    }

    private String getTranslatedOption(String option) {
         if (optionIsLiteral) return option;
         return Text.translatable("omm.config.state."+(option.toLowerCase())).getString();
    }

    @Override
    public void onPress() {
        System.out.println("Press");
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
