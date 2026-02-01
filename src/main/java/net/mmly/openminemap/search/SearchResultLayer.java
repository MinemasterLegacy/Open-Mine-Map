package net.mmly.openminemap.search;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.map.RequestManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.UnitConvert;
import org.lwjgl.glfw.GLFW;

import java.time.Duration;
import java.util.Arrays;

public class SearchResultLayer extends ClickableWidget {

    private int resultNumber;
    private SearchResult myResult;
    private int yPos;

    public SearchResultLayer(int x, int y, int width, int resultNumber) {
        super(x, y, width, 20, Text.of(""));
        this.resultNumber = resultNumber;
        yPos = y;
        this.setTooltipDelay(Duration.ofMillis(500));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    public void setResult(SearchResult result) {
        myResult = result;
        if (result == null) {
            setY(-50);
        } else {
            setY(yPos);
        }
    }

    private int getResultColor() {
        if (myResult.resultType == SearchResultType.SEARCH) {
            return resultNumber % 2 == 0 ? 0xFF0BD604 : 0xFF0DFF05;
        } else {
            return resultNumber % 2 == 0 ? 0xFF0447D8 : 0xFF0554FF;
        }
    }

    public void drawWidget(DrawContext context, TextRenderer renderer) {
        //context.drawBorder(getX(), getY(), getX() + width, getY() + height, 0xFFFF0000);

        if (!FullscreenMapScreen.getSearchMenuState() || myResult == null) {
            visible = false;
            return;
        }
        visible = true;

        context.fill(getX(), getY(), getX() + width, getY() + height, 0x80000000);
        context.fill(getX(), getY(), getX() + 4, getY() + height, getResultColor());
        if (isFocused()) context.drawBorder(getX(), getY(), width, height, getResultColor());

        context.enableScissor(getX(), getY(), getX() + width - 20 - (myResult.historic ? 20 : 0), getY() + height);
        context.drawText(renderer, myResult.name, getX() + 8, getY() + 6, 0xFFFFFFFF, false);
        if (!myResult.context.isBlank()) {
            context.drawText(renderer, myResult.context, getX() + 16 + renderer.getWidth(myResult.name), getY() + 6, myResult.resultType == SearchResultType.SEARCH ? 0xFF548AF7 : 0xFFB0B0B0, false);
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
        if (myResult.resultType == SearchResultType.SEARCH) context.drawTexture(
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
        else if (myResult.historic) context.drawTexture(
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
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (isFocused()) goToResult();
    }

    public boolean isOption(SearchResultType type) {
        if (myResult == null) return false;
        return myResult.resultType == type;
    }

    private void goToResult() {

        if (myResult.resultType == SearchResultType.SEARCH) {
            RequestManager.setSearchRequest(FullscreenMapScreen.getInstance().getSearchBoxContents());
            return;
        }

        if (myResult.bounds != null) {
            goAndZoomToResult(myResult.bounds);
            return;
        }

        FullscreenMapScreen.map.setMapPosition(
                UnitConvert.longToMapX(myResult.longitude, FullscreenMapScreen.map.getZoom(), FullscreenMapScreen.map.getTileSize()),
                UnitConvert.latToMapY(myResult.latitude, FullscreenMapScreen.map.getZoom(), FullscreenMapScreen.map.getTileSize())
        );

        if (myResult.zoom != -1) {
            FullscreenMapScreen.map.setMapZoom(myResult.zoom);
        }
        FullscreenMapScreen.map.clampZoom();

    }

    private static final double log2 = Math.log(2);
    private void goAndZoomToResult(double[] bounds) { // for bounds: length is 4, first 2 are lat, last 2 are long
        OmmMap map = FullscreenMapScreen.map;

        double areaWidth = Math.abs(
                UnitConvert.longToMapX(bounds[2], 0, 128) -
                UnitConvert.longToMapX(bounds[3], 0, 128)
        );
        double areaHeight = Math.abs(
                UnitConvert.latToMapY(bounds[0], 0, 128) -
                UnitConvert.latToMapY(bounds[1], 0, 128)
        );

        double percentage = (Math.max(areaHeight, areaWidth) / 128) * 1.15; //multiply by 1.15 to add some empty space around the focused area

        /*
        System.out.println(
                map.getRenderAreaWidth() + "\t" +
                map.getRenderAreaHeight() + "\t" +
                percentage + "\t" +
                Math.log( Math.min(map.getRenderAreaHeight(), map.getRenderAreaWidth()) / (128 * percentage) ) / log2
        );
        */

        System.out.println(Arrays.toString(bounds));

        map.setMapZoom(
                Math.log( Math.min(map.getRenderAreaHeight(), map.getRenderAreaWidth()) / (128 * percentage) ) / log2
        );

        double areaCenterY = (bounds[0] + bounds[1]) / 2;
        double areaCenterX = (bounds[2] + bounds[3]) / 2;

        map.setMapPosition(
                UnitConvert.longToMapX(areaCenterX, map.getZoom(), map.getTileSize()),
                UnitConvert.latToMapY(areaCenterY, map.getZoom(), map.getTileSize())
        );

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            goToResult();
            return true;
        }

        FullscreenMapScreen.getInstance().jumpToSearchBox(keyCode, scanCode, modifiers);
        return true;

        //return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
