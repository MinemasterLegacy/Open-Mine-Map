package net.mmly.openminemap.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.mmly.openminemap.gui.FullscreenMapScreen.windowScaledHeight;
import static net.mmly.openminemap.gui.FullscreenMapScreen.windowScaledWidth;

public class BugReportLayer extends ClickableWidget {

    public BugReportLayer(int x, int y) {
        super(x, y, 70, 16, Text.empty());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0xFFFFFFFF);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    public void drawWidget(DrawContext context, TextRenderer textRenderer) {
        context.fill(windowScaledWidth - 70, windowScaledHeight - 32, windowScaledWidth, windowScaledHeight - 16, 0x88000000);
        context.drawText(textRenderer,
                isHovered() ?
                        Text.literal("Report Bugs").formatted(Formatting.UNDERLINE) :
                        Text.literal("Report Bugs"),
                windowScaledWidth - 65,
                windowScaledHeight + 7 - textRenderer.fontHeight - 10 - 16,
                0xFF0B9207,
                true);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        //FullscreenMapScreen.openBugReportScreen();
        FullscreenMapScreen.openLinkScreen("https://github.com/MinemasterLegacy/Open-Mine-Map/issues/new", new FullscreenMapScreen());
    }
}
