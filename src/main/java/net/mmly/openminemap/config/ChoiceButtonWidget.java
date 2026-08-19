package net.mmly.openminemap.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.util.ConfigFile;

import java.util.List;

public class ChoiceButtonWidget extends Button implements ConfigChoice {

    List<String> options;
    int selection;
    net.minecraft.network.chat.Component message;
    ConfigOptions configOption;
    AnchorWidget anchor;
    boolean optionIsLiteral;

    protected ChoiceButtonWidget(List<String> options, ConfigOptions configOption, boolean optionIsLiteral) {
        super(0, -100, 120, 20, net.minecraft.network.chat.Component.empty(), (buttonWidget) -> {buttonWidget.onPress(null);}, Button.DEFAULT_NARRATION);
        this.options = options;
        this.message = net.minecraft.network.chat.Component.translatable(configOption.message);
        this.setTooltip(Tooltip.create(net.minecraft.network.chat.Component.translatable(configOption.tooltip)));
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
         return net.minecraft.network.chat.Component.translatable("omm.config.state."+(option.toLowerCase())).getString();
    }

    @Override
    public void onPress(InputWithModifiers input) {
        cycleOption();
    }

    private void refreshMessage() {
        this.setMessage(net.minecraft.network.chat.Component.nullToEmpty(message.getString() + ": " + getTranslatedOption(options.get(selection))));
    }

    private void cycleOption() {
        selection++;
        selection %= options.size();
        refreshMessage();
    }

    @Deprecated
    protected Button getButtonWidget() {
        return this;
    }

    @Override
    public void setAnchor(AnchorWidget anchor) {
        this.anchor = anchor;
    }

    public void writeParameterToFile() {
        ConfigFile.writeParameter(configOption, options.get(selection).toLowerCase());
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
        if (!anchor.drawNow) return;
        this.setX(anchor.getX());
        this.setY(anchor.getY());
        this.width = anchor.getWidth();
        this.extractDefaultSprite(context);
        this.extractDefaultLabel(context.textRendererForWidget(this, GuiGraphicsExtractor.HoveredTextEffects.NONE));
        //context.fill(getX(), getY(), getRight(), getBottom(), 0x3300FF00);
    }

}
