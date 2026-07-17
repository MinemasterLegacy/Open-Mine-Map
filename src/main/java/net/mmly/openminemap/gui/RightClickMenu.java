package net.mmly.openminemap.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.draw.Justify;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.WebIcon;
import net.mmly.openminemap.event.KeyInputHandler;
import net.mmly.openminemap.http.RequestManager;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.*;
import net.mmly.openminemap.waypoint.WaypointScreen;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class RightClickMenu extends ClickableWidget {

    // = 16 * number of menu options
    private static final int OPTION_HEIGHT = 16;
    private RightClickMenuOption selectedOption;
    static double clickX = 0;
    static double clickY = 0;
    private final Identifier rightClickMarker = Identifier.of("openminemap", "locationhighlight.png");
    public int horizontalSide = 1;
    public int verticalSize = 1;
    TextRenderer textRenderer;
    //private WebAppSelectLayer webSelect = null;
    public NamedLocation selectedLocation;
    private boolean firstOptionIsBold = false;
    public static RightClickMenu instance;
    static float savedMouseLat;
    static float savedMouseLong;

    private static final ArrayList<WebIcon> webIcons = new ArrayList<>();
    private WebIcon webIconSelection = null;
    private int webIconScroll = 0;
    private int webIconsTotalWidth = 0;
    private boolean showWebIcons = false;

    private static final RightClickMenuOption[] waypointMenuOptions = {
        RightClickMenuOption.NAME,
        RightClickMenuOption.TELEPORT_HERE,
        RightClickMenuOption.COPY_COORDINATES,
        RightClickMenuOption.OPEN_IN,
        RightClickMenuOption.EDIT_WAYPOINT,
        RightClickMenuOption.SET_SNAP_ANGLE
    };
    private static final RightClickMenuOption[] pinnedWaypointOptions = {
        RightClickMenuOption.NAME,
        RightClickMenuOption.TELEPORT_HERE,
        RightClickMenuOption.COPY_COORDINATES,
        RightClickMenuOption.OPEN_IN,
        RightClickMenuOption.EDIT_WAYPOINT,
        RightClickMenuOption.VIEW_ON_MAP,
        RightClickMenuOption.UNPIN,
        RightClickMenuOption.SET_SNAP_ANGLE
    };
    private static final RightClickMenuOption[] defaultOptions = {
        RightClickMenuOption.TELEPORT_HERE,
        RightClickMenuOption.COPY_COORDINATES,
        RightClickMenuOption.OPEN_IN,
        RightClickMenuOption.CREATE_WAYPOINT,
        RightClickMenuOption.REVERSE_SEARCH
    };
    private static final RightClickMenuOption[] waypointScreenOptions = {
        RightClickMenuOption.TELEPORT_HERE,
        RightClickMenuOption.COPY_COORDINATES,
    };
    private static final RightClickMenuOption[] searchLocationOption = {
        RightClickMenuOption.NAME,
        RightClickMenuOption.TELEPORT_HERE,
        RightClickMenuOption.COPY_COORDINATES,
        RightClickMenuOption.OPEN_IN,
        RightClickMenuOption.CREATE_WAYPOINT
    };

    private ArrayList<RightClickMenuOption> menuOptions;
    private RightClickMenuType displayType = RightClickMenuType.HIDDEN;

    public static RightClickMenu getInstance() {
        return instance;
    }

    private ArrayList<RightClickMenuOption> getMenuOptions(RightClickMenuType type) {
        return getMenuOptions(type, false);
    }

    private ArrayList<RightClickMenuOption> getMenuOptions(RightClickMenuType type, boolean withSnapAngle) {
        RightClickMenuOption[] options = determineOption(type);
        ArrayList<RightClickMenuOption> list = new ArrayList<>(Arrays.asList(options));
        if (ConfigOptions.WEB_OPTIONS.getAsString().isEmpty()) list.remove(RightClickMenuOption.OPEN_IN);
        if (!withSnapAngle) list.remove(RightClickMenuOption.SET_SNAP_ANGLE);
        return list;
    }

    private static RightClickMenuOption[] determineOption(RightClickMenuType type) {
        if (type == RightClickMenuType.WAYPOINT) return waypointMenuOptions;
        if (type == RightClickMenuType.PINNED_WAYPOINT) return pinnedWaypointOptions;
        if (type == RightClickMenuType.SCREEN_WAYPOINT) return waypointScreenOptions;
        if (type == RightClickMenuType.SEARCH_LOCATION) return searchLocationOption;
        return defaultOptions;
    }

    public void setDisplayType(RightClickMenuType type, NamedLocation location) {
        this.displayType = type;
        firstOptionIsBold = false;
        if (type.isLocationType) {
            this.selectedLocation = location;
            firstOptionIsBold = true;
            menuOptions = getMenuOptions(type, !(selectedLocation.angle < 0));
        } else if (type == RightClickMenuType.DEFAULT) {
            menuOptions = getMenuOptions(RightClickMenuType.DEFAULT);
        }

        this.setHeight(Math.max(OPTION_HEIGHT * menuOptions.size(), OPTION_HEIGHT));
        width = 16;
        for (int i = 0; i < menuOptions.size(); i++) {
            int compare = 8;
            if (menuOptions.get(i) == RightClickMenuOption.NAME) compare += textRenderer.getWidth(Text.literal(selectedLocation == null ? "(null)" : selectedLocation.name).formatted(Formatting.BOLD));
            else compare += textRenderer.getWidth(Text.translatable(menuOptions.get(i).getTranslationKey()));
            width = Math.max(width, 8 + compare);
        }
        this.setWidth(width);
    }

    public static RightClickMenuType getDisplayType() {
        return instance.displayType;
    }

    public static boolean useTp() {
        String option = ConfigOptions.TELEPORT_METHOD.getAsStringFromValues(ConfigOptions.Values.TP_COMMANDS);
        if (option.equals("tpll")) return false;
        if (option.equals("tp")) return true;
        //option is dynamic
        return MinecraftClient.getInstance().isInSingleplayer();
    }

    public static boolean useTpll() {
        return !useTp();
    }

    public RightClickMenu(TextRenderer textRenderer) {
        super(-500, -500, 0, 0, Text.empty());

        instance = this;
        this.textRenderer = textRenderer;

        webIcons.clear();
        for (String s : ConfigOptions.WEB_OPTIONS.getAsString().split(",")) {
            WebIcon icon = WebIcon.getEnumFromName(s);
            if (icon != null) webIcons.add(icon);
        }

        this.menuOptions = getMenuOptions(RightClickMenuType.DEFAULT);
        this.setDisplayType(RightClickMenuType.HIDDEN, null);

        populateWebOptions();
    }

    private void populateWebOptions() {
        webIcons.clear();
        for (String s : ConfigOptions.WEB_OPTIONS.getAsString().split(",")) {
            WebIcon icon = WebIcon.getEnumFromName(s);
            if (icon != null) webIcons.add(icon);
        }
        webIconsTotalWidth = webIcons.size() * 14;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + width, getY() + height, 0x00000000);
        if (this.isMouseOver(mouseX, mouseY)) {
            selectedOption = menuOptions.get((mouseY - getY()) / OPTION_HEIGHT);
            if (selectedOption == RightClickMenuOption.NAME) {
                selectedOption = null;
            }
        } else {
            selectedOption = null;
        }

        if (selectedOption == RightClickMenuOption.OPEN_IN) {
            int selId = (mouseX - getX() - webIconScroll) / 14;
            if (selId < 0) webIconSelection = null;
            else if (selId > webIcons.size()-1) webIconSelection = null;
            else webIconSelection = webIcons.get(selId);
        } else {
            webIconSelection = null;
        }

    }

    public static void disableMenu() {
        if (instance != null) {
            instance.setDisplayType(RightClickMenuType.HIDDEN, null);
            instance.setPosition(-500, 500);
        }
        PinnedWaypointsLayer.menuSelection = -1;
        if (getInstance() != null) getInstance().selectedLocation = null;
    }

    public static void enableMenu(RightClickMenuType type, double mapX, double mapY, NamedLocation waypoint) {
        if (type == RightClickMenuType.HIDDEN) return;
        PinnedWaypointsLayer.menuSelection = -1;
        instance.setDisplayType(type, waypoint);
        clickX = mapX;
        clickY = mapY;
        instance.webIconScroll = 0;
        instance.showWebIcons = false;
        instance.setPosition((int) mapX, (int) mapY);

        if (type.isLocationType) {
            instance.setSavedMouseLatLong(waypoint.longitude, waypoint.latitude);
        } else {
            instance.setSavedMouseLatLong(MapScreen.map.getMouseLong(), MapScreen.map.getMouseLat());
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

    private MutableText getTextFor(RightClickMenuOption option) {
        if (option == null) return Text.literal("[null]").formatted(Formatting.GRAY);
        if (option == RightClickMenuOption.NAME) return Text.literal(selectedLocation.name).formatted(Formatting.BOLD);
        else return Text.translatable(option.getTranslationKey());
    }

    public void drawWidget(DrawContext context, TextRenderer renderer) {
        if (displayType == RightClickMenuType.HIDDEN) return;
        context.fill(getX(), getY(), getX() + width, getY() + height, displayType == RightClickMenuType.SCREEN_WAYPOINT ? 0xFF000000 : MapScreen.backingColor);

        for (int i = 0; i < menuOptions.size(); i++) {
            boolean selected = selectedOption == menuOptions.get(i);
            MutableText text = getTextFor(menuOptions.get(i));

            if (menuOptions.get(i) == RightClickMenuOption.OPEN_IN && selected && showWebIcons) {
                context.enableScissor(getX() + 1, getY(), getRight() - 1, getBottom());
                int x = getX() + 1 + webIconScroll;
                for (WebIcon icon : webIcons) {
                    if (webIconSelection == icon) {
                        UContext.drawTexture(icon.highlight, x, getY() + 1 + (OPTION_HEIGHT * i), 12, 16, 12, 16);
                    }
                    UContext.drawTexture(icon.icon, x + 1, getY() + 2 + (OPTION_HEIGHT * i), 10, 14, 10, 14);
                    x += 14;
                }

                context.disableScissor();
                if (webIconSelection != null) context.drawTooltip(textRenderer, Text.of(webIconSelection.tooltipText), getX() - 6, getY() + (OPTION_HEIGHT * (i-1) + 14));

                if (webIconScroll < 0) {
                    UContext.drawDottedVerticalLine(getY() + (OPTION_HEIGHT * i), getY() + (OPTION_HEIGHT * i) + 17, getX(), (MapScreen.getPlainTextColor() == 0xFFFFFFFF ? 0xFFa8afff : MapScreen.getPlainTextColor()));
                }
                if (webIconsTotalWidth + webIconScroll > width) {
                    UContext.drawDottedVerticalLine(getY() + (OPTION_HEIGHT * i), getY() + (OPTION_HEIGHT * i) + 17, getRight() - 1, (MapScreen.getPlainTextColor() == 0xFFFFFFFF ? 0xFFa8afff : MapScreen.getPlainTextColor()));
                }

                continue;
            }

            UContext.drawJustifiedText(
                    selected && MapScreen.getPlainTextColor() != 0xFFFFFFFF ? text.formatted(Formatting.UNDERLINE) : text,
                    horizontalSide == -1 ? Justify.RIGHT : Justify.LEFT,
                    horizontalSide == -1 ? getX() + width - 4 : getX() + 4,
                    getY() + 4 + (OPTION_HEIGHT * i),
                    selected ?
                            (MapScreen.getPlainTextColor() == 0xFFFFFFFF ? 0xFFa8afff : MapScreen.getPlainTextColor()) :
                            MapScreen.getPlainTextColor()

            );
        }

        if (displayType == RightClickMenuType.DEFAULT) UContext.drawTexture(rightClickMarker, (int) clickX - 5, (int) clickY - 14, 10, 14);

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (webIconsTotalWidth < width) return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        if (verticalAmount > 0) {
            webIconScroll += 14;
        }
        if (verticalAmount < 0) {
            webIconScroll -= 14;
        }
        webIconScroll = Math.clamp(webIconScroll, width - webIconsTotalWidth, 0);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        switch (selectedOption) {
            case TELEPORT_HERE: {
                //MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll " + savedMouseLat + " " + savedMouseLong);
                try { //can be used during development to use the /tp command instead of /tpll
                    if (MinecraftClient.getInstance().player != null) {
                        double[] mcXz = Projection.from_geo(savedMouseLat, savedMouseLong);
                        if (useTp()) {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tp "+(int) mcXz[0]+" "+PlayersManager.getHighestPoint(mcXz[0], mcXz[1])+" "+ (int) mcXz[1]);
                        } else {
                            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll "+savedMouseLat+" "+savedMouseLong);
                        }
                        if (MinecraftClient.getInstance().currentScreen instanceof WaypointScreen) {
                            MinecraftClient.getInstance().setScreen(new MapScreen());
                            MapScreen.map.setMapLatLong(selectedLocation.latitude, selectedLocation.longitude);
                        }
                    }
                } catch (CoordinateValueError error) {
                    MapScreen.addNotification(new Notification(Text.translatable("omm.notification.something-wrong")));
                }
                break;
            }
            case COPY_COORDINATES: {
                try {
                    //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("test"), null);
                    MinecraftClient.getInstance().keyboard.setClipboard(savedMouseLat + " " + savedMouseLong);
                    MapScreen.addNotification(new Notification(Text.translatable("omm.key.execute.copy-coordinates")));
                } catch (HeadlessException e) {
                    MapScreen.addNotification(new Notification(Text.translatable("omm.notification.something-wrong")));
                }
                break;
            }
            case OPEN_IN: {
                if (!showWebIcons) {
                    showWebIcons = true;
                    return;
                }
                if (webIconSelection == null) return;
                openUrl(webIconSelection);
                return;
            }
            case CREATE_WAYPOINT: {
                if (selectedLocation != null) MinecraftClient.getInstance().setScreen(
                        new WaypointScreen(savedMouseLat, savedMouseLong, selectedLocation.name)
                );
                else MinecraftClient.getInstance().setScreen(
                        new WaypointScreen(savedMouseLat, savedMouseLong)
                );
                selectedLocation = null;
                break;
            }
            case EDIT_WAYPOINT: {
                //open the waypoint screen in edit mode
                MinecraftClient.getInstance().setScreen(
                        new WaypointScreen((Waypoint) selectedLocation)
                );
                break;
            }
            case VIEW_ON_MAP: {
                MapScreen.followPlayer(false);
                MapScreen.map.setMapLatLong(selectedLocation.latitude, selectedLocation.longitude);
                break;
            }
            case UNPIN: {
                if (!WaypointFile.setWaypointPinned(selectedLocation.name, false)) {
                    OpenMineMapClient.debugMessages.add(Text.translatable("omm.error.waypoint-property-failiure").getString());
                }
                break;
            }
            case SET_SNAP_ANGLE: {
                setSnapAngle();
                MapScreen.addNotification(new Notification(Text.of(
                        Text.translatable("omm.notification.snap-angle-set").getString() +
                        UnitConvert.floorToPlace(HudMap.snapAngle,3) +
                        "°")));
                break;
            }
            case REVERSE_SEARCH: {
                MapScreen.addNotification(new Notification(Text.translatable("omm.notification.searching")));
                RequestManager.reverseSearch(savedMouseLat, savedMouseLong);
                break;
            }
            case NAME: {
                return; //prevents from disabling right click menu when clicking name
            }
        }
        disableMenu();
    }

    private void setSnapAngle() {
        ConfigFile.writeParameter(ConfigOptions.SNAP_ANGLE, Double.toString(selectedLocation.angle));
        HudMap.setSnapAngle();
    }

    private void snapToWaypointAngle() {
        KeyInputHandler.snapToAngle(-selectedLocation.angle);
    }

    protected void setSavedMouseLatLong(double x, double y) {
        savedMouseLat = (float) y;
        savedMouseLong = (float) x;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    private static void openUrl(WebIcon icon) {
        float lat = RightClickMenu.savedMouseLat;
        float lon = RightClickMenu.savedMouseLong;
        int zoom = MapScreen.map.getTileZoom();
        switch (icon) {
            case GOOGLE_MAPS: {
                openUrl("https://google.com/maps/@"+lat+","+lon+","+Math.max(2, zoom)+"z", false);
                break;
            } case GOOGLE_EARTH: {
                openUrl("https://earth.google.com/web/search/"+lat+"+"+lon, false);
                break;
            } case GOOGLE_EARTH_PRO: {
                openUrl(lat+", "+lon+" (.kml file)", true);
                break;
            } case OPEN_STREET_MAP: {
                openUrl("https://openstreetmap.org/#map="+Math.clamp(zoom, 0, 19)+"/"+lat+"/"+lon, false);
                break;
            } case YANDEX_MAPS: {
                openUrl("https://yandex.com/maps/?ll="+lon+"%2C"+lat+"&z="+Math.clamp(zoom, 2, 19), false);
                break;
            } case BING_MAPS: {
                openUrl("https://bing.com/maps?cp="+lat+"~"+lon+"&lvl="+Math.clamp(zoom, 2, 22), false);
                break;
            } case APPLE_MAPS: {
                openUrl("https://maps.apple.com/frame?center="+lat+"%2C"+lon, false);
                break;
            } case BUILD_THE_EARTH: {
                openUrl("https://buildtheearth.net/map?z="+Math.clamp(zoom, 1, 22)+"&lat="+lat+"&lng="+lon, false);
                break;
            } case MAPILLARY: {
                openUrl("https://www.mapillary.com/app/?lat="+lat+"&lng="+lon+"&z="+Math.clamp(zoom, 1, 19.9), false);
                break;
            } case LOOKMAP: {
                openUrl("https://lookmap.skzk.dev/#c="+Math.clamp(zoom, 3, 20)+"/"+lat+"/"+lon, false);
                break;
            }
        }

    }

    private static void openUrl(String url, boolean isGep) {
        MinecraftClient.getInstance().setScreen(
                new ConfirmLinkScreen(new BooleanConsumer() {
                    @Override
                    public void accept(boolean b) {
                        if(b) {
                            if (isGep) openInGep();
                            else Util.getOperatingSystem().open(url);
                        }
                        MinecraftClient.getInstance().setScreen(new MapScreen());
                    }
                }, url, true)

        );
    }

    private static void openInGep() {
        //Util.getOperatingSystem().
        File file = new File(TileManager.getRootFile() + "openminemap/location.kml");
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "utf-8"))) {
            writer.write(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n" +
                            "<Document>\n" +
                            "\t<name>OpenMineMap Location</name>\n" +
                            "\t<Placemark>\n" +
                            "\t\t<name>"+RightClickMenu.savedMouseLat+", "+RightClickMenu.savedMouseLong+"</name>\n" +
                            "\t\t<LookAt>\n" +
                            "\t\t\t<longitude>"+RightClickMenu.savedMouseLong+"</longitude>\n" +
                            "\t\t\t<latitude>"+RightClickMenu.savedMouseLat+"</latitude>\n" +
                            "\t\t\t<altitude>0</altitude>\n" +
                            "\t\t\t<heading>-11.42103893546798</heading>\n" +
                            "\t\t\t<tilt>0</tilt>\n" +
                            "\t\t\t<range>"+zoomToMetersAbove(MapScreen.map.getTileZoom())+"</range>\n" +
                            "\t\t\t<gx:altitudeMode>relativeToSeaFloor</gx:altitudeMode>\n" +
                            "\t\t</LookAt>\n" +
                            "\t\t<Point>\n" +
                            "\t\t\t<gx:drawOrder>1</gx:drawOrder>\n" +
                            "\t\t\t<coordinates>"+RightClickMenu.savedMouseLong+","+RightClickMenu.savedMouseLat+",0</coordinates>\n" +
                            "\t\t</Point>\n" +
                            "\t</Placemark>\n" +
                            "</Document>\n" +
                            "</kml>"
            );
        } catch (IOException e) {
            return;
        }
        //System.out.println(file.exists());
        Util.getOperatingSystem().open(file);
    }

    private static String zoomToMetersAbove(int z) {
        return String.format("%.7f",
                84412457.8 * Math.pow(0.5, z)
        );
    }

}