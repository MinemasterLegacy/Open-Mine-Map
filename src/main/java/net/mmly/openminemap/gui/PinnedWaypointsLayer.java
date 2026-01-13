package net.mmly.openminemap.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.Waypoint;

import java.awt.*;

public class PinnedWaypointsLayer extends ClickableWidget {

    int waypointRenderSize; // how big the waypoints look
    int waypointHitboxSize;
    int visibleWaypointCount = 0;
    int maxHeight;
    int margin;
    private static Waypoint[] pinnedWaypoints;
    int mouseX = 0;
    int mouseY = 0;
    TextRenderer textRenderer;
    public static int menuSelection = -1;

    public PinnedWaypointsLayer(int x, int y, int width, int margin, TextRenderer renderer) {
        super(x, y, width, width, Text.of(""));

        waypointRenderSize = Math.max(0, width - (margin * 2));
        maxHeight = width;
        waypointHitboxSize = width;
        this.margin = margin;
        this.textRenderer = renderer;
    }

    public void setRoundedHeight(int height1) {
        maxHeight = height1 - (height1 % waypointHitboxSize);
        visibleWaypointCount = height1 / waypointHitboxSize;
    }

    private static void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        //temporary method for 21.9. Can be replaced with context.drawBorder(...) in 1.21.8-, and context.submitOutline(...) in 1.21.10+
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y + 1, x + 1, y + height - 1, color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    public void drawWidget(DrawContext context) {

        //if (!visible) return;
        setHeight(Math.min(maxHeight, pinnedWaypoints.length * width));

        context.fill(getX(), getY(), getX() + width, getY() + height, 0x88000000);

        if (FullscreenMapScreen.getRightClickMenuWaypoint() == null) menuSelection = -1;
        if (menuSelection > -1 ) drawBorder(context,getX() + margin - 1, getY() + margin - 1 + (menuSelection * waypointHitboxSize), waypointRenderSize + 2, waypointRenderSize + 2, 0xFFFFFCA8);

        if (isHovered()) {
            int selection = ((mouseY - getY()) / waypointHitboxSize);
            drawBorder(context,getX() + margin - 1, getY() + margin - 1 + (selection * waypointHitboxSize), waypointRenderSize + 2, waypointRenderSize + 2, 0xFFFFFFFF);
            if (FullscreenMapScreen.getRightClickMenuType() == RightClickMenuType.HIDDEN) {
                fill(context, getX() + width + 3, getY() + (selection * waypointHitboxSize) + (waypointHitboxSize / 2) - (textRenderer.fontHeight / 2) - 2, textRenderer.getWidth(pinnedWaypoints[selection].name) + 3, textRenderer.fontHeight + 3, 0x80000000);
                context.drawText(textRenderer, pinnedWaypoints[selection].name, getX() + width + 5, getY() + (selection * waypointHitboxSize) + (waypointHitboxSize / 2) - (textRenderer.fontHeight / 2), RGBof(pinnedWaypoints[selection].color), false);
            }
        }

        int y = getY();

        for (int i = 0; i < Math.min(pinnedWaypoints.length, visibleWaypointCount); i++) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, pinnedWaypoints[i].identifier, getX() + margin, getY() + (i * waypointHitboxSize) + margin, 0, 0, waypointRenderSize, waypointRenderSize, waypointRenderSize, waypointRenderSize);
        }

        /*
        for (Waypoint waypoint : pinnedWaypoints) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, waypoint.identifier, getX() + margin, getY() + margin + y, 0, 0, waypointRenderSize, waypointRenderSize, waypointRenderSize, waypointRenderSize);
            y += waypointHitboxSize;
        }

         */
    }

    private int RGBof(int HSB) {
        return 0xFF000000 | Color.HSBtoRGB(
                (float) ((HSB >> 16) & 0xFF) / 256,
                (float) ((HSB >> 8) & 0xFF) / 256,
                (float) (HSB & 0xFF) / 256);
    }

    private void fill(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    public Waypoint getSelectedWaypoint() {
        return pinnedWaypoints[((((int) mouseY) - getY()) / waypointHitboxSize)];
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        int selection = ((((int) mouseY) - getY()) / waypointHitboxSize);
        if (FullscreenMapScreen.getRightClickMenuType() == RightClickMenuType.PINNED_WAYPOINT && FullscreenMapScreen.getRightClickMenuWaypoint().name.equals(pinnedWaypoints[selection].name)) {
            FullscreenMapScreen.disableRightClickMenu();
        } else {
            FullscreenMapScreen.enableRightClickMenu(
                    getX() + width + 3,
                    /*getY() + (selection * waypointHitboxSize) + ((double) waypointHitboxSize / 2) - ((double) textRenderer.fontHeight / 2) - 3,*/
                    getY() + 3,
                    RightClickMenuType.PINNED_WAYPOINT,
                    getSelectedWaypoint()
            );
            menuSelection = selection;

        }
    }

    public static void updatePinnedWaypoints() {
        int num = 0;
        for (Waypoint waypoint : OmmMap.getWaypoints()) {
            if (waypoint.pinned) num++;
        }

        pinnedWaypoints = new Waypoint[num];

        int i = 0;
        for (Waypoint waypoint : OmmMap.getWaypoints()) {
            if (waypoint.pinned) {
                pinnedWaypoints[i] = waypoint;
                i++;
            }
        }
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

}
