package net.mmly.openminemap.config;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ConfigFile;

public class ChoiceSliderWidget extends SliderWidget {

    String[] options;
    int selection;
    Text message;
    ConfigOptions configOption;

    public ChoiceSliderWidget(int x, int y, Text message, Text tooltip, String[] options, ConfigOptions configOption) {
        super(x, y, 120, 20, message, 0);
        this.options = options;
        this.message = message;
        this.configOption = configOption;
        selection = getSelectedOption();
        this.value = (1F / (options.length - 1)) * selection;
        this.setTooltip(Tooltip.of(tooltip));
        updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Text.of(message.getString() + ": " + options[selection]));
        //System.out.println(message.getString() + ": " + options[selection]);
    }



    private int getSelectedOption() {
        String selectedOption = ConfigFile.readParameter(configOption);
        for (int i = 0; i <= options.length; i++) {
            if (selectedOption.equalsIgnoreCase(options[i])) return i;
        }
        return 0;
    }

    @Override
    protected void applyValue() {
        selection = Math.max(0, (int) Math.ceil(this.value * options.length) - 1);
        //System.out.println(selection);
    }

    protected void writeParameterToFile() {
        ConfigFile.writeParameter(configOption, options[selection].toLowerCase());
    }
}