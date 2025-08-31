package net.mmly.openminemap.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class AttributionLayer extends ClickableWidget {

    public AttributionLayer(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public void onClick(double mouseX, double mouseY) {
        FullscreenMapScreen.openOSMAttrScreen();
    }
}
