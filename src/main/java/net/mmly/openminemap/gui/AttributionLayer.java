package net.mmly.openminemap.gui;

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

import static net.mmly.openminemap.gui.MapScreen.windowScaledHeight;
import static net.mmly.openminemap.gui.MapScreen.windowScaledWidth;

public class AttributionLayer extends ClickableWidget {

    public final int textWidth;
    private final String attribution;
    private final String attributionString;
    private final char[] attributionCharArray; //todo remove
    private int[][] selectionZones;
    private int selection = -1;
    private int oldScreenWidth = 0;

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
        attributionCharArray = attributionString.toCharArray();
        attribution = (TileUrlFile.osmAttribution + split + RasterProvider.getCurrentBaseRaster().attribution).replaceAll("\\{", "").replaceAll("}", "");
        selectionZones = new int[(attributionCharArray.length - attribution.length() / 2)][2];
        textWidth = MinecraftClient.getInstance().textRenderer.getWidth(attribution);
        SPACE_WIDTH = MinecraftClient.getInstance().textRenderer.getWidth(" ");
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000);
        calculateSelection(mouseX);
    }

    public void updatePositionAndDimensions(int coordinateWidth, int screenWidth, int screenHeight) {
        if (oldScreenWidth == screenWidth) return;
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        setWidth(Math.max(screenWidth - coordinateWidth, MIN_WIDTH));
        parseAttributionString(textRenderer, attributionString);
        System.out.println("attr. string: \"" + attributionString + "\"");
        if (words.size() == 1) width = textRenderer.getWidth(attributionString.replaceAll("[{}]", "")) + 2 * X_MARGIN;

        oldScreenWidth = screenWidth;
        setHeight(words.size() * LINE_HEIGHT);
        setPosition(screenWidth - width, screenHeight - height);
    }

    public void newDrawWidget(DrawContext context, TextRenderer textRenderer) {
        context.fill(getX(), getY(), getRight(), getBottom(), MapScreen.backingColor);
        int offsetX = X_MARGIN;
        int offsetY = Y_MARGIN;
        for (ArrayList<String> line : words) {
            for (String word : line) {
                context.drawText(textRenderer, Text.of(word), getX() + offsetX, getY() + offsetY, 0xFFFF0000, true);
                offsetX += textRenderer.getWidth(word);
                if (line.getLast().equals(word)) continue;
                context.drawText(textRenderer, Text.of(" "), getX() + offsetX, getY() + offsetY, 0xFFFF0000, true);
                offsetX += SPACE_WIDTH;
            }
            offsetY += LINE_HEIGHT;
            offsetX = X_MARGIN;
        }
    }

    public void drawWidget(DrawContext context, TextRenderer textRenderer) {
        //context.fill(windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight, 0x88000000);
        //context.drawText(textRenderer, "Map data from", windowScaledWidth - 152, windowScaledHeight + 7 - textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
        //context.drawText(textRenderer, Text.of("OpenStreetMap"), windowScaledWidth - 77, windowScaledHeight + 7 - textRenderer.fontHeight - 10, 0xFF548AF7, true); //0xFF1b75d0

        context.fill(windowScaledWidth - textWidth - 8, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight, MapScreen.backingColor);

        int y = windowScaledHeight + 7 -textRenderer.fontHeight - 10;
        int drawCursorX = windowScaledWidth - textWidth - 3;
        int startX = drawCursorX;
        int attributionsCount = 0;
        StringBuilder bufferedText = new StringBuilder();
        //System.out.println("----");
        for (char currentChar : attributionCharArray) {
            //System.out.println(currentChar+"\t"+drawCursorX);
            if (currentChar == '{') {
                context.drawText(textRenderer, Text.of(bufferedText.toString()), startX, y, MapScreen.getPlainTextColor(), true);
            } else if (currentChar == '}') {
                context.drawText(textRenderer,
                        selection == attributionsCount ?
                                Text.literal(bufferedText.toString()).formatted(Formatting.UNDERLINE):
                                Text.of(bufferedText.toString()),
                        startX, y, 0xFF548AF7, true);
                selectionZones[attributionsCount][0] = startX;
                selectionZones[attributionsCount][1] = drawCursorX;
                attributionsCount++;
            } else {
                //System.out.print(currentChar);
                bufferedText.append(currentChar);
                drawCursorX += (textRenderer.getWidth(Character.toString(currentChar)));
                continue;
            }
            startX = drawCursorX;
            //System.out.println("StartX -> "+startX);
            bufferedText = new StringBuilder();
        }
        context.drawText(textRenderer, Text.of(bufferedText.toString()), startX, y, MapScreen.getPlainTextColor(), true);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    private void calculateSelection(double mouseX) {
        if (!isHovered()) {
            selection = -1;
            return;
        }
        for (int i = 0 ; i < selectionZones.length ; i++) {
            if (mouseX > selectionZones[i][0] && mouseX < selectionZones[i][1]) {
                selection = i;
                return;
            }
        }
        selection = -1;
    }

    ArrayList<ArrayList<String>> words = new ArrayList<>();
    ArrayList<int[]> clickZones = new ArrayList<>();
    ArrayList<Integer> clickLinks = new ArrayList<>();

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
                    line++;
                }
                words.add(new ArrayList<>());
                if (inLink) {
                    clickZones.add(new int[] {line, linkStartX, lineWidth - textRenderer.getWidth(builder.toString()) - SPACE_WIDTH});
                    clickLinks.add(linkPointer);
                }
                lineWidth = textRenderer.getWidth(builder.toString());
                linkStartX = 0;
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
        if (selection == -1) return;
        String link;
        if (selection == 0) link = TileUrlFile.osmAttributionUrl;
        else link = RasterProvider.getCurrentBaseRaster().attribution_links[selection - 1];
        MapScreen.openLinkScreen(link, new MapScreen(), true);
    }
}
