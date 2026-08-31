package net.mmly.openminemap.search;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.util.ColorUtil;

public class SearchButtonLayer extends ClickableWidget {
    public SearchButtonLayer(int x, int y) {
        super(x, y, 20, 20, Text.of(""));
    }

    private static final Identifier defaultIdentifier = Identifier.of("openminemap", "buttons/vanilla/default/search.png");
    private static final Identifier highlightedIdentifer = Identifier.of("openminemap", "buttons/vanilla/hover/search.png");
    private static final Identifier disabledIdentifer = Identifier.of("openminemap", "buttons/vanilla/locked/search.png");
    private static final Identifier generatedIdentifier = Identifier.of("openminemap", "buttons/vanilla/generated/search.png");
    private static final Identifier generatedShadowIdentifier = ColorUtil.getColoredIdentifier(generatedIdentifier,0x00003e);
    private static final Identifier generatedDarkIdentifier = ColorUtil.getColoredIdentifier(generatedIdentifier, 0x00007f);

    public void drawWidget(DrawContext context) {
        if (!ButtonLayer.texturedButtons) {
            UContext.drawButtonOnWidget(this, MapScreen.getSearchMenuState() && !isHovered(), isHovered());
            UContext.drawTexture(generatedShadowIdentifier, getX() + 1, getY() + 1, getWidth(), getHeight());
        }
        UContext.drawTexture(getIdentifier(MapScreen.getSearchMenuState(), isHovered()), getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void setFocused(boolean focused) {
        if (focused) MapScreen.semiTransparentUi = true;
        super.setFocused(focused);
    }

    private Identifier getIdentifier(boolean disabled, boolean highlighted) {
        if (!ButtonLayer.texturedButtons) {
            if (highlighted) return generatedIdentifier;
            if (disabled) return generatedDarkIdentifier;
            return generatedIdentifier;
        }
        if (highlighted) return highlightedIdentifer;
        if (disabled) return disabledIdentifer;
        return defaultIdentifier;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        MapScreen.toggleSearchMenu(!MapScreen.getSearchMenuState());
        MapScreen.getInstance().jumpToSearchBox();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        MapScreen.getInstance().jumpToSearchBox(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
