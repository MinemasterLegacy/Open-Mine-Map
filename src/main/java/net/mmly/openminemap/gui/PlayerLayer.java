package net.mmly.openminemap.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.Text;

public class PlayerLayer extends ClickableWidget {

    public PlayerLayer(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
       // context.fill(getX(), getY(), getX() + 8, getY() + 8, 0xFF000000);
        //currently disabling player clicking, may be re-enabled later for right click menu-ing players
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        System.out.println("yea");
    }

    @Override
    public void playDownSound(SoundManager soundManager) {}

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
}
