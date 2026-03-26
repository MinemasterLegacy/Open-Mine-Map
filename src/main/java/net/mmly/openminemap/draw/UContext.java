package net.mmly.openminemap.draw;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.TreeMap;

public class UContext { //UniversalContext ; makes it easier to update draw methods per-version and allows for adding custom ones ; also eliminates the need to pass a context with draw methods

    static DrawContext drawContext;
    static TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    public static VertexConsumerProvider.Immediate capturedVertexProvider;

    public static void setContext(DrawContext context) {
        drawContext = context;
    }

    public static DrawContext getContext() {
        return drawContext;
    }

    public static void setTextRenderer(TextRenderer renderer) {
        textRenderer = renderer;
    }

    // x value defines different points depending on justification:
    //  - Left: defines leftmost edge
    //  - Right: defines rightmost edge
    //  - Center: defines center
    public static void drawJustifiedText(Text text, Justify justify, int x, int y, int color, boolean shadow) {
        switch (justify) {
            case LEFT: {
                drawContext.drawText(textRenderer, text, x, y, color, shadow);
                break;
            }
            case RIGHT: {
                drawContext.drawText(textRenderer, text, x - textRenderer.getWidth(text), y, color, shadow);
                break;
            }
            case CENTER: {
                drawContext.drawText(textRenderer, text, x - (textRenderer.getWidth(text) / 2), y, color, shadow);
                break;
            }
        }
    }
    public static void drawJustifiedText(MutableText text, Justify justify, int x, int y, int color) {
        drawJustifiedText(text, justify, x, y, color, false);
    }

    public static void drawBorder(int x, int y, int width, int height, int color) {
        drawContext.drawBorder(x, y, width, height, color);
    }
    public static void drawBorderZone(int x, int y, int x2, int y2, int color) {
        drawBorder(x, y, x2 - x, y2 - y, color);
    }

    public static void fillZone(int x, int y, int width, int height, int color) {
        drawContext.fill(x, y, x + width, y + height, color);
    }

    public static void square(int x, int y, int radius, int color) {
        drawContext.fill(x - radius, y - radius, x + radius, y + radius, color);
    }

    public static void fillAndDrawText(Text text, int x, int y, int marginWidth, int marginHeight, int fillColor, int textColor, boolean shadow) {
        fillZone(x, y, (marginWidth * 2) + textRenderer.getWidth(text), (marginHeight * 2) + textRenderer.fontHeight, fillColor);
        drawJustifiedText(text, Justify.LEFT, x + marginWidth, y + marginHeight, textColor, shadow);
    }

    public static void fillAndDrawText(Text text, int x, int y, int marginWidth, int marginHeight, int fillColor, int textColor) {
        fillAndDrawText(text, x, y, marginWidth, marginHeight, fillColor, textColor, false);
    }

    public static void drawTexture(Identifier identifier, int x, int y, int width, int height, int textureWidth, int textureHeight) {
        drawTexture(identifier, x, y, width, height, 0, 0, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    public static void drawTexture(Identifier identifier, int x, int y, int width, int height, float u, float v, int textureWidth, int textureHeight) {
        drawTexture(identifier, x, y, width, height, u, v, textureWidth, textureHeight, textureWidth, textureHeight);
    }

    public static void drawTexture(Identifier identifier, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        drawContext.drawTexture(RenderLayer::getGuiTextured, identifier, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    public static void drawTriangle(int[][] triangle, int fillColor) {
        RenderSystem.disableCull();

        triangle = sortTriangleToDrawOrder(triangle);
        if (triangle == null) return;

        Matrix4f matrix4f = drawContext.getMatrices().peek().getPositionMatrix();

        VertexConsumer vertexConsumer = capturedVertexProvider.getBuffer(RenderLayer.getGui());
        //System.out.println(Arrays.deepToString(triangle));

        for (int i = 2; i >= 0; i--) {
            vertexConsumer.vertex(matrix4f, triangle[i][0], triangle[i][1], 0).color(fillColor);
        }
        vertexConsumer.vertex(matrix4f, triangle[0][0], triangle[0][1], 0).color(fillColor);

    }

    public static int[][] sortTriangleToDrawOrder(int[][] triangle) {
        //System.out.println(Arrays.deepToString(triangle));
        double[] center = getCircumcenter(triangle);
        TreeMap<Double, int[]> points = new TreeMap<>();
        for (int i = 0; i < 3; i++) {
            points.put(
                    Math.atan2(triangle[i][1] - center[1], triangle[i][0] - center[0]),
                    triangle[i]
            );
        }
        int[][] pointsArray = points.values().toArray(new int[0][]);
        if (pointsArray.length != 3) return null;
        int[][] sortedTriangle = new int[3][2];
        for (int i = 0; i < pointsArray.length; i++) {
            sortedTriangle[i][0] = pointsArray[i][0];
            sortedTriangle[i][1] = pointsArray[i][1];
        }
        return sortedTriangle;
    }

    private static double[] getCircumcenter(int[][] triangle) {
        int ax = triangle[0][0];
        int ay = triangle[0][1];
        int bx = triangle[1][0];
        int by = triangle[1][1];
        int cx = triangle[2][0];
        int cy = triangle[2][1];
        double d = 2 * (ax * (by - cy) + bx * (cy - ay) + cx * (ay - by));
        double ux = ((ax * ax + ay * ay) * (by - cy) + (bx * bx + by * by) * (cy - ay) + (cx * cx + cy * cy) * (ay - by)) / d;
        double uy = ((ax * ax + ay * ay) * (cx - bx) + (bx * bx + by * by) * (ax - cx) + (cx * cx + cy * cy) * (bx - ax)) / d;
        return new double[] {ux, uy};
    }

    private static void rotateRad(MatrixStack matrixStack, float radians) {
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotation(radians));
    }

    private static void rotateDeg(MatrixStack matrixStack, float degrees) {
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(degrees));
    }

    public static void drawDiagonalLine(int[] start, int[] end, float thickness, int color) {

        MatrixStack matrixStack = drawContext.getMatrices();
        matrixStack.push();

        double boundsX = start[0] - end[0];
        double boundsY = start[1] - end[1];
        float length = (float) Math.sqrt(boundsX * boundsX + boundsY * boundsY);

        matrixStack.translate(
                (float) (start[0] + end[0]) / 2,
                (float) (start[1] + end[1]) / 2,
                0
        );
        rotateRad(matrixStack, (float) Math.atan2(boundsY, boundsX));
        matrixStack.scale(length / 2, thickness / 2, 1);

        drawContext.fill(-1, -1, 1, 1, color);
        matrixStack.pop();

    }

}
