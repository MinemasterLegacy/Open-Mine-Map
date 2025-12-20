package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.event.KeyInputHandler;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.UnitConvert;
import net.mmly.openminemap.util.Waypoint;
import net.mmly.openminemap.util.WaypointFile;
import net.mmly.openminemap.waypoint.WaypointScreen;

import java.awt.*;
import java.util.Objects;

public class RightClickMenu extends ClickableWidget {

    // = 16 * number of menu options
    private int hoverOn = 0;
    private boolean useTp;
    public static boolean selectingSite = false;
    double clickX = 0;
    double clickY = 0;
    private final Identifier rightClickCursor = Identifier.of("openminemap", "selectcursor.png");
    public int horizontalSide = 1;
    public int verticalSize = 1;
    TextRenderer textRenderer;
    //private WebAppSelectLayer webSelect = null;
    public Waypoint selectedWaypoint;
    private boolean firstOptionIsBold = false;

    private final String[] waypointMenuOptions = {
        "",
        "Teleport Here",
        "Copy Coordinates",
        "Open In...",
        "Edit Waypoint",
        "Set Snap Angle"
    };
    private final String[] waypointMenuOptionMinusAngle = {
        "",
        "Teleport Here",
        "Copy Coordinates",
        "Open In...",
        "Edit Waypoint"
    };
    private final String[] pinnedWaypointOptions = {
        "",
        "Teleport Here",
        "Copy Coordinates",
        "Open In...",
        "Edit Waypoint",
        "View On Map",
        "Unpin",
        "Set Snap Angle"
    };
    private final String[] pinnedWaypointOptionsMinusSnap = {
        "",
        "Teleport Here",
        "Copy Coordinates",
        "Open In...",
        "Edit Waypoint",
        "View On Map",
        "Unpin"
    };
    private final String[] defaultOptions = {
        "Teleport Here",
        "Copy Coordinates",
        "Open In...",
        "Create Waypoint"
    };
    private String[] menuOptions;

    private RightClickMenuType displayType = RightClickMenuType.HIDDEN;

    public void setDisplayType(RightClickMenuType type) {
        this.displayType = type;
        firstOptionIsBold = false;
        switch (type) {
            case RightClickMenuType.DEFAULT -> menuOptions = defaultOptions;
            case RightClickMenuType.WAYPOINT -> {
                this.selectedWaypoint = FullscreenMapScreen.map.getHoveredWaypoint();
                if (selectedWaypoint.angle < 0) menuOptions = waypointMenuOptionMinusAngle;
                else menuOptions = waypointMenuOptions;
                menuOptions[0] = selectedWaypoint.name;
                firstOptionIsBold = true;
            }
            case RightClickMenuType.PINNED_WAYPOINT -> {
                this.selectedWaypoint = FullscreenMapScreen.getSelectedPinnedWaypoint();
                if (selectedWaypoint.angle < 0) menuOptions = pinnedWaypointOptionsMinusSnap;
                else menuOptions = pinnedWaypointOptions;
                menuOptions[0] = selectedWaypoint.name;
                firstOptionIsBold = true;
            }
        }

        this.setHeight(Math.max(16 * menuOptions.length, 16));
        width = 16;
        for (int i = 0; i < menuOptions.length; i++) {
            width = Math.max(width, 8 + textRenderer.getWidth(firstOptionIsBold && i == 0 ? Text.literal(menuOptions[i]).formatted(Formatting.BOLD) : Text.literal(menuOptions[i])));
        }
        this.setWidth(width);
    }

    public RightClickMenuType getDisplayType() {
        return displayType;
    }

    public RightClickMenu(int x, int y, TextRenderer textRenderer) {
        super(x, y, 0, 0, Text.empty());
        useTp = Objects.equals(ConfigFile.readParameter(ConfigOptions.RIGHT_CLICK_MENU_USES), "/tp");
        this.textRenderer = textRenderer;

        this.menuOptions = defaultOptions;
        this.setDisplayType(RightClickMenuType.HIDDEN);

    }

    static float savedMouseLat;
    static float savedMouseLong;

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + width, getY() + height, 0x00000000);
        if (this.isMouseOver(UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getX()), UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getY()))) {
            hoverOn = (int) Math.ceil((UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getY()) - this.getY() + UnitConvert.pixelToScaledCoords(1))/16);
        } else {
            hoverOn = 0;
        }

    }

    public void drawWidget(DrawContext context, TextRenderer renderer) {
        if (displayType == RightClickMenuType.HIDDEN) return;
        context.fill(getX(), getY(), getX() + width, getY() + height, 0x88000000);

        for (int i = 0; i < menuOptions.length; i++) {
            context.drawText(
                    renderer,
                    firstOptionIsBold && i == 0 ? Text.literal(menuOptions[i]).formatted(Formatting.BOLD) : Text.literal(menuOptions[i]),
                    getX() + 4,
                    getY() + 4 + (16 * i),
                    hoverOn == i + 1 && !(firstOptionIsBold && i == 0) ?
                            0xFFa8afff :
                            0xFFFFFFFF,
                    false
            );
        }

        if (displayType == RightClickMenuType.DEFAULT) context.drawTexture(RenderPipelines.GUI_TEXTURED, rightClickCursor, (int) clickX - 4, (int) clickY - 4, 0, 0, 9, 9, 9, 9);

    }

    @Override
    public void onClick(Click click, boolean doubled) {
        switch (hoverOn - (displayType != RightClickMenuType.DEFAULT ? 1 : 0)) { //accounts for the extra name field
            case 1: {
                //MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll " + savedMouseLat + " " + savedMouseLong);
                try { //can be used during development to use the /tp command instead of /tpll
                    if (MinecraftClient.getInstance().player != null) {
                        double[] mcXz = Projection.from_geo(savedMouseLat, savedMouseLong);
                        if (useTp) {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tp "+(int) mcXz[0]+" "+PlayersManager.getHighestPoint(mcXz[0], mcXz[1])+" "+ (int) mcXz[1]);
                        } else {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll "+savedMouseLat+" "+savedMouseLong);
                        }
                    }
                } catch (CoordinateValueError error) {
                    System.out.println("Error with teleport here");
                }
                break;
            }
            case 2: {
                try {
                    //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("test"), null);
                    MinecraftClient.getInstance().keyboard.setClipboard(savedMouseLat + " " + savedMouseLong);
                } catch (HeadlessException e) {
                    System.out.println("Unable to write to clipboard; System does not support it.");
                }
                break;
            }
            case 3: {
                selectingSite = !selectingSite;
                int modX = 0;
                int modY = 0;
                if (horizontalSide == -1) modX -= width + 8 + 14;
                if (verticalSize == -1) modY -= (98 - height);

                FullscreenMapScreen.webAppSelectLayer.setPosition(getX() + width + 4 + modX, getY() + modY);
                return;
            }
            case 4: {
                if (displayType == RightClickMenuType.DEFAULT) {
                    MinecraftClient.getInstance().setScreen(
                            new WaypointScreen(savedMouseLat, savedMouseLong)
                    );
                } else {
                    //open the waypoint screen in edit mode
                    MinecraftClient.getInstance().setScreen(
                            new WaypointScreen(selectedWaypoint)
                    );
                }
                break;
            }
            case 5: {
                if (displayType == RightClickMenuType.WAYPOINT) {
                    setSnapAngle();
                } else { //Pinned
                    FullscreenMapScreen.map.setMapPosition(
                            UnitConvert.longToMapX(selectedWaypoint.longitude, FullscreenMapScreen.map.getZoom(), FullscreenMapScreen.map.getTileSize()),
                            UnitConvert.latToMapY(selectedWaypoint.latitude, FullscreenMapScreen.map.getZoom(), FullscreenMapScreen.map.getTileSize())
                    );
                }
                break;
            }
            case 6: {
                if (!WaypointFile.setWaypointPinned(selectedWaypoint.name, false)) {
                    OpenMineMapClient.debugMessages.add("OpenMineMap: Waypoint property change failed");
                }
                break;
            }
            case 7: {
                setSnapAngle();
                break;
            }
            default: {
                //should never occur, but it's here just in case (:
            }
        }
        FullscreenMapScreen.disableRightClickMenu();
    }

    private void setSnapAngle() {
        ConfigFile.writeParameter(ConfigOptions.SNAP_ANGLE, Double.toString(selectedWaypoint.angle));
        HudMap.setSnapAngle();
    }

    private void snapToWaypointAngle() {
        KeyInputHandler.snapToAngle(-selectedWaypoint.angle);
    }

    protected void setSavedMouseLatLong(double x, double y) {
        savedMouseLat = (float) y;
        savedMouseLong = (float) x;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}
}