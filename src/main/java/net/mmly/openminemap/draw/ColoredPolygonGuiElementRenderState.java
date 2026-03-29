package net.mmly.openminemap.draw;


import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import static net.mmly.openminemap.draw.UContext.sortTriangleToDrawOrder;

@Environment(EnvType.CLIENT)
public record ColoredPolygonGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int[][][] polygon, int x0, int y0, int x1, int y1, int color, @Nullable ScreenRect scissorArea, @Nullable ScreenRect bounds) implements SimpleGuiElementRenderState {
    public ColoredPolygonGuiElementRenderState(Matrix3x2f pose, int[][][] polygon, int x0, int y0, int x1, int y1, int color, @Nullable ScreenRect scissorArea) {
        this(RenderPipelines.GUI, TextureSetup.empty(), pose, polygon, x0, y0, x1, y1, color, scissorArea, createBounds(polygon, pose, scissorArea));
    }

    public void setupVertices(VertexConsumer vertices, float depth) {
        for (int[][] triangle : polygon()) {
            triangle = sortTriangleToDrawOrder(triangle);
            if (triangle == null) continue;

            vertices.vertex(this.pose(), (float) triangle[0][0], (float) triangle[0][1], depth).color(this.color());
            vertices.vertex(this.pose(), (float) triangle[1][0], (float) triangle[1][1], depth).color(this.color());
            vertices.vertex(this.pose(), (float) triangle[2][0], (float) triangle[2][1], depth).color(this.color());
            vertices.vertex(this.pose(), (float) triangle[1][0], (float) triangle[1][1], depth).color(this.color());

        }

    }

    @Nullable
    private static ScreenRect createBounds(int[][][] polygon, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        //int x0 = getMin(polygon, 0);
        //int x1 = getMax(polygon, 0);
        //int y0 = getMin(polygon, 1);
        //int y1 = getMax(polygon, 1);

        ScreenRect screenRect = (new ScreenRect(0, 0, 0x7FFFFFFF, 0x7FFFFFFF)).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
/*
    @Nullable
    private static ScreenRect createBounds(int[][][] polygon, Matrix3x2f pose, @Nullable ScreenRect scissorArea) {
        int x0 = getMin(polygon, 0);
        int x1 = getMax(polygon, 0);
        int y0 = getMin(polygon, 1);
        int y1 = getMax(polygon, 1);

        ScreenRect screenRect = (new ScreenRect(x0, y0, x1 - x0, y1 - y0)).transformEachVertex(pose);
        return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
    }
*/
    private static int getMin(int[][][] polygon, int xOrY) {
        int polygonMin = 0x7FFFFFFF;
        for (int[][] triangle : polygon) {
            polygonMin = Math.min(polygonMin, Math.min(triangle[0][xOrY], Math.min(triangle[1][xOrY], triangle[2][xOrY])));
        }
        return polygonMin;
    }

    private static int getMax(int[][][] polygon, int xOrY) {
        int polygonMax = 0xFFFFFFFF;
        for (int[][] triangle : polygon) {
            polygonMax = Math.max(polygonMax, Math.max(triangle[0][xOrY], Math.max(triangle[1][xOrY], triangle[2][xOrY])));
        }
        return polygonMax;
    }


}