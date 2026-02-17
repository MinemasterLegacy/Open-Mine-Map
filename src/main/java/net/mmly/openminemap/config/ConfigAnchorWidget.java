package net.mmly.openminemap.config;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

public class ConfigAnchorWidget extends AlwaysSelectedEntryListWidget.Entry<ConfigAnchorWidget> {

    ClickableWidget widget;
    public boolean drawNow = false;

    @Override
    public Text getNarration() {
        return Text.of("");
    }

    public void setWidget(ClickableWidget widget) {
        this.widget = widget;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        return widget.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        return widget.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(Click click) {
        return widget.mouseReleased(click);
    }

    @Override
    public boolean charTyped(CharInput input) {
        return widget.charTyped(input);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        return widget.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyInput input) {
        return widget.keyReleased(input);
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
    public void render(DrawContext context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        drawNow = true;
        widget.render(context, mouseX, mouseY, deltaTicks);
        drawNow = false;
        //context.fill(getX(), getY(), getX() + getWidth(), getX() + getHeight(), 0x80FF0000);
    }
}
