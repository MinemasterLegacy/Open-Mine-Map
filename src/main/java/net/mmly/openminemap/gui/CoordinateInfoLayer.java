package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.draw.Justify;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.UnitConvert;

public class CoordinateInfoLayer extends ClickableWidget {

    private static boolean showMouseCoordinates = true;
    private static boolean showPlayerCoordinates = true;
    private static boolean showDistortion = true;
    private static final String NULL_COORDINATE_STRING = "-.-";
    private static final String MAX_LENGTH_COORDINATE_STRING = "-99.99999°, -999.99999°";
    private static final String MAX_LENGTH_DISTORTION_STRING = "9.9999 ±9.9999°";
    private static final int LINE_HEIGHT = 16;
    private static final int MIN_WIDTH = 24;
    private static final int TEXT_MARGIN = 4;

    public CoordinateInfoLayer() {
        super(0, 0, 0, 0, Text.of(""));
        setWidth(getMaxWidth(MinecraftClient.getInstance().textRenderer));
        showDistortion = ConfigOptions.DISTORTION_DISPLAY.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF);
        setHeight(
                (showMouseCoordinates ? LINE_HEIGHT : 0) +
                (showPlayerCoordinates ? LINE_HEIGHT : 0) +
                (showDistortion ? LINE_HEIGHT : 0)
        );
    }

    private int getMaxWidth(TextRenderer textRenderer) {
        return Math.max(
                Math.max(
                        Math.max(
                                showMouseCoordinates ? textRenderer.getWidth(Text.translatable("omm.fullscreen.mouse-coordinates-label").getString() + MAX_LENGTH_COORDINATE_STRING) : 0,
                                showPlayerCoordinates ? textRenderer.getWidth(Text.translatable("omm.fullscreen.player-coordinates-label").getString() + MAX_LENGTH_COORDINATE_STRING) : 0
                        ),
                        showDistortion ? textRenderer.getWidth(Text.translatable("omm.fullscreen.distortion-label").getString() + MAX_LENGTH_DISTORTION_STRING) : 0
                ),
                MIN_WIDTH
        ) + 2 * TEXT_MARGIN;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        setY(context.getScaledWindowHeight()
                - (showDistortion ? LINE_HEIGHT : 0)
                - (showPlayerCoordinates ? LINE_HEIGHT : 0)
                - (showMouseCoordinates ? LINE_HEIGHT : 0)
        );
    }

    public void drawWidget(int windowHeight) {
        String mouseDisplayLong = NULL_COORDINATE_STRING;
        String mouseDisplayLat = NULL_COORDINATE_STRING;
        String playerDisplayLat = NULL_COORDINATE_STRING;
        String playerDisplayLon = NULL_COORDINATE_STRING;
        String distortionBaseDisplay = NULL_COORDINATE_STRING;
        String distortionMarginDisplay = NULL_COORDINATE_STRING;
        // ±

        if (!MapScreen.map.mouseIsOutOfBounds()) {
            mouseDisplayLong = UnitConvert.floorToPlace(MapScreen.map.getMouseLong(), 5);
            mouseDisplayLat = UnitConvert.floorToPlace(MapScreen.map.getMouseLat(), 5);
        }
        if (PlayerAttributes.positionIsValid()) {
            double lon = PlayerAttributes.getLongitude();
            double lat = PlayerAttributes.getLatitude();
            playerDisplayLon = UnitConvert.floorToPlace(lon, 5);
            playerDisplayLat = UnitConvert.floorToPlace(lat, 5);
            try {
                double[] distortion = Projection.getDistortion(lon, lat);
                if (distortion[0] >= 10) distortionBaseDisplay = ">10.0";
                else distortionBaseDisplay = UnitConvert.floorToPlace(distortion[0], 4);
                if (distortion[1] >= 10) distortionMarginDisplay = ">10.0";
                else distortionMarginDisplay = UnitConvert.floorToPlace(distortion[1], 4);
            } catch (CoordinateValueError ignored) {}
        } else {
            MapScreen.map.setFollowPlayer(false);
        }

        int lineNum = 1;
        if (showDistortion) {
            drawTextLine(
                    MinecraftClient.getInstance().textRenderer,
                    Text.translatable("omm.fullscreen.distortion-label").getString() + distortionBaseDisplay + " ±" + distortionMarginDisplay + "°",
                    lineNum++,
                    windowHeight);
        }
        if (showPlayerCoordinates) {
            drawTextLine(
                    MinecraftClient.getInstance().textRenderer,
                    Text.translatable("omm.fullscreen.player-coordinates-label").getString() + playerDisplayLat + "°, " + playerDisplayLon + "°",
                    lineNum++,
                    windowHeight);
        }
        if (showMouseCoordinates) {
            drawTextLine(
                    MinecraftClient.getInstance().textRenderer,
                    Text.translatable("omm.fullscreen.mouse-coordinates-label").getString() + mouseDisplayLat + "°, " + mouseDisplayLong + "°",
                    lineNum++,
                    windowHeight);
        }


    }

    private void drawTextLine(TextRenderer renderer, String text, int num, int windowHeight) {
        UContext.fillZone(0, windowHeight - num * LINE_HEIGHT, TEXT_MARGIN * 2 + renderer.getWidth(Text.of(text)), LINE_HEIGHT, MapScreen.backingColor);
        UContext.drawJustifiedText(Text.of(text), Justify.LEFT, TEXT_MARGIN, TEXT_MARGIN + windowHeight - num * LINE_HEIGHT, MapScreen.getPlainTextColor(), true);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
