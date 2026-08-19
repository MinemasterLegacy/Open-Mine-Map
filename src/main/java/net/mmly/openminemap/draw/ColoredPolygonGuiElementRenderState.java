package net.mmly.openminemap.draw;


import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

import static net.mmly.openminemap.draw.UContext.sortTriangleToDrawOrder;

public record ColoredPolygonGuiElementRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose, int[][][] polygon, int x0, int y0, int x1, int y1, int color, @Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds) implements GuiElementRenderState {
    public ColoredPolygonGuiElementRenderState(Matrix3x2f pose, int[][][] polygon, int x0, int y0, int x1, int y1, int color, @Nullable ScreenRectangle scissorArea) {
        this(RenderPipelines.GUI, TextureSetup.noTexture(), pose, polygon, x0, y0, x1, y1, color, scissorArea, createBounds(pose, scissorArea));
    }

    public void buildVertices(VertexConsumer vertices) {
        boolean writtenVertices = false;
        for (int[][] triangle : polygon()) {
            triangle = sortTriangleToDrawOrder(triangle);
            if (triangle == null) continue;
            writtenVertices = true;

            vertices.addVertexWith2DPose(this.pose(), (float) triangle[0][0], (float) triangle[0][1]).setColor(this.color());
            vertices.addVertexWith2DPose(this.pose(), (float) triangle[1][0], (float) triangle[1][1]).setColor(this.color());
            vertices.addVertexWith2DPose(this.pose(), (float) triangle[2][0], (float) triangle[2][1]).setColor(this.color());
            vertices.addVertexWith2DPose(this.pose(), (float) triangle[1][0], (float) triangle[1][1]).setColor(this.color());
        }

        //if i don't write anything at this step it crashes (but only on a server for some f***ing reason) so i have to do this
        if (!writtenVertices) {
            for (int i = 0; i < 4; i++) {
                vertices.addVertexWith2DPose(this.pose(), 0, 0).setColor(this.color());
            }
        }

    }

    @Nullable
    private static ScreenRectangle createBounds(Matrix3x2f pose, @Nullable ScreenRectangle scissorArea) {
        ScreenRectangle screenRect = (new ScreenRectangle(0, 0, 0x7FFFFFFF, 0x7FFFFFFF)).transformMaxBounds(pose);
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