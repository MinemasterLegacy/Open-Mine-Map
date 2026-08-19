package net.mmly.openminemap.waypoint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.mmly.openminemap.gui.AnchorWidget;

public class WaypointList extends AbstractSelectionList<AnchorWidget> {

    private static double savedScrollAmount;

    public WaypointList(Minecraft minecraftClient, int width, int height, int y, int itemHeight) {
        super(minecraftClient, width, height, y, itemHeight);
    }

    public void restoreScroll() {
        setScrollAmount(savedScrollAmount);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        this.setFocused(null);
        return super.mouseClicked(click, doubled);
    }

    @Override
    public int addEntry(AnchorWidget entry) {
        return super.addEntry(entry);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    @Override
    protected void renderSelection(GuiGraphics guiGraphics, AnchorWidget entry, int i) {
        //do nothing
    }
    /*
    @Override
    protected void drawSelectionHighlight(GuiGraphics context, AnchorWidget entry, int color) {
        //do nothing
    }*/

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        savedScrollAmount = scrollAmount();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}