package net.mmly.openminemap.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.mmly.openminemap.gui.AnchorWidget;

public class ConfigList extends AbstractSelectionList<AnchorWidget> {

    private static double savedScrollAmount;

    public ConfigList(Minecraft minecraftClient, int width, int height, int y, int itemHeight) {
        super(minecraftClient, width, height, y, itemHeight);
    }

    public void restoreScroll() {
        setScrollAmount(savedScrollAmount);
    }

    @Override
    public int addEntry(AnchorWidget entry) {
        return super.addEntry(entry);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    @Override
    protected void extractSelection(GuiGraphicsExtractor guiGraphics, AnchorWidget entry, int i) {
        //do nothing
    }
    /*
    @Override
    protected void drawSelectionHighlight(GuiGraphics context, AnchorWidget entry, int color) {
        //do nothing
    }*/

    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractWidgetRenderState(context, mouseX, mouseY, delta);
        savedScrollAmount = scrollAmount();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
