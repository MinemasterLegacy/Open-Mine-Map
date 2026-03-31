package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ColorUtil;

public class ColorChoiceSliderWidget extends ChoiceSliderWidget {
    public ColorChoiceSliderWidget(ConfigOptions configOption) {
        super(ConfigOptions.Values.COLORS, configOption, true);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        //Following copied from renderWidget method
        if (!anchor.drawNow) return;
        UContext.drawBorder(getX(), getY(), width, height, getTextColor(true));
        //this.drawTextWithMargin(context.getHoverListener(this, DrawContext.HoverType.NONE), this.getMessage(), 2);
        //this.drawScrollableText(context, MinecraftClient.getInstance().textRenderer, 2, getTextColor(true) | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }

    public int getTextColor(boolean returnTrueRainbow) {
        if (selection == 0) return 0xFFFFFFFF;
        if (selection == 21 && returnTrueRainbow) return ColorUtil.getCurrentRainbowColor();
        if (selection == 21) return 0xFF000000;
        return ColorUtil.hsl(255, (selection - 1) * 18, 0.95f, 0.75f);
    }
}
