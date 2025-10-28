package net.mmly.openminemap.config;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ConfigFile;

public class ChoiceButtonWidget extends ButtonWidget {

    ButtonWidget buttonWidget;
    String[] options;
    int selection;
    Text message;
    ConfigOptions configOption;

    protected ChoiceButtonWidget(int x, int y, Text message, Text tooltip, String[] options, ConfigOptions configOption) {
        super(x, y, 120, 20, message, null, null);
        this.options = options;
        this.message = message;
        this.configOption = configOption;
        selection = getSelectedOption();

        buttonWidget = ButtonWidget.builder(Text.of(message.getString() + ": " + this.options[selection]),
            this::cycleOption
        ).dimensions(x, y, 120, 20).build();
        buttonWidget.setTooltip(Tooltip.of(tooltip));
    }

    private int getSelectedOption() {
        String selectedOption = ConfigFile.readParameter(configOption);
        for (int i = 0; i < options.length; i++) {
            if (selectedOption.equals(options[i].toLowerCase())) return i;
        }
        return 0;
    }

    private void cycleOption(ButtonWidget buttonWidget) {
        selection++;
        if (selection > options.length - 1) selection = 0;
        buttonWidget.setMessage(Text.of(message.getString() + ": " + options[selection]));
    }

    protected ButtonWidget getButtonWidget() {
        return buttonWidget;
    }

    protected void writeParameterToFile() {
        ConfigFile.writeParameter(configOption, options[selection].toLowerCase());
    }

}
