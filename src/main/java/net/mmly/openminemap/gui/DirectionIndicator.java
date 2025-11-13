package net.mmly.openminemap.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayerAttributes;
import org.joml.Matrix4f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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
        getTextureFromResources();
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    private void getTextureFromResources() {
        try {
            InputStream stream = MinecraftClient.getInstance().getResourceManager().getResource(textureId).get().getInputStream();
            baseTexture = ImageIO.read(stream);
            loadSuccess = true;
        } catch (IOException e) {
            loadSuccess = false;
        }
    }

    public static void draw(DrawContext context, double rotation, int x, int y, boolean hudCrop, boolean indicatorOnly) {
        int x1 = x;
        int y1 = y;
        int x2 = x + 24;
        int y2 = y + 24;

        if (hudCrop && (x <= HudMap.hudMapX - 16 || y1 <= HudMap.hudMapY - 16 || x2 >= HudMap.hudMapX2 + 16 || y2 >= HudMap.hudMapY2 + 16)) {
            return;
        }

        int z = 0;
        float v1 = 0 + 0.0F / 24;
        float v2 = 0 + 1.0F;
        float u1 = 0 + 0.0F / 24;
        float u2 = 0 + 1.0F;

        float width = 24;
        float height = 24;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        //matrices.scale(1F, 1F, 1F);
        //matrices.translate(x1, 0F, 0F);
        matrices.translate(x + width / 2, y + height / 2, 0F);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) rotation));

        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        /*
        bufferBuilder.vertex(matrix4f, (float)x1, (float)y1, (float)z).texture(u1, v1);
        bufferBuilder.vertex(matrix4f, (float)x1, (float)y2, (float)z).texture(u1, v2);
        bufferBuilder.vertex(matrix4f, (float)x2, (float)y2, (float)z).texture(u2, v2);
        bufferBuilder.vertex(matrix4f, (float)x2, (float)y1, (float)z).texture(u2, v1);
         */

        bufferBuilder.vertex(matrix4f, (float) -width / 2, (float) -height / 2, z).texture(u1, v1);
        bufferBuilder.vertex(matrix4f, (float) -width / 2, (float) height / 2, z).texture(u1, v2);
        bufferBuilder.vertex(matrix4f, (float) width / 2, (float) height / 2, z).texture(u2, v2);
        bufferBuilder.vertex(matrix4f, (float) width / 2, (float) -height / 2, z).texture(u2, v1);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, indicatorOnly ? playerOnlyTextureId :textureId);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrices.pop();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

}
