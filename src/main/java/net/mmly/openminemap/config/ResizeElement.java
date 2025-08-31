package net.mmly.openminemap.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class ResizeElement extends ClickableWidget {
    public ResizeElement(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
