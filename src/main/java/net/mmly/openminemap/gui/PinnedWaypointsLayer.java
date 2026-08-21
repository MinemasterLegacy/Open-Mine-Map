package net.mmly.openminemap.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.Waypoint;

import java.awt.*;

public class PinnedWaypointsLayer extends AbstractWidget {

    int waypointRenderSize; // how big the waypoints look
    int waypointHitboxSize;
    int visibleWaypointCount = 0;
    int maxHeight;
    int margin;
    private static Waypoint[] pinnedWaypoints;
    int mouseX = 0;
    int mouseY = 0;
    Font textRenderer;
    public static int menuSelection = -1;
    private static int MARGIN = 2;

    private int scrollOffset = 0;

    public PinnedWaypointsLayer(int x, int y, int width, int margin, Font renderer) {
        super(x, y, width, width, Component.nullToEmpty(""));

        waypointRenderSize = Math.max(0, width - (margin * 2));
        maxHeight = width;
        waypointHitboxSize = width;
        this.margin = margin;
        this.textRenderer = renderer;
    }

    public void setRoundedHeight(int availableHeight) {
        maxHeight = Math.max(20, availableHeight - (availableHeight % waypointHitboxSize));
        visibleWaypointCount = Math.max(1, availableHeight / waypointHitboxSize);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount > 0) {
            scrollOffset--;
        }
        if (verticalAmount < 0) {
            scrollOffset++;
        }
        clampScroll();

        return false;
    }

    public void clampScroll() {
        scrollOffset = Math.clamp(scrollOffset, 0, Math.max(0, pinnedWaypoints.length - visibleWaypointCount));
    }

    public void drawWidget(GuiGraphicsExtractor context) {

        if (!visible) return;
        setHeight(Math.min(maxHeight, pinnedWaypoints.length * width));

        context.fill(getX(), getY(), getX() + width, getY() + height, MapScreen.backingColor);

        if (MapScreen.getRightClickMenuLocation() == null) menuSelection = -1;
        if (menuSelection > -1) UContext.drawBorder(getX() + margin - 1, getY() + margin - 1 + ((menuSelection - scrollOffset) * waypointHitboxSize), waypointRenderSize + 2, waypointRenderSize + 2, 0xFFFFFCA8);

        if (isHovered()) {
            int selectedPosition = ((mouseY - getY()) / waypointHitboxSize);
            int selectedWaypointId = selectedPosition + scrollOffset;
            UContext.drawBorder(getX() + margin - 1, getY() + margin - 1 + (selectedPosition * waypointHitboxSize), waypointRenderSize + 2, waypointRenderSize + 2, 0xFFFFFFFF);
            if (RightClickMenu.getDisplayType() == RightClickMenuType.HIDDEN) {
                UContext.fillZone(getX() + width + 3, getY() + (selectedPosition * waypointHitboxSize) + (waypointHitboxSize / 2) - (textRenderer.lineHeight / 2) - 2, textRenderer.width(pinnedWaypoints[selectedWaypointId].name) + 3, textRenderer.lineHeight + 3, MapScreen.backingColor);
                context.text(textRenderer, pinnedWaypoints[selectedWaypointId].name, getX() + width + 5, getY() + (selectedPosition * waypointHitboxSize) + (waypointHitboxSize / 2) - (textRenderer.lineHeight / 2), RGBof(pinnedWaypoints[selectedWaypointId].color), true);
            }
        }

        clampScroll();
        for (int i = 0; i < Math.min(pinnedWaypoints.length, visibleWaypointCount); i++) {
            context.blit(RenderPipelines.GUI_TEXTURED, pinnedWaypoints[i+scrollOffset].identifier, getX() + margin, getY() + (i * waypointHitboxSize) + margin, 0, 0, waypointRenderSize, waypointRenderSize, waypointRenderSize, waypointRenderSize);
        }

        if (visibleWaypointCount < pinnedWaypoints.length) {
            if (scrollOffset != 0) UContext.drawDottedHorizontalLine(getX(), getWidth(), getY(), 0xFFFFFFFF);
            if (scrollOffset != pinnedWaypoints.length - visibleWaypointCount) UContext.drawDottedHorizontalLine(getX(), getRight(), getBottom() - 1, 0xFFFFFFFF);
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

    public Waypoint getSelectedWaypoint() {
        return pinnedWaypoints[((((int) mouseY) - getY()) / waypointHitboxSize) + scrollOffset];
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        int selection = ((((int) mouseY) - getY()) / waypointHitboxSize) + scrollOffset;
        if (RightClickMenu.getDisplayType() == RightClickMenuType.PINNED_WAYPOINT && MapScreen.getRightClickMenuLocation().name.equals(pinnedWaypoints[selection].name)) {
            RightClickMenu.disableMenu();
        } else {
            RightClickMenu.enableMenu(
                    RightClickMenuType.PINNED_WAYPOINT,
                    getX() + width + MARGIN,
                    /*getY() + (selection * waypointHitboxSize) + ((double) waypointHitboxSize / 2) - ((double) textRenderer.fontHeight / 2) - 3,*/
                    getY() + (selection - scrollOffset) * waypointHitboxSize,
                    getSelectedWaypoint()
            );
            menuSelection = selection;


            RightClickMenu.instance.setY(RightClickMenu.instance.getY() - Math.max(0, RightClickMenu.instance.getBottom() - getBottom()));
            RightClickMenu.getInstance().setY(Math.max(MARGIN, RightClickMenu.getInstance().getY()));
        }
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo input) {
        return input.button() == 0 || input.button() == 1;
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
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        if (this.isHovered()) context.requestCursor(CursorTypes.POINTING_HAND);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {
    }

}
