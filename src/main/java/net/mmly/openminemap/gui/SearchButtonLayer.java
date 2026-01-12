package net.mmly.openminemap.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SearchButtonLayer extends ClickableWidget {
    public SearchButtonLayer(int x, int y) {
        super(x, y, 20, 20, Text.of(""));
    }

    public void drawWidget(DrawContext context) {
        context.drawTexture(
                isHovered() ?
                        Identifier.of("openminemap", "buttons/vanilla/hover/search.png") :
                        Identifier.of("openminemap", "buttons/vanilla/default/search.png"),
                getX(),
                getY(),
                0,
                0,
                width,
                height,
                width,
                height
        );
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        FullscreenMapScreen.toggleSearchMenu(!FullscreenMapScreen.getSearchMenuState());
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
