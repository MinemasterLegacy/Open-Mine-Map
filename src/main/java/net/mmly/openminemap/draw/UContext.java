package net.mmly.openminemap.draw;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class UContext { //UniversalContext ; makes it easier to update draw methods per-version and allows for adding custom ones ; also eliminates the need to pass a context with draw methods

    static DrawContext drawContext;
    static TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

    public static void setContext(DrawContext context) {
        drawContext = context;
    }

    public static DrawContext getContext() {
        return drawContext;
    }

    public static void setTextRenderer(TextRenderer renderer) {
        textRenderer = renderer;
    }

    // x value defines different points depending on justification:
    //  - Left: defines leftmost edge
    //  - Right: defines rightmost edge
    //  - Center: defines center
    public static void drawJustifiedText(Text text, Justify justify, int x, int y, int color, boolean shadow) {
        switch (justify) {
            case LEFT: {
                drawContext.drawText(textRenderer, text, x, y, color, shadow);
                break;
            }
            case RIGHT: {
                drawContext.drawText(textRenderer, text, x - textRenderer.getWidth(text), y, color, shadow);
                break;
            }
            case CENTER: {
                drawContext.drawText(textRenderer, text, x - (textRenderer.getWidth(text) / 2), y, color, shadow);
                break;
            }
        }
    }
    public static void drawJustifiedText(Text text, Justify justify, int x, int y, int color) {
        drawJustifiedText(text, justify, x, y, color, false);
    }

    public static void drawBorder(int x, int y, int width, int height, int color) {
        drawContext.drawBorder(x, y, width, height, color);
    }
    public static void drawBorderZone(int x, int y, int x2, int y2, int color) {
        drawBorder(x, y, x2 - x, y2 - y, color);
    }

    public static void fillZone(int x, int y, int width, int height, int color) {
        drawContext.fill(x, y, x + width, y + height, color);
    }

    public static void fillAndDrawText(Text text, int x, int y, int marginWidth, int marginHeight, int fillColor, int textColor, boolean shadow) {
        fillZone(x, y, (marginWidth * 2) + textRenderer.getWidth(text), (marginHeight * 2) + textRenderer.fontHeight, fillColor);
        drawJustifiedText(text, Justify.LEFT, x + marginWidth, y + marginHeight, textColor, shadow);
    }

    public static void fillAndDrawText(Text text, int x, int y, int marginWidth, int marginHeight, int fillColor, int textColor) {
        fillAndDrawText(text, x, y, marginWidth, marginHeight, fillColor, textColor, false);
    }

    public static void drawTexture(Identifier identifier, int x, int y, int width, int height, int textureWidth, int textureHeight) {
        drawTexture(identifier, x, y, width, height, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    public static void drawTexture(Identifier identifier, int x, int y, int width, int height, float u, float v, int textureWidth, int textureHeight) {
        drawTexture(identifier, x, y, width, height, u, v, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    public static void drawTexture(Identifier identifier, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        drawContext.drawTexture(identifier, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }
}
