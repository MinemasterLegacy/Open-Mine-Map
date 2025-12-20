package net.mmly.openminemap.waypoint;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.util.Waypoint;
import net.mmly.openminemap.util.WaypointFile;

public class WaypointEntryWidget extends ClickableWidget {

    Waypoint waypoint;
    TextRenderer renderer;
    public int scrollOffset;

    private static final Identifier editId = Identifier.of("openminemap", "waypoints/gui/edit.png");
    private static final Identifier pinOnId = Identifier.of("openminemap", "waypoints/gui/pinon.png");
    private static final Identifier pinOffId = Identifier.of("openminemap", "waypoints/gui/pinoff.png");
    private static final Identifier viewOnId = Identifier.of("openminemap", "waypoints/gui/viewon.png");
    private static final Identifier viewOffId = Identifier.of("openminemap", "waypoints/gui/viewoff.png");

    private static final int selectedColor = 0xFFFFFFFF;
    private static final int idleColor = 0xFF808080;
    private static final int hoverColor = 0xFFB0B0B0;
    private static final int editingColor = 0xFFFFFCA8;

    private boolean visibleWaypoint;
    private boolean pinnedWaypoint;
    private Selection selection = Selection.NONE;

    private int mx = 0;
    private int my = 0;

    private static final Text[] tooltipMessages = new Text[] {
            Text.translatable("omm.waypoints.button.edit"),
            Text.translatable("omm.waypoints.button.view"),
            Text.translatable("omm.waypoints.button.pin"),
    };

    public void setScroll(int scroll) {
        scrollOffset = scroll;
    }

    public WaypointEntryWidget(int x, int y, Text message, Waypoint waypoint, TextRenderer textRenderer, boolean pinned, boolean visible) {
        super(x, y, 0, 20, message);
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

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

        mx = mouseX;
        my = mouseY;

        setWidth(WaypointScreen.getMidPoint() - 20);

        int borderColor = WaypointScreen.instance.editingWaypointName.equals(waypoint.name) ? editingColor : (isFocused() ? selectedColor : (isHovered() ? hoverColor : idleColor));

        context.drawTexture(RenderPipelines.GUI_TEXTURED, waypoint.identifier, getX() + 3, getY() + 3 - scrollOffset, 0, 0, 14, 14, 14, 14);

        int xMod = 0;
        setTooltip(null);
        selection = Selection.NONE;
        for (Identifier i : new Identifier[]{editId, visibleWaypoint ? viewOnId : viewOffId, pinnedWaypoint ? pinOnId : pinOffId}) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, i, getX() + width - 17 - (xMod * 16), getY() + 3 - scrollOffset, 0, 0, 14, 14, 14, 14);
            if (mouseIsInArea(getX() + width - 17 - (xMod * 16), getY() + 3, 14, 14)) {
                setTooltip(Tooltip.of(tooltipMessages[xMod]));
                selection = Selection.getById(xMod + 1);
            }
            xMod++;
        }

        context.enableScissor(0, 0, getX() + width - 52, MinecraftClient.getInstance().getWindow().getScaledHeight());
        context.drawText(renderer, WaypointScreen.instance.editingWaypointName.equals(waypoint.name) ? Text.literal("(Editing...)").formatted(Formatting.BOLD) : Text.literal(waypoint.name), getX() + 23, getY() + (height / 2) - (renderer.fontHeight / 2) - scrollOffset, 0xFFFFFFFF, true);
        context.disableScissor();

        context.drawBorder(getX(), getY() - scrollOffset, getWidth(), getHeight(), borderColor);
        context.drawVerticalLine(getX() + width - 52, getY() - scrollOffset, getY() + height - scrollOffset, borderColor);
        context.drawVerticalLine(getX() + 19, getY() - scrollOffset, getY() + height - scrollOffset, borderColor);

    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public void onClick(double mouseX, double mouseY) {
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