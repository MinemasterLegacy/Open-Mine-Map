package net.mmly.openminemap.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.TexturedQuadGuiElementRenderState;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayerAttributes;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
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

    public static void draw(RenderPipeline pipeline, DrawContext context, double rotation, int x, int y, boolean hudCrop, boolean indicatorOnly) {
        int x1 = x;
        int y1 = y;
        int x2 = x + 24;
        int y2 = y + 24;

        if (hudCrop && (x <= HudMap.hudMapX - 16 || y <= HudMap.hudMapY - 16 || x2 >= HudMap.hudMapX2 + 16 || y2 >= HudMap.hudMapY2 + 16)) {
            return;
        }

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

        context.drawTexture(pipeline, indicatorOnly ? playerOnlyTextureId : textureId, x1, y1, u1, v1, 24, 24, 24, 24);

        matrices.popMatrix();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

}
