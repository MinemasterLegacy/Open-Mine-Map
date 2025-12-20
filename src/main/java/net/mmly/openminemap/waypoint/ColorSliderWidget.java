package net.mmly.openminemap.waypoint;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;

public class ColorSliderWidget extends ClickableWidget {

    private static Identifier drawTexture = Identifier.of("openminemap", "colorspectrum.png");
    private ColorSliderType type;

    public static final float defaultHue = 0.614F;
    public static final float defaultSaturation = 1;
    public static final float defaultValue = 0.5098F;

    public static float hue = defaultHue;
    public static float saturation = defaultSaturation;
    public static float value = defaultValue;

    public ColorSliderWidget(int x, int y, int width, int height, ColorSliderType type) {
        super(x, y, width, height, Text.of(""));
        this.type = type;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        switch (type) {
            case HUE -> {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, drawTexture, getX(), getY(), 0, 0, width, height, width, height);
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

    private static void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        //temporary method for 21.9. Can be replaced with context.drawBorder(...) in 1.21.8-, and context.submitOutline(...) in 1.21.10+
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y + 1, x + 1, y + height - 1, color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    private void drawSelectionBox(DrawContext context, float channel) {
        drawBorder(context, getX() - 1, (int) (getY() - 1 + ((height-1) * channel)), width + 2, 3, 0xFF888888);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    protected void onDrag(Click click, double offsetX, double offsetY) {
        super.onDrag(click, offsetX, offsetY);
        switch (type) {
            case HUE -> hue = Math.clamp((float) (click.y() - getY()) / (height-1), 0, 1);
            case SATURATION -> saturation = Math.clamp((float) (click.y() - getY()) / (height-1), 0, 1);
            case VALUE -> value = Math.clamp((float) (click.y() - getY()) / (height-1), 0, 1);
        }
    }

    private static int oppositeColorOf(int argb) {
        int blue = argb & 0x000000FF;
        int green = (argb >> 8) & 0x000000FF;
        int red = (argb >> 16) & 0x000000FF;
        int alpha = (argb >> 24) & 0x000000FF;

        return (alpha << 24) | (Math.abs(red-256) << 16) | (Math.abs(green-256) << 8) | Math.abs(blue-256);
    }

    public static void setColor(float hue, float saturation, float value) {
        ColorSliderWidget.hue = hue;
        ColorSliderWidget.saturation = saturation;
        ColorSliderWidget.value = value;
    }

    public static void setColor(int hsb) {
        ColorSliderWidget.hue = (float) ((hsb & 0xFF0000) >> 16) / 255;
        ColorSliderWidget.saturation = (float) ((hsb & 0x00FF00) >> 8)  / 255;
        ColorSliderWidget.value = (float )(hsb & 0x0000FF) / 255;
        System.out.println(ColorSliderWidget.hue +"\t"+ ColorSliderWidget.saturation +"\t"+ ColorSliderWidget.value +"\t");
    }

}
