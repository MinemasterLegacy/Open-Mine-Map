package net.mmly.openminemap.search;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.gui.FullscreenMapScreen;

public class SearchResultLayer extends ClickableWidget {

    private int resultNumber;
    private SearchResult myResult;

    public SearchResultLayer(int x, int y, int width, int resultNumber) {
        super(x, y, width, 20, Text.of(""));
        this.resultNumber = resultNumber;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    public void setResult(SearchResult result) {
        myResult = result;
    }

    public void drawWidget(DrawContext context, TextRenderer renderer) {
        //context.drawBorder(getX(), getY(), getX() + width, getY() + height, 0xFFFF0000);

        if (!FullscreenMapScreen.getSearchMenuState() || myResult == null) return;

        int highlightColor = resultNumber % 2 == 0 ? 0xFF0447D8 : 0xFF0554FF;
        context.fill(getX(), getY(), getX() + width, getY() + height, 0x80000000);
        context.fill(getX(), getY(), getX() + 4, getY() + height, highlightColor);
        if (isFocused()) context.drawBorder(getX(), getY(), getX() + width, getY() + height, highlightColor);

        context.enableScissor(getX(), getY(), getX() + width, getY() + height);
        context.drawText(renderer, myResult.name, getX() + 8, getY() + 6, 0xFFFFFFFF, false);
        if (!myResult.context.isBlank()) {
            //renderer.fontHeight = 5;
            //context.drawText();
        }
        context.disableScissor();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        //TODO
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
