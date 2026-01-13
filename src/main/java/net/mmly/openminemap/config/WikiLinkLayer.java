package net.mmly.openminemap.config;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.mmly.openminemap.gui.FullscreenMapScreen;

import static net.mmly.openminemap.config.ConfigScreen.windowScaledHeight;
import static net.mmly.openminemap.config.ConfigScreen.windowScaledWidth;

public class WikiLinkLayer extends ClickableWidget {

    public WikiLinkLayer(int x, int y) {
        super(x, y, 91, 16, Text.empty());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0xFFFFFFFF);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    public void drawWidget(DrawContext context, TextRenderer textRenderer) {
        MutableText text = Text.translatable("omm.config.gui.omm-wiki");
        int textWidth = textRenderer.getWidth(text);
        //context.fill(windowScaledWidth - 70, windowScaledHeight - 32, windowScaledWidth, windowScaledHeight - 16, 0x88000000);
        context.drawText(textRenderer,
                isHovered() ?
                        text.formatted(Formatting.UNDERLINE) :
                        text,
                windowScaledWidth - (textWidth + 5),
                windowScaledHeight + 7 - textRenderer.fontHeight - 10 - 16,
                0xFF890792,
                true);
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        //FullscreenMapScreen.openBugReportScreen();
        FullscreenMapScreen.openLinkScreen("https://github.com/MinemasterLegacy/Open-Mine-Map/wiki", ConfigScreen.getInstance(), false);
    }
}
