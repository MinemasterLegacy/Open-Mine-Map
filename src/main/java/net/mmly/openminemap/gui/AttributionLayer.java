package net.mmly.openminemap.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import static net.mmly.openminemap.gui.FullscreenMapScreen.windowScaledHeight;
import static net.mmly.openminemap.gui.FullscreenMapScreen.windowScaledWidth;

public class AttributionLayer extends ClickableWidget {

    public AttributionLayer(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000);
    }

    public void drawWidget(DrawContext context, TextRenderer textRenderer) {
        context.fill(windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight, 0x88000000);
        context.drawText(textRenderer, "Map data from", windowScaledWidth - 152, windowScaledHeight + 7 - textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
        context.drawText(textRenderer, Text.of("OpenStreetMap"), windowScaledWidth - 77, windowScaledHeight + 7 - textRenderer.fontHeight - 10, 0xFF548AF7, true); //0xFF1b75d0
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public void onClick(Click click, boolean doubled) {
        FullscreenMapScreen.openOSMAttrScreen();
    }
}
