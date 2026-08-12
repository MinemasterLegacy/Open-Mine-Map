package net.mmly.openminemap.gui;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrlFile;

import java.util.ArrayList;
import java.util.Arrays;

public class AttributionLayer extends ClickableWidget {

    public final int textWidth;
    private final String attribution;
    private final String attributionString;
    private int hoveredZone = -1;
    private int oldScreenWidth = 0;

    ArrayList<ArrayList<String>> words = new ArrayList<>();
    ArrayList<int[]> clickZones = new ArrayList<>();
    ArrayList<Integer> clickLinks = new ArrayList<>();

    private static final int LINK_COLOR = 0xFF548AF7;
    public static final int LINE_HEIGHT = 16;
    public static final int X_MARGIN = 4;
    public static final int Y_MARGIN = 4;
    public static final int MIN_WIDTH = 120;
    private final int SPACE_WIDTH;

    public AttributionLayer(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        String split = " | ";
        if (RasterProvider.getCurrentBaseRaster().presetID == 0) split = "";
        TileUrlFile.initOsmAttribution();
        attributionString = (TileUrlFile.osmAttribution + split + RasterProvider.getCurrentBaseRaster().attribution);
        attribution = (TileUrlFile.osmAttribution + split + RasterProvider.getCurrentBaseRaster().attribution).replaceAll("\\{", "").replaceAll("}", "");
        textWidth = MinecraftClient.getInstance().textRenderer.getWidth(attribution);
        SPACE_WIDTH = MinecraftClient.getInstance().textRenderer.getWidth(" ");
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000);
        calculateSelection(mouseX, mouseY);
    }

    public void updatePositionAndDimensions(int coordinateWidth, int screenWidth, int screenHeight) {
        if (oldScreenWidth == screenWidth) return;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        setWidth(Math.max(screenWidth - coordinateWidth, MIN_WIDTH));
        parseAttributionString(textRenderer, attributionString);
        if (words.size() == 1) width = textRenderer.getWidth(attributionString.replaceAll("[{}]", "")) + 2 * X_MARGIN;

        oldScreenWidth = screenWidth;
        setHeight(words.size() * LINE_HEIGHT);
        setPosition(screenWidth - width, screenHeight - height);
    }

    enum SelectState {
        NONE,
        LINK,
        HOVER
    }

    public void drawWidget(DrawContext context, TextRenderer textRenderer) {
        context.fill(getX(), getY(), getRight(), getBottom(), MapScreen.backingColor);
        int offsetX = X_MARGIN;
        int offsetY = Y_MARGIN;
        for (int l = 0; l < words.size(); l++) {
            ArrayList<String> line = words.get(l);
            for (int i = 0; i < line.size(); i++) {
                String word = line.get(i);

                SelectState selectState = determineSelectState(l, offsetX);
                context.drawText(
                        textRenderer,
                        Text.literal(word).formatted(selectState == SelectState.HOVER ? Formatting.UNDERLINE : Formatting.RESET),
                        getX() + offsetX,
                        getY() + offsetY,
                        selectState == SelectState.NONE ? MapScreen.getPlainTextColor() : LINK_COLOR,
                        true
                );

                offsetX += textRenderer.getWidth(word);
                if (line.size() - 1 == i) continue;

                selectState = determineSelectState(l, offsetX);
                context.drawText(
                        textRenderer,
                        Text.literal(" ").formatted(selectState == SelectState.HOVER ? Formatting.UNDERLINE : Formatting.RESET),
                        getX() + offsetX,
                        getY() + offsetY,
                        selectState == SelectState.NONE ? MapScreen.getPlainTextColor() : LINK_COLOR,
                        true
                );

                offsetX += SPACE_WIDTH;
            }
            offsetY += LINE_HEIGHT;
            offsetX = X_MARGIN;
        }
    }

    private SelectState determineSelectState(int line, int xOffset) {
        xOffset -= X_MARGIN;
        for (int i = 0; i < clickZones.size(); i++) {
            int[] zone = clickZones.get(i);
            if (zone[0] != line) continue;
            if (zone[1] <= xOffset && xOffset < zone[2]) {
                if (hoveredZone == clickLinks.get(i)) return SelectState.HOVER;
                else return SelectState.LINK;
            };
        }
        return SelectState.NONE;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    private void calculateSelection(double mouseX, double mouseY) {
        if (!isHovered()) {
            hoveredZone = -1;
            return;
        }

        int mouseLinkLine = (int) ((mouseY - getY()) / 16);
        int mouseLinkX = ((int) mouseX) - getX() - 4;

        for (int i = 0; i < clickZones.size(); i++) {
            int[] zone = clickZones.get(i);
            if (zone[0] != mouseLinkLine) continue;
            if (zone[1] <= mouseLinkX && mouseLinkX < zone[2]) {
                hoveredZone = clickLinks.get(i);
                return;
            }
        }
        hoveredZone = -1;
    }

    private void parseAttributionString(TextRenderer textRenderer, String attributionString) {
        StringBuilder builder = new StringBuilder();
        boolean inLink = false;
        int linkPointer = 0;
        int linkStartX = 0;
        int lineWidth = 0;
        int line = 0;
        int maxWidth = width - 2 * X_MARGIN;

        words.clear();
        clickLinks.clear();
        clickZones.clear();

        words.add(new ArrayList<>());

        for (char c : attributionString.toCharArray()) {
            if (lineWidth >= maxWidth) {
                if (words.getLast().isEmpty()) {
                    words.getLast().add(builder.toString());
                    builder = new StringBuilder();
                }
                if (inLink) {
                    clickZones.add(new int[] {line, linkStartX, lineWidth - textRenderer.getWidth(builder.toString())});
                    if (clickZones.getLast()[1] == clickZones.getLast()[2]) {
                        clickZones.remove(clickZones.getLast());
                    } else {
                        clickLinks.add(linkPointer);
                    }
                }
                words.add(new ArrayList<>());
                lineWidth = textRenderer.getWidth(builder.toString());
                linkStartX = 0;
                line++;
            }

            if (c == '{') {
                linkStartX = lineWidth;
                inLink = true;
            } else if (c == ' ') {
                words.getLast().add(builder.toString());
                builder = new StringBuilder();
                lineWidth += textRenderer.getWidth(Character.toString(c));
            } else if (c == '}') {
                clickZones.add(new int[] {line, linkStartX, lineWidth});
                clickLinks.add(linkPointer++);
                inLink = false;
            } else {
                builder.append(c);
                lineWidth += textRenderer.getWidth(Character.toString(c));
            }
        }

        if (!builder.toString().isBlank()) {
            words.getLast().add(builder.toString());
        }

        if (true) return;

        System.out.println(Arrays.toString(words.toArray()));
        System.out.println(Arrays.deepToString(clickZones.toArray()));

        System.out.println();
        for (ArrayList<String> lineArray : words) {
            System.out.print(" ");
            for (String s : lineArray) {
                System.out.print(s + " ");
            }
            System.out.println();
        }

        System.out.println();
        linkPointer = 0;
        for (int[] zone : clickZones) {
            System.out.print(clickLinks.get(linkPointer++));
            for (int i = 0; i < zone[1]; i++) {
                System.out.print(" ");
            }
            for (int i = 0; i < zone[2] - zone[1]; i++) {
                System.out.print("=");
            }
            System.out.println();
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (hoveredZone == -1) return;
        String link;
        if (hoveredZone == 0) link = TileUrlFile.osmAttributionUrl;
        else link = RasterProvider.getCurrentBaseRaster().attribution_links[hoveredZone - 1];
        MapScreen.openLinkScreen(link, new MapScreen(), true);
    }
}
