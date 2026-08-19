package net.mmly.openminemap.waypoint;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.gui.AnchorWidget;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.gui.RightClickMenu;
import net.mmly.openminemap.gui.RightClickMenuType;
import net.mmly.openminemap.util.Waypoint;
import net.mmly.openminemap.util.WaypointFile;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.time.Duration;

public class WaypointEntryWidget extends AbstractWidget {

    Waypoint waypoint;
    Font renderer;
    public static int scrollOffset;

    private static final Identifier editId = Identifier.fromNamespaceAndPath("openminemap", "waypoints/gui/edit.png");
    private static final Identifier pinOnId = Identifier.fromNamespaceAndPath("openminemap", "waypoints/gui/pinon.png");
    private static final Identifier pinOffId = Identifier.fromNamespaceAndPath("openminemap", "waypoints/gui/pinoff.png");
    private static final Identifier viewOnId = Identifier.fromNamespaceAndPath("openminemap", "waypoints/gui/viewon.png");
    private static final Identifier viewOffId = Identifier.fromNamespaceAndPath("openminemap", "waypoints/gui/viewoff.png");

    private static final int selectedColor = 0xFFFFFFFF;
    private static final int idleColor = 0xFF808080;
    private static final int hoverColor = 0xFFB0B0B0;
    private static final int editingColor = 0xFFFFFCA8;

    private boolean visibleWaypoint;
    private boolean pinnedWaypoint;
    private Selection selection = Selection.NONE;

    private int mx = 0;
    private int my = 0;

    private AnchorWidget anchor;
    private int lastCheckedButton = 0;

    private static final Component[] tooltipMessages = new Component[] {
            Component.translatable("omm.waypoints.button.edit"),
            Component.translatable("omm.waypoints.button.view"),
            Component.translatable("omm.waypoints.button.pin"),
    };

    public static void setScroll(int scroll) {
        scrollOffset = scroll;
    }

    public WaypointEntryWidget(Component message, Waypoint waypoint, Font textRenderer, boolean pinned, boolean visible) {
        super(10, 0, 0, 20, message);
        this.waypoint = waypoint;
        this.renderer = textRenderer;
        this.visibleWaypoint = visible;
        this.pinnedWaypoint = pinned;
    }

    private void setPinned(boolean pinned) {
        if (WaypointFile.setWaypointPinned(waypoint.name, pinned)) {
            this.pinnedWaypoint = pinned;
        } else {
            OpenMineMapClient.debugMessages.add("OpenMineMap: Waypoint property change failed");
        }
    }

    private void setVisible(boolean visible) {
        if (WaypointFile.setWaypointVisibility(waypoint.name, visible)) {
            this.visibleWaypoint = visible;
        } else {
            OpenMineMapClient.debugMessages.add("OpenMineMap: Waypoint property change failed");
        }
    }

    private void editThisWaypoint() {
        WaypointScreen.getInstance().enableEditMode(waypoint);
    }

    public void setAnchor(AnchorWidget anchor) {
        this.anchor = anchor;
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo input) {
        this.lastCheckedButton = input.button();
        return input.button() == 0 || this.lastCheckedButton == 1;
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {

        if (!anchor.drawNow) return;

        mx = mouseX;
        my = mouseY;

        setX(anchor.getX());
        setY(anchor.getY());
        setWidth(anchor.getWidth());

        int borderColor = WaypointScreen.instance.editingWaypointName.equals(waypoint.name) ? editingColor : (isFocused() ? selectedColor : (isHovered() ? hoverColor : idleColor));

        context.blit(RenderPipelines.GUI_TEXTURED, waypoint.identifier, getX() + 3, getY() + 3, 0, 0, 14, 14, 14, 14);

        int xMod = 0;
        selection = Selection.NONE;
        if (mouseX < getRight() - 51) {
            setTooltip(Tooltip.create(Component.nullToEmpty(waypoint.name)));
            setTooltipDelay(Duration.ofMillis(1000));
        }
        for (Identifier i : new Identifier[]{editId, visibleWaypoint ? viewOnId : viewOffId, pinnedWaypoint ? pinOnId : pinOffId}) {
            context.blit(RenderPipelines.GUI_TEXTURED, i, getX() + width - 17 - (xMod * 16), getY() + 3, 0, 0, 14, 14, 14, 14);
            if (mouseIsInArea(getX() + width - 17 - (xMod * 16), getY() + 3, 14, 14)) {
                setTooltip(Tooltip.create(tooltipMessages[xMod]));
                setTooltipDelay(Duration.ZERO);
                selection = Selection.getById(xMod + 1);
                if (this.isHovered()) context.requestCursor(CursorTypes.POINTING_HAND);

            }
            xMod++;
        }

        if (!(
                (getY() >= RightClickMenu.instance.getY() && getY() < RightClickMenu.instance.getBottom()) ||
                (getBottom() < RightClickMenu.instance.getBottom() && getY() >= RightClickMenu.instance.getY())
            )) {
            context.enableScissor(0, 0, getX() + width - 52, Minecraft.getInstance().getWindow().getGuiScaledHeight());
            context.drawString(renderer, WaypointScreen.instance.editingWaypointName.equals(waypoint.name) ? Component.translatable("omm.waypoints.editing").withStyle(ChatFormatting.BOLD) : Component.literal(waypoint.name), getX() + 23, getY() + (height / 2) - (renderer.lineHeight / 2), 0xFFFFFFFF, true);
            context.disableScissor();
        }

        UContext.drawBorder(getX(), getY(), getWidth(), getHeight(), borderColor);
        context.vLine(getX() + width - 52, getY(), getY() + height, borderColor);
        context.vLine(getX() + 19, getY(), getY() + height, borderColor);

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {

        if (lastCheckedButton == 1) {
            RightClickMenu.enableMenu(
                    RightClickMenuType.SCREEN_WAYPOINT,
                    getX(),
                    getY() + height,
                    this.waypoint
            );
            return;
        }

        if (selection == Selection.VIEW) {
            setVisible(!visibleWaypoint);
        }
        if (selection == Selection.PIN) {
            setPinned(!pinnedWaypoint);
        }
        if (selection == Selection.EDIT) {
            if (WaypointScreen.instance.editingWaypointName.equals(waypoint.name)) {
                WaypointScreen.instance.exitEditMode();
            } else {
                editThisWaypoint();
            }

        }
    }

    private boolean mouseIsInArea(int x, int y, int width, int height) {
        return mx >= x && my >= y && mx <= x + width && my <= y + height;
    }

}

enum Selection {
    NONE,
    EDIT,
    VIEW,
    PIN;

    public static Selection getById(int i) {
        return switch (i) {
            case 1 -> EDIT;
            case 2 -> VIEW;
            case 3 -> PIN;
            default -> NONE;
        };
    }
}