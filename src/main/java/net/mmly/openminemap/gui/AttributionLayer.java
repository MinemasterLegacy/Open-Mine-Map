package net.mmly.openminemap.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.raster.LayerType;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.ArrayList;
import java.util.Arrays;

public class AttributionLayer extends AbstractWidget {

    public final int textWidth;
    private String visibleAttribution; // does *not* contain curly braces
    private String fullAttribution; // does contain curly braces
    private int hoveredZone = -1;
    private int oldScreenWidth = 0;

    ArrayList<ArrayList<String>> words = new ArrayList<>();
    ArrayList<int[]> clickZones = new ArrayList<>();
    ArrayList<Integer> clickLinkPointers = new ArrayList<>();
    ArrayList<String> attributionLinks = new ArrayList<>();

    private static final int LINK_COLOR = 0xFF548AF7;
    public static final int LINE_HEIGHT = 16;
    public static final int X_MARGIN = 4;
    public static final int Y_MARGIN = 4;
    public static final int MIN_WIDTH = 120;
    private final int SPACE_WIDTH;

    private void setAttributionInfo() {
        ArrayList<String> attributionStrings = new ArrayList<>();

        for (TileUrl raster : RasterProvider.getCurrentOverlays()) {
            if (raster.layerType == LayerType.LOCAL_GEN) continue;
            attributionStrings.add(raster.attribution);
            attributionLinks.addAll(Arrays.asList(raster.attribution_links));
        }

        if (RasterProvider.getCurrentBaseRaster().presetID != TileUrl.OPENSTREETMAP_PRESET_ID) {
            attributionStrings.add(RasterProvider.getCurrentBaseRaster().attribution);
            attributionLinks.addAll(Arrays.asList(RasterProvider.getCurrentBaseRaster().attribution_links));
        }

        attributionStrings.add(TileUrlFile.osmAttribution);
        attributionLinks.add(TileUrlFile.osmAttributionUrl);

        fullAttribution = String.join(" | ", attributionStrings);
        visibleAttribution = fullAttribution.replace("{", "").replace("}", "");
    }

    public AttributionLayer(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        TileUrlFile.initOsmAttribution();
        setAttributionInfo();
        textWidth = Minecraft.getInstance().font.width(visibleAttribution);
        SPACE_WIDTH = Minecraft.getInstance().font.width(" ");
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000);
        if (this.isHovered()) context.requestCursor(CursorTypes.POINTING_HAND);
        calculateSelection(mouseX, mouseY);
    }

    public void updatePositionAndDimensions(int coordinateWidth, int screenWidth, int screenHeight) {
        if (oldScreenWidth == screenWidth) return;
        Font textRenderer = Minecraft.getInstance().font;

        setWidth(Math.max(screenWidth - coordinateWidth, MIN_WIDTH));
        parseAttributionString(textRenderer, fullAttribution);
        if (words.size() == 1) width = textRenderer.width(fullAttribution.replaceAll("[{}]", "")) + 2 * X_MARGIN;

        oldScreenWidth = screenWidth;
        setHeight(words.size() * LINE_HEIGHT);
        setPosition(screenWidth - width, screenHeight - height);
    }

    enum SelectState {
        NONE,
        LINK,
        HOVER
    }

    public void drawWidget(GuiGraphics context, Font textRenderer) {
        context.fill(getX(), getY(), getRight(), getBottom(), MapScreen.backingColor);
        int offsetX = X_MARGIN;
        int offsetY = Y_MARGIN;
        for (int l = 0; l < words.size(); l++) {
            ArrayList<String> line = words.get(l);
            for (int i = 0; i < line.size(); i++) {
                String word = line.get(i);

                SelectState selectState = determineSelectState(l, offsetX);
                context.drawString(
                        textRenderer,
                        Component.literal(word).withStyle(selectState == SelectState.HOVER ? ChatFormatting.UNDERLINE : ChatFormatting.RESET),
                        getX() + offsetX,
                        getY() + offsetY,
                        selectState == SelectState.NONE ? MapScreen.getPlainTextColor() : LINK_COLOR,
                        true
                );

                offsetX += textRenderer.width(word);
                if (line.size() - 1 == i) continue;

                selectState = determineSelectState(l, offsetX);
                context.drawString(
                        textRenderer,
                        Component.literal(" ").withStyle(selectState == SelectState.HOVER ? ChatFormatting.UNDERLINE : ChatFormatting.RESET),
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
                if (hoveredZone == clickLinkPointers.get(i)) return SelectState.HOVER;
                else return SelectState.LINK;
            };
        }
        return SelectState.NONE;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {}

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
                hoveredZone = clickLinkPointers.get(i);
                return;
            }
        }
        hoveredZone = -1;
    }

    private void parseAttributionString(Font textRenderer, String attributionString) {
        StringBuilder builder = new StringBuilder();
        boolean inLink = false;
        int linkPointer = 0;
        int linkStartX = 0;
        int lineWidth = 0;
        int line = 0;
        int maxWidth = width - 2 * X_MARGIN;

        words.clear();
        clickLinkPointers.clear();
        clickZones.clear();

        words.add(new ArrayList<>());

        for (char c : attributionString.toCharArray()) {
            if (lineWidth >= maxWidth) {
                if (words.getLast().isEmpty()) {
                    words.getLast().add(builder.toString());
                    builder = new StringBuilder();
                }
                if (inLink) {
                    clickZones.add(new int[] {line, linkStartX, lineWidth - textRenderer.width(builder.toString())});
                    if (clickZones.getLast()[1] == clickZones.getLast()[2]) {
                        clickZones.remove(clickZones.getLast());
                    } else {
                        clickLinkPointers.add(linkPointer);
                    }
                }
                words.add(new ArrayList<>());
                lineWidth = textRenderer.width(builder.toString());
                linkStartX = 0;
                line++;
            }

            if (c == '{') {
                linkStartX = lineWidth;
                inLink = true;
            } else if (c == ' ') {
                words.getLast().add(builder.toString());
                builder = new StringBuilder();
                lineWidth += textRenderer.width(Character.toString(c));
            } else if (c == '}') {
                clickZones.add(new int[] {line, linkStartX, lineWidth});
                clickLinkPointers.add(linkPointer++);
                inLink = false;
            } else {
                builder.append(c);
                lineWidth += textRenderer.width(Character.toString(c));
            }
        }

        if (!builder.toString().isBlank()) {
            words.getLast().add(builder.toString());
        }

        /*
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
            System.out.print(clickLinkPointers.get(linkPointer++));
            for (int i = 0; i < zone[1]; i++) {
                System.out.print(" ");
            }
            for (int i = 0; i < zone[2] - zone[1]; i++) {
                System.out.print("=");
            }
            System.out.println();
        }

         */
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        if (hoveredZone == -1) return;
        String link = attributionLinks.get(hoveredZone);
        MapScreen.openLinkScreen(link, new MapScreen(), true);
    }
}
