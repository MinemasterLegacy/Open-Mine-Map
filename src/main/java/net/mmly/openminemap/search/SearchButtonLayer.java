package net.mmly.openminemap.search;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.gui.MapScreen;

public class SearchButtonLayer extends ClickableWidget {
    public SearchButtonLayer(int x, int y) {
        super(x, y, 20, 20, Text.of(""));
    }

    public void drawWidget(DrawContext context) {
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                isHovered() ?
                        Identifier.of("openminemap", "buttons/vanilla/hover/search.png") :
                        (MapScreen.getSearchMenuState() ?
                                Identifier.of("openminemap", "buttons/vanilla/locked/search.png") :
                                Identifier.of("openminemap", "buttons/vanilla/default/search.png")),
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
    public void onClick(Click click, boolean doubled) {
        MapScreen.toggleSearchMenu(!MapScreen.getSearchMenuState());
        MapScreen.getInstance().jumpToSearchBox();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        MapScreen.getInstance().jumpToSearchBox(input);
        return true;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
