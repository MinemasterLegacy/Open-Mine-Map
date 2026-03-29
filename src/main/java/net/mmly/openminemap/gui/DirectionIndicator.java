package net.mmly.openminemap.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.mmly.openminemap.maps.OmmMap;

import java.awt.image.BufferedImage;
import java.time.Clock;
import java.util.function.Function;

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

        int size = OmmMap.PLAYERSIZE * 3;

        int x1 = x;
        int y1 = y;
        int x2 = x + size;
        int y2 = y + size;

        int z = 0;
        float v1 = 0 + 0.0F / size;
        float v2 = 0 + 1.0F;
        float u1 = 0 + 0.0F / size;
        float u2 = 0 + 1.0F;

        float width = size;
        float height = size;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        //matrices.scale(1F, 1F, 1F);
        //matrices.translate(x1, 0F, 0F);
        matrices.translate(x + width / 2, y + height / 2, 0F);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) rotation));

        context.drawTexture(RenderLayer::getGuiTextured, indicatorOnly ? playerOnlyTextureId : textureId, -size / 2, -size / 2, u1, v1, size, size, size, size);

        matrices.pop();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

}
