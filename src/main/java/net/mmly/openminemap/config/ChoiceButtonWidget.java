package net.mmly.openminemap.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.AbstractInput;
import net.minecraft.text.Text;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ConfigFile;

import java.util.List;

public class ChoiceButtonWidget extends ButtonWidget implements ConfigChoice {

    List<String> options;
    int selection;
    net.minecraft.text.Text message;
    ConfigOptions configOption;
    ConfigAnchorWidget anchor;
    boolean optionIsLiteral;

    protected ChoiceButtonWidget(List<String> options, ConfigOptions configOption, boolean optionIsLiteral) {
        super(0, -100, 120, 20, net.minecraft.text.Text.empty(), (buttonWidget) -> {buttonWidget.onPress(null);}, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        this.options = options;
        this.message = net.minecraft.text.Text.translatable(configOption.message);
        this.setTooltip(Tooltip.of(net.minecraft.text.Text.translatable(configOption.tooltip)));
        this.configOption = configOption;
        selection = getSelectedOption();
        this.optionIsLiteral = optionIsLiteral;
        refreshMessage();
    }

    protected ChoiceButtonWidget(List<String> options, ConfigOptions configOption) {
        this(options, configOption, false);
    }

    private int getSelectedOption() {
        String selectedOption = configOption.getAsString();
        for (int i = 0; i < options.size(); i++) {
            if (selectedOption.equals(options.get(i).toLowerCase())) return i;
        }
        return 0;
    }

    private String getTranslatedOption(String option) {
         if (optionIsLiteral) return option;
         return net.minecraft.text.Text.translatable("omm.config.state."+(option.toLowerCase())).getString();
    }

    @Override
    public void onPress(AbstractInput input) {
        cycleOption();
    }

    private void refreshMessage() {
        this.setMessage(net.minecraft.text.Text.of(message.getString() + ": " + getTranslatedOption(options.get(selection))));
    }

    private void cycleOption() {
        selection++;
        selection %= options.size();
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
        ConfigFile.writeParameter(configOption, options.get(selection).toLowerCase());
    }

    @Override
    protected void drawIcon(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        if (!anchor.drawNow) return;
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();
        this.drawButton(context);
        this.drawLabel(context.getHoverListener(this, DrawContext.HoverType.NONE));
        //context.fill(getX(), getY(), getRight(), getBottom(), 0x3300FF00);
    }

}
