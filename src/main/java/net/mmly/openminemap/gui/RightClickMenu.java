package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.draw.Justify;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.event.KeyInputHandler;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.map.RequestManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.*;
import net.mmly.openminemap.waypoint.WaypointScreen;

import java.awt.*;
import java.util.Objects;

public class RightClickMenu extends ClickableWidget {

    // = 16 * number of menu options
    private int hoverOn = 0;
    private boolean useTp;
    public static boolean selectingSite = false;
    static double clickX = 0;
    static double clickY = 0;
    private final Identifier rightClickCursor = Identifier.of("openminemap", "selectcursor.png");
    public int horizontalSide = 1;
    public int verticalSize = 1;
    TextRenderer textRenderer;
    //private WebAppSelectLayer webSelect = null;
    public Waypoint selectedWaypoint;
    private boolean firstOptionIsBold = false;
    public static RightClickMenu instance;

    private final RightClickMenuOption[] waypointMenuOptions = {
        RightClickMenuOption.NAME,
        RightClickMenuOption.TELEPORT_HERE,
        RightClickMenuOption.COPY_COORDINATES,
        RightClickMenuOption.OPEN_IN,
        RightClickMenuOption.EDIT_WAYPOINT,
        RightClickMenuOption.SET_SNAP_ANGLE
    };
    private final RightClickMenuOption[] waypointMenuOptionMinusAngle = {
        RightClickMenuOption.NAME,
        RightClickMenuOption.TELEPORT_HERE,
        RightClickMenuOption.COPY_COORDINATES,
        RightClickMenuOption.OPEN_IN,
        RightClickMenuOption.EDIT_WAYPOINT
    };
    private final RightClickMenuOption[] pinnedWaypointOptions = {
        RightClickMenuOption.NAME,
        RightClickMenuOption.TELEPORT_HERE,
        RightClickMenuOption.COPY_COORDINATES,
        RightClickMenuOption.OPEN_IN,
        RightClickMenuOption.EDIT_WAYPOINT,
        RightClickMenuOption.VIEW_ON_MAP,
        RightClickMenuOption.UNPIN,
        RightClickMenuOption.SET_SNAP_ANGLE
    };
    private final RightClickMenuOption[] pinnedWaypointOptionsMinusSnap = {
        RightClickMenuOption.NAME,
        RightClickMenuOption.TELEPORT_HERE,
        RightClickMenuOption.COPY_COORDINATES,
        RightClickMenuOption.OPEN_IN,
        RightClickMenuOption.EDIT_WAYPOINT,
        RightClickMenuOption.VIEW_ON_MAP,
        RightClickMenuOption.UNPIN
    };
    private final RightClickMenuOption[] defaultOptions = {
        RightClickMenuOption.TELEPORT_HERE,
        RightClickMenuOption.COPY_COORDINATES,
        RightClickMenuOption.OPEN_IN,
        RightClickMenuOption.CREATE_WAYPOINT,
        RightClickMenuOption.REVERSE_SEARCH
    };
    private final RightClickMenuOption[] waypointScreenOptions = {
        RightClickMenuOption.NAME,
        RightClickMenuOption.TELEPORT_HERE,
        RightClickMenuOption.COPY_COORDINATES
    };
    private RightClickMenuOption[] menuOptions;

    private RightClickMenuType displayType = RightClickMenuType.HIDDEN;

    public static RightClickMenu getInstance() {
        return instance;
    }

    private RightClickMenuOption[] getMenuOptions(RightClickMenuType type) {
        return getMenuOptions(type, false);
    }

    private RightClickMenuOption[] getMenuOptions(RightClickMenuType type, boolean withSnapAngle) {
        if (type == RightClickMenuType.WAYPOINT) {
            if (withSnapAngle) return waypointMenuOptions;
            else return waypointMenuOptionMinusAngle;
        }
        if (type == RightClickMenuType.PINNED_WAYPOINT) {
            if (withSnapAngle) return pinnedWaypointOptions;
            else return pinnedWaypointOptionsMinusSnap;
        }
        if (type == RightClickMenuType.SCREEN_WAYPOINT) {
            return waypointScreenOptions;
        }
        return defaultOptions;
    }

    public void setDisplayType(RightClickMenuType type, Waypoint waypoint) {
        this.displayType = type;
        firstOptionIsBold = false;
        if (type.isWaypointType) {
            this.selectedWaypoint = waypoint;
            firstOptionIsBold = true;
            menuOptions = getMenuOptions(type, !(selectedWaypoint.angle < 0));
        } else if (type == RightClickMenuType.DEFAULT) {
            menuOptions = getMenuOptions(RightClickMenuType.DEFAULT);
        }

        this.setHeight(Math.max(16 * menuOptions.length, 16));
        width = 16;
        for (int i = 0; i < menuOptions.length; i++) {
            int compare = 8;
            if (menuOptions[i] == RightClickMenuOption.NAME) compare += textRenderer.getWidth(Text.literal(selectedWaypoint.name).formatted(Formatting.BOLD));
            else compare += textRenderer.getWidth(Text.translatable(menuOptions[i].getTranslationKey()));
            width = Math.max(width, 8 + compare);
        }
        this.setWidth(width);
    }

    public static RightClickMenuType getDisplayType() {
        return instance.displayType;
    }

    public RightClickMenu(TextRenderer textRenderer) {
        super(-500, -500, 0, 0, Text.empty());
        instance = this;
        useTp = Objects.equals(ConfigFile.readParameter(ConfigOptions.RIGHT_CLICK_MENU_USES), "/tp");
        this.textRenderer = textRenderer;

        this.menuOptions = getMenuOptions(RightClickMenuType.DEFAULT);
        this.setDisplayType(RightClickMenuType.HIDDEN, null);
    }

    static float savedMouseLat;
    static float savedMouseLong;

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + width, getY() + height, 0x00000000);
        if (this.isMouseOver(mouseX, mouseY)) {
            hoverOn = (int) Math.ceil((mouseY - this.getY() + UnitConvert.pixelToScaledCoords(1))/16);
        } else {
            hoverOn = 0;
        }

    }

    public static void disableMenu() {
        instance.setDisplayType(RightClickMenuType.HIDDEN, null);
        instance.setPosition(-500, 500);
        selectingSite = false;
        PinnedWaypointsLayer.menuSelection = -1;
    }

    public static void enableMenu(RightClickMenuType type, double mapX, double mapY, Waypoint waypoint) {
        if (type == RightClickMenuType.HIDDEN) return;
        PinnedWaypointsLayer.menuSelection = -1;
        instance.setDisplayType(type, waypoint);
        RightClickMenu.selectingSite = false;
        clickX = mapX;
        clickY = mapY;
        instance.setPosition((int) mapX, (int) mapY);

        if (type.isWaypointType) {
            instance.setSavedMouseLatLong(waypoint.longitude, waypoint.latitude);
        } else {
            instance.setSavedMouseLatLong(FullscreenMapScreen.map.getMouseLong(), FullscreenMapScreen.map.getMouseLat());
        }

        if (!(MinecraftClient.getInstance().currentScreen instanceof WaypointScreen)) instance.repositionForOverflow();
    }

    private void repositionLeftward() {
        this.setX(this.getX() - width + 1);
        this.horizontalSide = -1;
    }

    private void repositionDownward() {
        this.setY(this.getY() - height + 1);
        this.verticalSize = -1;
    }

    protected void repositionForOverflow() {
        Screen currentScreen = MinecraftClient.getInstance().currentScreen;
        if (currentScreen == null) return;
        int windowScaledWidth = currentScreen.width;
        int windowScaledHeight = currentScreen.height;

        if (displayType == RightClickMenuType.PINNED_WAYPOINT) {
            horizontalSide = 1;
            verticalSize = 1;
            return;
        }
        if (getX() + width > windowScaledWidth && getX() - width < 0) { //if there's no way to fit the whole menu on screen
            if (getX() > windowScaledWidth / 2) repositionLeftward();
            else horizontalSide = 1;
        } else if (getX() + width > windowScaledWidth) { //else, reposition left if needed
            repositionLeftward();
        } else horizontalSide = 1;

        if (getY() + height > windowScaledHeight && getY() - height < 0) {
            if (getY() > windowScaledHeight / 2) repositionDownward();
            else verticalSize = 1;
        } else if (getY() + height > windowScaledHeight) {
            repositionDownward();
        } else verticalSize = 1;
    }

    private Text getTextFor(RightClickMenuOption option) {
        if (option == null) return Text.literal("[null]").formatted(Formatting.GRAY);
        if (option == RightClickMenuOption.NAME) return Text.literal(selectedWaypoint.name).formatted(Formatting.BOLD);
        else return Text.translatable(option.getTranslationKey());
    }

    public void drawWidget(DrawContext context, TextRenderer renderer) {
        if (displayType == RightClickMenuType.HIDDEN) return;
        context.fill(getX(), getY(), getX() + width, getY() + height, 0x88000000);

        for (int i = 0; i < menuOptions.length; i++) {
            UContext.drawJustifiedText(
                    getTextFor(menuOptions[i]),
                    horizontalSide == -1 ? Justify.RIGHT : Justify.LEFT,
                    horizontalSide == -1 ? getX() + width - 4 : getX() + 4,
                    getY() + 4 + (16 * i),
                    hoverOn == i + 1 && !(firstOptionIsBold && i == 0) ?
                            0xFFa8afff :
                            0xFFFFFFFF
            );
        }

        if (displayType == RightClickMenuType.DEFAULT) context.drawTexture(RenderPipelines.GUI_TEXTURED, rightClickCursor, (int) clickX - 4, (int) clickY - 4, 0, 0, 9, 9, 9, 9);

    }

    @Override
    public void onClick(Click click, boolean doubled) {
        switch (menuOptions[hoverOn - 1]) {
            case TELEPORT_HERE: {
                //MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll " + savedMouseLat + " " + savedMouseLong);
                try { //can be used during development to use the /tp command instead of /tpll
                    if (MinecraftClient.getInstance().player != null) {
                        double[] mcXz = Projection.from_geo(savedMouseLat, savedMouseLong);
                        if (useTp) {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tp "+(int) mcXz[0]+" "+PlayersManager.getHighestPoint(mcXz[0], mcXz[1])+" "+ (int) mcXz[1]);
                        } else {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll "+savedMouseLat+" "+savedMouseLong);
                        }
                        if (MinecraftClient.getInstance().currentScreen instanceof WaypointScreen) {
                            MinecraftClient.getInstance().setScreen(new FullscreenMapScreen());
                            FullscreenMapScreen.map.setMapLatLong(selectedWaypoint.latitude, selectedWaypoint.longitude);
                        }
                    }
                } catch (CoordinateValueError error) {
                    System.out.println("Error with teleport here");
                }
                break;
            }
            case COPY_COORDINATES: {
                try {
                    //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("test"), null);
                    MinecraftClient.getInstance().keyboard.setClipboard(savedMouseLat + " " + savedMouseLong);
                    FullscreenMapScreen.addNotification(new Notification(Text.translatable("omm.key.execute.copy-coordinates")));
                } catch (HeadlessException e) {
                    System.out.println("Unable to write to clipboard; System does not support it.");
                }
                break;
            }
            case OPEN_IN: {
                selectingSite = !selectingSite;
                int modX = 0;
                int modY = 0;
                if (horizontalSide == -1) modX -= width + 8 + 14;
                if (verticalSize == -1) modY -= (98 - height);

                FullscreenMapScreen.webAppSelectLayer.setPosition(getX() + width + 4 + modX, getY() + modY);
                return;
            }
            case CREATE_WAYPOINT: {
                MinecraftClient.getInstance().setScreen(
                        new WaypointScreen(savedMouseLat, savedMouseLong)
                );
                break;
            }
            case EDIT_WAYPOINT: {
                //open the waypoint screen in edit mode
                MinecraftClient.getInstance().setScreen(
                        new WaypointScreen(selectedWaypoint)
                );
                break;
            }
            case VIEW_ON_MAP: {
                FullscreenMapScreen.followPlayer(false);
                FullscreenMapScreen.map.setMapLatLong(selectedWaypoint.latitude, selectedWaypoint.longitude);
                break;
            }
            case UNPIN: {
                if (!WaypointFile.setWaypointPinned(selectedWaypoint.name, false)) {
                    OpenMineMapClient.debugMessages.add("OpenMineMap: Waypoint property change failed");
                }
                break;
            }
            case SET_SNAP_ANGLE: {
                setSnapAngle();
                FullscreenMapScreen.addNotification(new Notification(Text.of(
                        Text.translatable("omm.notification.snap-angle-set").getString() +
                        UnitConvert.floorToPlace(HudMap.snapAngle,3) +
                        "°")));
                break;
            }
            case REVERSE_SEARCH: {
                FullscreenMapScreen.addNotification(new Notification(Text.translatable("omm.notification.searching")));
                RequestManager.setReverseSearchRequest(savedMouseLat, savedMouseLong);
                break;
            }
            case NAME: {
                return; //prevents from disabling right click menu when clicking name
            }
        }
        disableMenu();
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