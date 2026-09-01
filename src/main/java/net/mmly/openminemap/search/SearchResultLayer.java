package net.mmly.openminemap.search;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.http.RequestManager;
import org.lwjgl.glfw.GLFW;

import java.time.Duration;

public class SearchResultLayer extends AbstractWidget {

    private int resultNumber;
    private SearchResult myResult;

    public SearchResultLayer(int x, int y, int width, int resultNumber) {
        super(x, y, width, 20, Component.nullToEmpty(""));
        this.resultNumber = resultNumber;
        this.setTooltipDelay(Duration.ofMillis(500));
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (this.isHovered()) context.requestCursor(CursorTypes.POINTING_HAND);
    }

    public void setResult(SearchResult result) {
        myResult = result;
        if (result == null) {
            setY(-50);
        }
    }

    private int getResultColor() {
        if (myResult.resultType.isSearchType()) {
            return resultNumber % 2 == 0 ? 0xFF0BD604 : 0xFF0DFF05;
        } else {
            return resultNumber % 2 == 0 ? 0xFF0447D8 : 0xFF0554FF;
        }
    }

    public void drawWidget(GuiGraphicsExtractor context, Font renderer) {
        //context.drawBorder(getX(), getY(), getX() + width, getY() + height, 0xFFFF0000);

        if (!MapScreen.getSearchMenuState() || myResult == null || !SearchBoxLayer.isResultVisible(resultNumber)) {
            visible = false;
            return;
        }
        visible = true;

        context.fill(getX(), getY(), getX() + width, getY() + height, MapScreen.backingColor);
        context.fill(getX(), getY(), getX() + 4, getY() + height, getResultColor());
        if (isFocused()) {
            UContext.drawBorder(getX(), getY(), width, height, getResultColor());
            MapScreen.map.setFocusedResult(resultNumber);
        }

        context.enableScissor(getX(), getY(), getX() + width - 20 - (myResult.historic ? 20 : 0), getY() + height);
        context.text(renderer, myResult.name, getX() + 8, getY() + 6, MapScreen.getPlainTextColor(), true);
        if (!myResult.context.isBlank()) {
            context.text(renderer, myResult.context, getX() + 16 + renderer.width(myResult.name), getY() + 6, myResult.resultType.isSearchType() ? 0xFF548AF7 : MapScreen.getSemiDarkTextColor(), true);
            //renderer.fontHeight = 5;
            //context.drawText();
        }
        context.disableScissor();

        MutableComponent tooltip = Component.literal(myResult.name);
        if (!myResult.name.isBlank() && !myResult.context.isBlank()) tooltip.append("\n");
        if (!myResult.context.isBlank()) tooltip = tooltip.append(Component.literal(myResult.context).withStyle(ChatFormatting.GRAY));
        setTooltip(Tooltip.create(tooltip));

        if (myResult.resultType != SearchResultType.LOCATION) context.blit(
                RenderPipelines.GUI_TEXTURED,
                Identifier.fromNamespaceAndPath("openminemap", "search/" + myResult.resultType.toString().toLowerCase() + ".png"),
                getX() + getWidth() - 17,
                getY() + 3,
                0,
                0,
                14,
                14,
                14,
                14
        );
        if (myResult.historic) context.blit(
                RenderPipelines.GUI_TEXTURED,
                Identifier.fromNamespaceAndPath("openminemap", "search/history.png"),
                getX() + getWidth() - 32,
                getY() + 3,
                0,
                0,
                14,
                14,
                14,
                14
        );
        else if (myResult.resultType.isSearchType()) context.blit(
                RenderPipelines.GUI_TEXTURED,
                Identifier.fromNamespaceAndPath("openminemap", "search/photon.png"),
                getX() + getWidth() - 34,
                getY() + 3,
                0,
                0,
                14,
                14,
                14,
                14);

    }

    @Override
    public void setFocused(boolean focused) {
        if (focused) MapScreen.semiTransparentUi = true;
        super.setFocused(focused);
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        if (isFocused()) goToResult();
    }

    public boolean isOption(SearchResultType type) {
        if (myResult == null) return false;
        return myResult.resultType == type;
    }

    public boolean isHistoric() {
        if (myResult == null) return false;
        return myResult.historic;
    }

    private void goToResult() {

        if (myResult.historic) {
            SearchBoxLayer.showHistoricResult(myResult);
            return;
        }

        if (myResult.resultType == SearchResultType.SEARCH) {
            RequestManager.search(MapScreen.getInstance().getSearchBoxContents());
            return;
        }

        if (myResult.resultType == SearchResultType.SEARCHLOCAL) {
            RequestManager.search(
                    MapScreen.getInstance().getSearchBoxContents(),
                    MapScreen.map.getMapCenterLat(),
                    MapScreen.map.getMapCenterLon()
            );
            return;
        }

        if (myResult.resultType == SearchResultType.COORDINATES) {
            MapScreen.map.setSearchResults(new SearchResult[]{myResult});
            MapScreen.map.setFocusedResult(0);
        }

        myResult.focusOnMapViaSearchMenu();
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.input() == GLFW.GLFW_KEY_ENTER) {
            goToResult();
            return true;
        }

        MapScreen.getInstance().jumpToSearchBox(input);
        return true;

        //return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        SearchBoxLayer.scrollMenu(verticalAmount);
        return false;
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        //no sound
    }
}
