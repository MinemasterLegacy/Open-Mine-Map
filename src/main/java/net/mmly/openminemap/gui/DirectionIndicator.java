package net.mmly.openminemap.gui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
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
import java.util.function.Function;

public class DirectionIndicator extends ClickableWidget {

    public static Identifier textureId = Identifier.of("openminemap", "rotatabledirectionindicator.png");
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

    protected void render(DrawContext context) {
        //if (updateDynamicTexture() || !loadSuccess) return;
        //context.drawTexture(textureId, FullscreenMapScreen.playerMapX - 8, FullscreenMapScreen.playerMapY - 8, 0, 0, 24, 24, 24, 24);
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

    //private static final RenderPipelines

    private static final RenderPipeline DIRECTION_INDICATOR = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET)
            .withLocation(Identifier.of("openminemap", "pipeline/direction_indicator"))
            .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR, VertexFormat.DrawMode.QUADS)
            .withCull(false)
            .build()
    );

    private static final RenderLayer renderLayer = RenderLayer.of(
            "direction_indicator",
            1536,
            DIRECTION_INDICATOR,
            RenderLayer.MultiPhaseParameters.builder().build(false)
    );

    public static void draw(Function<Identifier, RenderLayer> renderLayers, DrawContext context, double rotation, int x, int y) {
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

        //context.drawTexture(renderLayers, textureId, 0, 0, 0, 0, 20, 20, 142, 142);

        renderLayers.apply(textureId);

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        //matrices.scale(1F, 1F, 1F);
        //matrices.translate(x1, 0F, 0F);
        matrices.translate(x + width / 2, y + height / 2, 0F);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) rotation));

        Matrix4f matrix4f = context.getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        bufferBuilder.vertex(matrix4f, (float) -width / 2, (float) -height / 2, z).texture(u1, v1).color(-1);
        bufferBuilder.vertex(matrix4f, (float) -width / 2, (float) height / 2, z).texture(u1, v2).color(-1);
        bufferBuilder.vertex(matrix4f, (float) width / 2, (float) height / 2, z).texture(u2, v2).color(-1);
        bufferBuilder.vertex(matrix4f, (float) width / 2, (float) -height / 2, z).texture(u2, v1).color(-1);



        //RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX);
        //RenderSystem.setShaderTexture(0, textureId);

        RenderSystem.setTextureMatrix();

        //BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        renderLayer.draw(bufferBuilder.end());

        matrices.pop();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

}
