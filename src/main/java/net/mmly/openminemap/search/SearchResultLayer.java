package net.mmly.openminemap.search;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.map.RequestManager;
import org.lwjgl.glfw.GLFW;

import java.time.Duration;

public class SearchResultLayer extends ClickableWidget {

    private int resultNumber;
    private SearchResult myResult;

    public SearchResultLayer(int x, int y, int width, int resultNumber) {
        super(x, y, width, 20, Text.of(""));
        this.resultNumber = resultNumber;
        this.setTooltipDelay(Duration.ofMillis(500));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

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

    public void drawWidget(DrawContext context, TextRenderer renderer) {
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
        context.drawText(renderer, myResult.name, getX() + 8, getY() + 6, MapScreen.getPlainTextColor(), true);
        if (!myResult.context.isBlank()) {
            context.drawText(renderer, myResult.context, getX() + 16 + renderer.getWidth(myResult.name), getY() + 6, myResult.resultType.isSearchType() ? 0xFF548AF7 : MapScreen.getSemiDarkTextColor(), true);
            //renderer.fontHeight = 5;
            //context.drawText();
        }
        context.disableScissor();

        MutableText tooltip = Text.literal(myResult.name);
        if (!myResult.name.isBlank() && !myResult.context.isBlank()) tooltip.append("\n");
        if (!myResult.context.isBlank()) tooltip = tooltip.append(Text.literal(myResult.context).formatted(Formatting.GRAY));
        setTooltip(Tooltip.of(tooltip));

        if (myResult.resultType != SearchResultType.LOCATION) context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                Identifier.of("openminemap", "search/" + myResult.resultType.toString().toLowerCase() + ".png"),
                getX() + getWidth() - 17,
                getY() + 3,
                0,
                0,
                14,
                14,
                14,
                14
        );
        if (myResult.historic) context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                Identifier.of("openminemap", "search/history.png"),
                getX() + getWidth() - 32,
                getY() + 3,
                0,
                0,
                14,
                14,
                14,
                14
        );
        else if (myResult.resultType.isSearchType()) context.drawTexture(
                RenderPipelines.GUI_TEXTURED,
                Identifier.of("openminemap", "search/photon.png"),
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
    public void onClick(Click click, boolean doubled) {
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
            RequestManager.setSearchRequest(MapScreen.getInstance().getSearchBoxContents());
            return;
        }

        if (myResult.resultType == SearchResultType.SEARCHLOCAL) {
            RequestManager.setSearchRequest(
                    MapScreen.getInstance().getSearchBoxContents(),
                    MapScreen.map.getMapCenterLat(),
                    MapScreen.map.getMapCenterLon()
            );
            return;
        }

        if (myResult.resultType == SearchResultType.COORDINATES) {
            MapScreen.map.displaySearchResults(new SearchResult[]{myResult});
            MapScreen.map.setFocusedResult(0);
        }

        myResult.focusOnMapViaSearchMenu();
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.getKeycode() == GLFW.GLFW_KEY_ENTER) {
            goToResult();
            return true;
        }

        MapScreen.getInstance().jumpToSearchBox(input);
        return true;

        //return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

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
