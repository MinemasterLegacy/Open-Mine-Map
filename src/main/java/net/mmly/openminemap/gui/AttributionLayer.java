package net.mmly.openminemap.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.util.TileUrlFile;

import java.util.Arrays;
import java.util.Objects;

import static net.mmly.openminemap.gui.FullscreenMapScreen.windowScaledHeight;
import static net.mmly.openminemap.gui.FullscreenMapScreen.windowScaledWidth;

public class AttributionLayer extends ClickableWidget {

    public int textWidth;
    private final String attribution;
    private final char[] attributionString;
    private int[][] selectionZones;

    public AttributionLayer(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        String split = " | ";
        if (TileUrlFile.getCurrentUrl().attribution.equals("")) split = "";
        attributionString = (TileUrlFile.osmAttribution + split + TileUrlFile.getCurrentUrl().attribution).toCharArray();
        attribution = (TileUrlFile.osmAttribution + split + TileUrlFile.getCurrentUrl().attribution).replaceAll("\\{", "").replaceAll("}", "");
        selectionZones = new int[(attributionString.length - attribution.length() / 2)][2];
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000);
    }

    public void drawWidget(DrawContext context, TextRenderer textRenderer) {
        //context.fill(windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight, 0x88000000);
        //context.drawText(textRenderer, "Map data from", windowScaledWidth - 152, windowScaledHeight + 7 - textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
        //context.drawText(textRenderer, Text.of("OpenStreetMap"), windowScaledWidth - 77, windowScaledHeight + 7 - textRenderer.fontHeight - 10, 0xFF548AF7, true); //0xFF1b75d0

        textWidth = textRenderer.getWidth(attribution);
        context.fill(windowScaledWidth - textWidth - 8, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight, 0x88000000);

        int y = windowScaledHeight + 7 -textRenderer.fontHeight - 10;
        int drawCursorX = windowScaledWidth - textWidth - 3;
        int startX = drawCursorX;
        int attributionsCount = 0;
        StringBuilder bufferedText = new StringBuilder();
        //System.out.println("----");
        for (char currentChar : attributionString) {
            //System.out.println(currentChar+"\t"+drawCursorX);
            if (currentChar == '{') {
                context.drawText(textRenderer, Text.of(bufferedText.toString()), startX, y, 0xFFFFFFFF, true);
            } else if (currentChar == '}') {
                context.drawText(textRenderer, Text.of(bufferedText.toString()), startX, y, 0xFF548AF7, true);
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
        context.drawText(textRenderer, Text.of(bufferedText.toString()), startX, y, 0xFFFFFFFF, true);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public void onClick(double mouseX, double mouseY) {
        for (int i = 0 ; i < selectionZones.length ; i++) {
            if (mouseX > selectionZones[i][0] && mouseX < selectionZones[i][1]) {
                String link;
                if (i == 0) link = TileUrlFile.osmAttributionUrl;
                else link = TileUrlFile.getCurrentUrl().attribution_links[i - 1];
                FullscreenMapScreen.openLinkScreen(link, new FullscreenMapScreen());
            }
        }
    }
}
