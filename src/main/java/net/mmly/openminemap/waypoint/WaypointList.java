package net.mmly.openminemap.waypoint;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.util.ConfigFile;

public class WaypointList extends EntryListWidget<AnchorWidget> {

    private static double savedScrollAmount;

    public WaypointList(MinecraftClient minecraftClient, int width, int height, int y, int itemHeight) {
        super(minecraftClient, width, height, y, itemHeight);
    }

    public void restoreScroll() {
        setScrollY(savedScrollAmount);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        this.setFocused(null);
        return super.mouseClicked(click, doubled);
    }

    @Override
    public int addEntry(AnchorWidget entry) {
        return super.addEntry(entry);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    protected void drawSelectionHighlight(DrawContext context, AnchorWidget entry, int color) {
        //do nothing
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        savedScrollAmount = getScrollY();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}