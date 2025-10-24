package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class DirectionLayer extends ClickableWidget {

    Identifier baseTexture = Identifier.of("openminemap", "rotatabledirectionindicator.png");

    public DirectionLayer(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {}

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}


}
