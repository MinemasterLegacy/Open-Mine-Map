package net.mmly.openminemap.gui;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2fStack;

import java.awt.image.BufferedImage;
import java.time.Clock;

public class DirectionIndicator extends ClickableWidget {

    private static final Identifier textureId = Identifier.of("openminemap", "rotatabledirectionindicator.png");
    private static final Identifier playerOnlyTextureId = Identifier.of("openminemap", "rotatabledirectionedplayer.png");
    BufferedImage baseTexture;
    public boolean loadSuccess;

    static Clock clock = Clock.systemUTC();
    static String instant;
    static double before;
    static double now;

    public DirectionIndicator(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    public static void draw(DrawContext context, double rotation, int x, int y, boolean indicatorOnly) {
        int x1 = x;
        int y1 = y;
        int x2 = x + 24;
        int y2 = y + 24;

        int z = 0;
        float v1 = 0 + 0.0F / 24;
        float v2 = 0 + 1.0F;
        float u1 = 0 + 0.0F / 24;
        float u2 = 0 + 1.0F;

        float width = 24;
        float height = 24;

        Matrix3x2fStack matrices = context.getMatrices();

        matrices.pushMatrix();
        matrices.rotateAbout((float) Math.toRadians(rotation), x1 + width / 2, y1 + height / 2);

        context.drawTexture(RenderPipelines.GUI_TEXTURED,  indicatorOnly ? playerOnlyTextureId : textureId, x1, y1, u1, v1, 24, 24, 24, 24);

        matrices.popMatrix();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

}
