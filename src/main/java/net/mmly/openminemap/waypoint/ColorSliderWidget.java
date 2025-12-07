package net.mmly.openminemap.waypoint;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

public class ColorSliderWidget extends ClickableWidget {

    private static Identifier drawTexture = Identifier.of("openminemap", "colorspectrum.png");
    private ColorSliderType type;

    public static float hue = 0;
    public static float saturation = 1;
    public static float value = 1;

    public ColorSliderWidget(int x, int y, int width, int height, ColorSliderType type) {
        super(x, y, width, height, Text.of(""));
        this.type = type;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        switch (type) {
            case HUE -> {
                context.drawTexture(drawTexture, getX(), getY(), 0, 0, width, height, width, height);
                drawSelectionBox(context, hue);
            }
            case SATURATION -> {
                context.fillGradient(getX() + width, getY() + height, getX(), getY(), Color.HSBtoRGB(hue, 1, 1), 0xFFFFFFFF);
                drawSelectionBox(context, saturation);
            }
            case VALUE -> {
                context.fillGradient(getX() + width, getY() + height, getX(), getY(), Color.HSBtoRGB(hue, 1, 1), 0xFF000000);
                drawSelectionBox(context, value);
            }
        }
    }

    private void drawSelectionBox(DrawContext context, float channel) {
        context.drawBorder(getX() - 1, (int) (getY() - 1 + ((height-1) * channel)), width + 2, 3, 0xFF888888);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        super.onDrag(mouseX, mouseY, deltaX, deltaY);
        switch (type) {
            case HUE -> hue = Math.clamp((float) (mouseY - getY()) / (height-1), 0, 1);
            case SATURATION -> saturation = Math.clamp((float) (mouseY - getY()) / (height-1), 0, 1);
            case VALUE -> value = Math.clamp((float) (mouseY - getY()) / (height-1), 0, 1);
        }
    }

    private static int oppositeColorOf(int argb) {
        int blue = argb & 0x000000FF;
        int green = (argb >> 8) & 0x000000FF;
        int red = (argb >> 16) & 0x000000FF;
        int alpha = (argb >> 24) & 0x000000FF;

        return (alpha << 24) | (Math.abs(red-256) << 16) | (Math.abs(green-256) << 8) | Math.abs(blue-256);
    }

}
