package net.mmly.openminemap.waypoint;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.util.Waypoint;

public class WaypointEntryWidget extends ClickableWidget {

    Waypoint waypoint;
    TextRenderer renderer;

    public WaypointEntryWidget(int x, int y, Text message, Waypoint waypoint, TextRenderer textRenderer) {
        super(x, y, 0, 20, message);
        this.waypoint = waypoint;
        this.renderer = textRenderer;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

        context.enableScissor(0, 0, WaypointScreen.getMidPoint(), MinecraftClient.getInstance().getWindow().getScaledHeight());

        setWidth(WaypointScreen.getMidPoint() - 20);

        context.drawTexture(waypoint.identifier, getX() + 4, getY() + 4, 0, 0, 12, 12, 12, 12);
        context.drawText(renderer, waypoint.name, getX() + 23, getY() + (height / 2) - (renderer.fontHeight / 2), 0xFFFFFFFF, true);
        context.drawBorder(getX(), getY(), getWidth(), getHeight(), isFocused() ? 0xFFFFFFFF : 0xFF808080);

        context.disableScissor();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
