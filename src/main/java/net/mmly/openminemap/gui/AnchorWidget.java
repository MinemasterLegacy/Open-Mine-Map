package net.mmly.openminemap.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class AnchorWidget extends ObjectSelectionList.Entry<AnchorWidget> {

    public AbstractWidget widget;
    public boolean drawNow = false;

    @Override
    public Component getNarration() {
        return Component.nullToEmpty("");
    }

    public void setWidget(AbstractWidget widget) {
        this.widget = widget;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        return widget.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
        return widget.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        return widget.mouseReleased(click);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        return widget.charTyped(input);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        return widget.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyEvent input) {
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
    public void extractContent(GuiGraphicsExtractor context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        drawNow = true;
        widget.extractRenderState(context, mouseX, mouseY, deltaTicks);
        drawNow = false;
        //context.fill(getX(), getY(), getX() + getWidth(), getX() + getHeight(), 0x80FF0000);
    }
}
