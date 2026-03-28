package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.MaceItem;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ColorUtil;

public class ColorChoiceSliderWidget extends ChoiceSliderWidget {
    public ColorChoiceSliderWidget(ConfigOptions configOption) {
        super(genColorRange(), configOption, true);
    }

    private static String[] genColorRange() {
        String[] choices = new String[22];
        choices[0] = "#FFFFFF";
        choices[21] = "Rainbow";
        int index = 0;
        for (int i = 0; i < 360; i += 18) {
            index += 1;
            choices[index] = "#" + Integer.toHexString(ColorUtil.hsl(255, i, 0.95f, 0.75f)).substring(2, 8);
        }
        return choices;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        //Following copied from renderWidget method
        if (!anchor.drawNow) return;
        this.drawScrollableText(context, MinecraftClient.getInstance().textRenderer, 2, getTextColor(true) | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    public int getTextColor(boolean returnTrueRainbow) {
        if (selection == 0) return 0xFFFFFFFF;
        if (selection == 21 && returnTrueRainbow) return ColorUtil.getCurrentRainbowColor();
        if (selection == 21) return 0xFF000000;
        return ColorUtil.hsl(255, (selection - 1) * 18, 0.95f, 0.75f);
    }
}
