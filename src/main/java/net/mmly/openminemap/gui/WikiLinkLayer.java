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

public class WikiLinkLayer extends ClickableWidget {

    private static final int TEXT_COLOR = 0xFFaa00aa;
    private static final MutableText TEXT = Text.translatable("omm.config.gui.omm-wiki");
    private static final MutableText UNDERLINED_TEXT = Text.translatable("omm.config.gui.omm-wiki").formatted(Formatting.UNDERLINE);

    public WikiLinkLayer(int x, int y) {
        super(x, y, 0, 16, Text.empty());
        setWidth(MinecraftClient.getInstance().textRenderer.getWidth(TEXT) + 8);
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
                isHovered() ? UNDERLINED_TEXT : TEXT,
                getX() + 4,
                getY() + 4,
                TEXT_COLOR,
                true);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        //FullscreenMapScreen.openBugReportScreen();
        MapScreen.openLinkScreen("https://github.com/MinemasterLegacy/Open-Mine-Map/wiki", new MapScreen(), true);
    }
}
