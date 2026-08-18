package net.mmly.openminemap.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.gui.MapScreen;

import static net.mmly.openminemap.config.ConfigScreen.windowScaledHeight;
import static net.mmly.openminemap.config.ConfigScreen.windowScaledWidth;

public class CreditLayer extends ClickableWidget {

    private static final int TEXT_COLOR = 0xff00AAAA;
    private static final MutableText TEXT = Text.literal("By MinemasterLegacy");
    private static final MutableText UNDERLINED_TEXT = Text.literal("By MinemasterLegacy").formatted(Formatting.UNDERLINE);

    public CreditLayer(int x, int y) {
        super(x, y, MinecraftClient.getInstance().textRenderer.getWidth(TEXT) + 4, 12, Text.empty());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0xFFFFFFFF);
        if (this.isHovered()) context.setCursor(StandardCursors.POINTING_HAND);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    public void drawWidget(DrawContext context, TextRenderer textRenderer) {
        context.drawText(textRenderer,
                isHovered() ? UNDERLINED_TEXT : TEXT,
                getX() + 2,
                getY() + 1,
                TEXT_COLOR,
                true);
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        //FullscreenMapScreen.openBugReportScreen();
        MapScreen.openLinkScreen("https://github.com/MinemasterLegacy/Open-Mine-Map/wiki", ConfigScreen.getInstance(), false);
    }
}
