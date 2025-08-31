package net.mmly.openminemap.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class WaypointLayer extends ClickableWidget {
    int id;
    public WaypointLayer(int x, int y, int i) {
        super(x, y, 9, 9, Text.empty());
        id = i;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + 9, getY() + 9, 0xFF000000);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
}
