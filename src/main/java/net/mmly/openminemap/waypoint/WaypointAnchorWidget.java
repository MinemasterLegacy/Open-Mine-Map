package net.mmly.openminemap.waypoint;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class WaypointAnchorWidget extends AlwaysSelectedEntryListWidget.Entry<WaypointAnchorWidget> {

    private int yStore = 0;
    private int xStore = 0;
    private int widthStore = 0;
    WaypointEntryWidget widget;
    public boolean drawNow = false;

    @Override
    public Text getNarration() {
        return Text.of("");
    }

    public int getY() {
        return yStore;
    }

    public int getX() {
        return xStore;
    }

    public int getWidth() {
        return widthStore;
    }

    public void setWidget(WaypointEntryWidget widget) {
        this.widget = widget;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return widget.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return widget.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return widget.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return widget.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return widget.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public void setFocused(boolean focused) {
        widget.setFocused(focused);
    }

    @Override
    public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        this.yStore = y;
        this.xStore = x;
        this.widthStore = entryWidth;
        drawNow = true;
        widget.render(context, mouseX, mouseY, tickDelta);
        drawNow = false;
        context.fill(x, y, x + entryWidth, y + entryHeight, 0x80FF0000);
    }
}
