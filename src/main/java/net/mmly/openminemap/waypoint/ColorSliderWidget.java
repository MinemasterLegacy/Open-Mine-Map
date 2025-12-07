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
    public static float value = 0.5F;

    public ColorSliderWidget(int x, int y, int width, int height, ColorSliderType type) {
        super(x, y, width, height, Text.of(""));
        this.type = type;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) { //TODO
        switch (type) {
            case HUE -> context.drawTexture(drawTexture, getX(), getY(), 0, 0, width, height, width, height);
            case SATURATION -> context.fillGradient(getX() + width, getY() + height, getX(), getY(), 0xFFFFFFFF, Color.HSBtoRGB(hue, saturation, value));
            case VALUE -> context.fillGradient(getX() + width, getY() + height, getX(), getY(), 0xFF000000, Color.HSBtoRGB(hue, saturation, value));
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        super.onDrag(mouseX, mouseY, deltaX, deltaY);
        switch (type) {
            case HUE -> hue = (float) (mouseX - getX()) / width;
            case SATURATION -> saturation = (float) (mouseX - getX()) / width;
            case VALUE -> value = (float) (mouseX - getX()) / width;
        }
    }
}
