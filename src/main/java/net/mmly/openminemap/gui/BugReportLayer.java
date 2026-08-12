package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.mmly.openminemap.draw.UContext;

import static net.mmly.openminemap.gui.MapScreen.windowScaledHeight;
import static net.mmly.openminemap.gui.MapScreen.windowScaledWidth;

public class BugReportLayer extends ClickableWidget {

    private final MutableText text;

    public BugReportLayer(int x, int y) {
        super(x, y, 0, 16, Text.empty());
        text = Text.translatable("omm.fullscreen.report-bugs");
        setWidth(MinecraftClient.getInstance().textRenderer.getWidth(text) + 8);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0xFFFFFFFF);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    public void drawWidget(DrawContext context, TextRenderer textRenderer) {
        UContext.fillWidget(this, MapScreen.backingColor);
        context.drawText(textRenderer,
                isHovered() ?
                        text.formatted(Formatting.UNDERLINE) :
                        text,
                getX() + 4,
                getY() + 4,
                0xFF0B9207,
                true);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        //FullscreenMapScreen.openBugReportScreen();
        MapScreen.openLinkScreen("https://github.com/MinemasterLegacy/Open-Mine-Map/issues/new", new MapScreen(), true);
    }
}
