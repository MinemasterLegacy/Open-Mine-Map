package net.mmly.openminemap.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;
import net.mmly.openminemap.config.MapConfigScreen;
import net.mmly.openminemap.hud.HudMap;

public class InteractionLayer extends ClickableWidget {
    public InteractionLayer(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }

    double subDeltaX;
    double subDeltaY;

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {/* Play no sound */}

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount > 0) {
            FullscreenMapScreen.zoomIn();
        } else {
            FullscreenMapScreen.zoomOut();
        }
        return false;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    /*
    @Override
    public void onRelease(double mouseX, double mouseY) {
        FullscreenMapScreen.lastMouseDown = false;
        System.out.println("release");
    }
    */

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        System.out.println("set true");
        FullscreenMapScreen.disableRightClickMenu();
        FullscreenMapScreen.lastMouseDown = true;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        System.out.println("click");
        /*
        if (button == 0) {
            //FullscreenMapScreen.lastMouseDown = true;
            FullscreenMapScreen.disableRightClickMenu();
        }
        */
        if (button == 1) {
            //System.out.println("got it");
            if (FullscreenMapScreen.mouseDisplayLong.length() > 3) {
                FullscreenMapScreen.enableRightClickMenu(mouseX, mouseY);
            } else {
                FullscreenMapScreen.disableRightClickMenu();
            }
        }
        //FullscreenMapScreen.doFollowPlayer = false;
        return true;
    }

}