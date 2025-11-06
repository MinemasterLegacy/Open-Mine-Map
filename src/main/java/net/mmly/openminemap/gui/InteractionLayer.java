package net.mmly.openminemap.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.mmly.openminemap.map.TileManager;

public class InteractionLayer extends ClickableWidget {
    public InteractionLayer(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }


    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {/* Play no sound */}

    /*
    @Override
    public void onClick(double mouseX, double mouseY) {
        FullscreenMapScreen.lastMouseDown = true;
    }
    */

    @Override
    public void onRelease(double mouseX, double mouseY) {
        FullscreenMapScreen.lastMouseDown = false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount * (TileManager.doReverseScroll ? -1 : 1) > 0) {
            //
            FullscreenMapScreen.mouseZoomIn();
        } else {
            FullscreenMapScreen.mouseZoomOut();
        }
        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { //left click
            FullscreenMapScreen.lastMouseDown = true;
            FullscreenMapScreen.disableRightClickMenu();
        }
        if (button == 1) { //right click
            //System.out.println("got it");
            if (FullscreenMapScreen.mouseDisplayLong.length() > 3) { //checks if mouse is positioned on the map (this variable will be "-.-" if it isn't)
                FullscreenMapScreen.enableRightClickMenu(mouseX, mouseY);
            } else {
                FullscreenMapScreen.disableRightClickMenu();
            }
        }
        FullscreenMapScreen.doFollowPlayer = false;
        return false;

    }
}