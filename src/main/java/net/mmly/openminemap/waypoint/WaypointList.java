package net.mmly.openminemap.waypoint;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.mmly.openminemap.config.ConfigAnchorWidget;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ConfigFile;

public class WaypointList extends EntryListWidget<WaypointAnchorWidget> {

    private static double savedScrollAmount;

    public WaypointList(MinecraftClient minecraftClient, int width, int height, int y, int itemHeight) {
        super(minecraftClient, width, height, y, itemHeight);
    }

    public void restoreScroll() {
        setScrollAmount(savedScrollAmount);
    }

    @Override
    public int addEntry(WaypointAnchorWidget entry) {
        return super.addEntry(entry);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        savedScrollAmount = getScrollAmount();
    }

    @Override
    protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
        //nothing
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (ConfigFile.readParameter(ConfigOptions.REVERSE_SCROLL).equals("on")) verticalAmount *= -1;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}