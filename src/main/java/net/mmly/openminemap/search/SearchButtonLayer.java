package net.mmly.openminemap.search;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.gui.ButtonLayer;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.util.ColorUtil;

public class SearchButtonLayer extends AbstractWidget {
    public SearchButtonLayer(int x, int y) {
        super(x, y, 20, 20, Component.nullToEmpty(""));
    }

    private static final Identifier defaultIdentifier = Identifier.fromNamespaceAndPath("openminemap", "buttons/vanilla/default/search.png");
    private static final Identifier highlightedIdentifer = Identifier.fromNamespaceAndPath("openminemap", "buttons/vanilla/hover/search.png");
    private static final Identifier disabledIdentifer = Identifier.fromNamespaceAndPath("openminemap", "buttons/vanilla/locked/search.png");
    private static final Identifier generatedIdentifier = Identifier.fromNamespaceAndPath("openminemap", "buttons/vanilla/generated/search.png");
    private static final Identifier generatedShadowIdentifier = ColorUtil.getColoredIdentifier(generatedIdentifier,0x00003e);
    private static final Identifier generatedDarkIdentifier = ColorUtil.getColoredIdentifier(generatedIdentifier, 0x00007f);

    public void drawWidget(GuiGraphicsExtractor context) {
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
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (this.isHovered()) {
            context.requestCursor(CursorTypes.POINTING_HAND);
        }
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        MapScreen.toggleSearchMenu(!MapScreen.getSearchMenuState());
        MapScreen.getInstance().jumpToSearchBox();
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        MapScreen.getInstance().jumpToSearchBox(input);
        return true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }
}
