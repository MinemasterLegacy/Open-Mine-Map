package net.mmly.openminemap.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.mmly.openminemap.config.ConfigScreen;
import net.mmly.openminemap.config.MapConfigScreen;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.event.KeyInputHandler;
import net.mmly.openminemap.http.MapType;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.TileLoader;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.raster.CreateRasterScreen;
import net.mmly.openminemap.raster.RasterScreen;
import net.mmly.openminemap.raster.RasterWarningScreen;
import net.mmly.openminemap.raster.ViewSetRastersScreen;
import net.mmly.openminemap.search.*;
import net.mmly.openminemap.util.*;
import net.mmly.openminemap.waypoint.WaypointScreen;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.BooleanSupplier;

public class MapScreen extends Screen { //Screen object that represents the fullscreen map
    public MapScreen() {
        super(Component.nullToEmpty("OMM Fullscreen Map"));
        toggleAltScreenMap(false);
    }

    public static int windowScaledHeight;
    public static int windowScaledWidth;
    //width range: 320 - 640

    private static final int BUTTON_SIZE = 20;
    private static final int BUTTON_MARGIN = 4;
    private static final int LINE_HEIGHT = 16;
    private static final String MAX_LENGTH_COORDINATE_STRING = "-99.99999°, -999.99999°";
    private static int attributionOffset = 0;
    private static final int[][] buttonPositions = new int[2][10];
    // modifiers used to offset the map so it can be moved relative to the screen
    // these modifiers should be scaled when the screen is zoomed in or zoomed out
    // Ex: zoom 0, range -128 - 127 | zoom 1, range -256 - 255 | zoom 2, range -512 - 511 | etc.

    private static final LinkedHashMap<ButtonFunction, ButtonLayer> buttonCenterShelf = new LinkedHashMap<>();
    private static final LinkedHashMap<ButtonFunction, ButtonLayer> buttonLeftShelf = new LinkedHashMap<>();
    //private static final LinkedHashMap<ButtonFunction, ButtonLayer> buttonRightShelf = new LinkedHashMap<>();

    private static RightClickMenu rightClickLayer;
    private static AttributionLayer attributionLayer;
    private static WikiLinkLayer bugReportLayer;
    private static CoordinateInfoLayer coordinateInfoLayer;
    private static ToggleButtonLayer toggleHudMapButtonLayer;
    private static ToggleButtonLayer toggleClaimRenderingButtonLayer;
    private static SearchButtonLayer searchButtonLayer;
    private static SearchBoxLayer searchBoxLayer;
    private static NetworkStatusLayer networkStatusLayer;
    public static SearchResultLayer[] searchResultLayers = new SearchResultLayer[SearchBoxLayer.MAX_RESULTS];
    private static PinnedWaypointsLayer pinnedWaypointsLayer;

    static MapScreen instance;
    public static final OmmMap map = new OmmMap(
            0,
            0,
            640,
            480,
            ConfigOptions._FS_LAST_ZOOM.getAsDouble(),
            ConfigOptions._FS_LAST_X.getAsDouble(),
            ConfigOptions._FS_LAST_Y.getAsDouble(),
            ConfigOptions._FS_LAST_TILE_SIZE.getAsInt()
    );
    private static final LinkedList<Notification> notifications = new LinkedList<>();

    public static int backingColor = 0x80000000;
    private static boolean textIsRainbow = false;
    private static int plainTextColor = 0xFFFFFFFF;
    private static int semiLightTextColor = 0xFFbfbfbf;
    private static int semiDarkTextColor = 0xFF7f7f7f;
    private static int darkTextColor = 0xFF3f3f3f;

    private static boolean renderAltMap = false;
    private boolean chatToBeOpened = false;
    private boolean chatIsOpened = false;
    private static boolean hudWasHidden;
    private static boolean altKeyPressed = false;
    public static boolean semiTransparentUi = false;


    public static void setPlainTextColor(int argb, boolean checkForRainbowText) {
        if (checkForRainbowText) textIsRainbow = (argb == 0xFF7f7f7f);
        plainTextColor = argb;
        semiLightTextColor = ColorUtil.darken(argb, 0.25);
        semiDarkTextColor = ColorUtil.darken(argb, 0.5);
        darkTextColor = ColorUtil.darken(argb, 0.75);
    }

    private int getMapboxAttributionSize() {
        int scale = (int) Math.round(minecraft.getWindow().getGuiScale() * 2);
        if (scale > 20) return 1;
        return (int) Math.ceil(30.0 / scale) * 2;
    }

    public static int getPlainTextColor() {
        return plainTextColor;
    }

    public static int getSemiLightTextColor() {
        return semiLightTextColor;
    }

    public static int getSemiDarkTextColor() {
        return semiDarkTextColor;
    }

    public static int getDarkTextColor() {
        return darkTextColor;
    }

    public static void clampZoom() {
        //used to decrease zoom level (if needed) when artificial zoom is disabled
        map.clampZoom();
    }

    public static void followPlayer(boolean follow) {
        map.setFollowPlayer(follow);
    }

    public static MapScreen getInstance() {
        return instance;
    }

    @Override
    public void onClose() {
        RightClickMenu.disableMenu();
        writeParameters();
        ConfigFile.writeToFile();
        this.minecraft.gui.setScreen(null);
        toggleAltScreenMap(false);
    }

    public static void writeParameters() {
        ConfigOptions._FS_LAST_ZOOM.write(Double.toString(map.getZoom()));
        ConfigOptions._FS_LAST_X.write(Double.toString(map.getMapCenterX()));
        ConfigOptions._FS_LAST_Y.write(Double.toString(map.getMapCenterY()));
        ConfigOptions._FS_LAST_TILE_SIZE.write(Integer.toString(map.getTileSize()));
    }

    private void updateScreenDims() {
        windowScaledHeight = minecraft.getWindow().getGuiScaledHeight();
        windowScaledWidth = minecraft.getWindow().getGuiScaledWidth();
        map.setRenderSize(windowScaledWidth, windowScaledHeight);
    }

    static protected void zoomIn() {
        map.zoomIn(1);
    }

    static protected void zoomOut() {
        map.zoomOut(1);
    }

    static protected void resetMap() {
        map.resetMap();
    }

    public static void openLinkScreen(String link, Screen returnScreen, boolean toggleAltScreenMap) {
        Minecraft.getInstance().gui.setScreen(
                new ConfirmLinkScreen(new BooleanConsumer() {
                    @Override
                    public void accept(boolean b) {
                        if(b) {
                            Util.getPlatform().openUri(link);
                        }
                        Minecraft.getInstance().gui.setScreen(returnScreen);
                    }

                }, link, true)

        );
        if (toggleAltScreenMap) toggleAltScreenMap(true);
    }

    public static NamedLocation getRightClickMenuLocation() {return rightClickLayer.selectedLocation;}


    public static Waypoint getSelectedPinnedWaypoint() {
        return pinnedWaypointsLayer.getSelectedWaypoint();
    }

    private static void onLeftClick() {
        RightClickMenu.disableMenu();
        searchBoxLayer.setFocused(false);
        //toggleSearchMenu(false);
    }

    private static void onRightClick() {
        if (!map.mouseIsOutOfBounds()) { //checks if mouse is positioned on the map (this variable will be "-.-" if it isn't)
            if (map.getHoveredWaypoint() != null) RightClickMenu.enableMenu(RightClickMenuType.WAYPOINT, map.getMouseX(), map.getMouseY(), map.getHoveredWaypoint());
            else if (map.getHoveredSearchResult() != null) RightClickMenu.enableMenu(RightClickMenuType.SEARCH_LOCATION, map.getMouseX(), map.getMouseY(), map.getHoveredSearchResult().asLocation());
            else RightClickMenu.enableMenu(RightClickMenuType.DEFAULT, map.getMouseX(), map.getMouseY(), null);
        } else {
            RightClickMenu.disableMenu();
        }
        //toggleSearchMenu(false);
    }

    public static void toggleSearchMenu(boolean toggle) {
        pinnedWaypointsLayer.visible = !toggle;
        searchBoxLayer.visible = toggle;
        if (toggle) {
            RightClickMenu.disableMenu();
            MapScreen.getInstance().setFocused(searchBoxLayer);
            searchBoxLayer.recalculateResults();
            MapScreen.getInstance().jumpToSearchBox();
        } else {
            SearchBoxLayer.setValueStore("");
            SearchBoxLayer.toggleSearching(false);
            SearchHistoryFile.writeToFile();
        }
    }

    public static boolean getSearchMenuState() {
        return searchBoxLayer.visible;
    }
    public void jumpToSearchBox() {
        setFocused(searchBoxLayer);
        SearchBoxLayer.resetScroll();
    }
    public void jumpToBestOption() {
        for (SearchResultLayer layer : searchResultLayers) {
            if (layer.isOption(SearchResultType.SEARCH) || layer.isOption(SearchResultType.COORDINATES) || layer.isHistoric()) {
                setFocused(layer);
                layer.keyPressed(new KeyEvent(GLFW.GLFW_KEY_ENTER, 0, 0));
                return;
            }
        }
    }

    public void jumpToSearchBox(KeyEvent input) {
         jumpToSearchBox();
         searchBoxLayer.keyPressed(input);
    }

    private static boolean blockZoomOnZoom() {
        return RightClickMenu.getDisplayType() != RightClickMenuType.HIDDEN;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        map.setMouseDown(false);
        return super.mouseReleased(click);
    }

    private BooleanSupplier getDisableConditionOf(ButtonFunction f) {
        return switch (f) {
            case ZOOMIN -> () -> map.getZoom() >= map.getMaxZoom();
            case ZOOMOUT -> () -> map.getZoom() <= 0;
            case FOLLOW -> () -> !PlayerAttributes.positionIsValid() || map.followingPlayer();
            case null, default -> null;
        };
    }

    @Override
    protected void init() { //called when screen is being initialized
        instance = this;
        map.initFields();
        toggleAltScreenMap(false);

        rightClickLayer = new RightClickMenu(this.font);
        this.addRenderableWidget(rightClickLayer);

        ButtonFunction[] shelfFunctions = ButtonFunction.getCenterShelf();
        for (ButtonFunction function : shelfFunctions) {
            buttonCenterShelf.put(function, new ButtonLayer(0, 0, function, getDisableConditionOf(function)));
            this.addRenderableWidget(buttonCenterShelf.get(function));
        }

        shelfFunctions = ButtonFunction.getLeftShelf();
        for (ButtonFunction function : shelfFunctions) {
            buttonLeftShelf.put(function, new ButtonLayer(0, 0, function, getDisableConditionOf(function)));
            this.addRenderableWidget(buttonLeftShelf.get(function));
        }

        toggleClaimRenderingButtonLayer = new ToggleButtonLayer(windowScaledWidth - 50, windowScaledHeight - 57, ToggleButtonLayer.Type.CLAIM_RENDERING);
        if (ConfigOptions.CLAIMS_RENDERING.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF)) this.addRenderableWidget(toggleClaimRenderingButtonLayer);

        toggleHudMapButtonLayer = new ToggleButtonLayer(windowScaledWidth - 25, windowScaledHeight - 57, ToggleButtonLayer.Type.TOGGLE_HUDMAP);
        this.addRenderableWidget(toggleHudMapButtonLayer);

        for (int i = 0; i < SearchBoxLayer.MAX_RESULTS; i++) {
            searchResultLayers[i] = new SearchResultLayer(26, 0, 250, i);
            this.addRenderableWidget(searchResultLayers[i]);
        }

        networkStatusLayer = new NetworkStatusLayer(0, 0);

        searchButtonLayer = new SearchButtonLayer(3, 3);
        this.addRenderableWidget(searchButtonLayer);
        searchBoxLayer = new SearchBoxLayer(this.font, 26, 3);
        this.addRenderableWidget(searchBoxLayer);

        coordinateInfoLayer = new CoordinateInfoLayer();
        this.addRenderableWidget(coordinateInfoLayer);
        attributionLayer = new AttributionLayer(windowScaledWidth - 157, windowScaledHeight - 16, 157, LINE_HEIGHT);
        this.addRenderableWidget(attributionLayer); //windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight,
        bugReportLayer = new WikiLinkLayer(windowScaledWidth - 157, windowScaledHeight - 32);
        this.addRenderableWidget(bugReportLayer); //windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight,

        TileManager.initializeConfigParameters();

        pinnedWaypointsLayer = new PinnedWaypointsLayer(0, 26, 20, 2, this.font);
        this.addRenderableWidget(pinnedWaypointsLayer);

        this.addRenderableWidget(map); //added last so it's checked last for clicking

        map.setDraggable(true);
        map.rightClickProcedure = MapScreen::onRightClick;
        map.leftClickProcedure = MapScreen::onLeftClick;
        map.blockZoomProcedure = MapScreen::blockZoomOnZoom;
        map.waypointClickedProcedure = MapScreen::onRightClick;
        map.setTextRenderer(this.font);
        map.doPlayerTooltipNames(true);
        map.setMouseDown(false);

        toggleSearchMenu(false);
    }

    private static void drawButtons(GuiGraphicsExtractor context) {
        for (ButtonFunction function : buttonCenterShelf.keySet()) {
            buttonCenterShelf.get(function).drawWidget(context);
        }
        for (ButtonFunction function : buttonLeftShelf.keySet()) {
            buttonLeftShelf.get(function).drawWidget(context);
        }
    }

    private static void updateWidgetPositions(Font textRenderer) {
        attributionLayer.updatePositionAndDimensions(coordinateInfoLayer.getWidth() + 3, windowScaledWidth, windowScaledHeight);
        //if attribution would overlay the coordinate display
        //coordinate sample is meant to simulate the longest possible case so movement doesn't occur when the mouse is moved

        int buttonShelfWidth = (BUTTON_SIZE * buttonCenterShelf.size()) + (BUTTON_MARGIN * (buttonCenterShelf.size() - 1));
        int buttonX = (int) ((float) (windowScaledWidth - buttonShelfWidth) / 2);
        int buttonY = windowScaledHeight - BUTTON_SIZE - BUTTON_MARGIN - attributionLayer.getHeight();
        if (coordinateInfoLayer.getRight() >= (windowScaledWidth / 2) - (buttonShelfWidth / 2)) {
            buttonY = Math.min(buttonY, coordinateInfoLayer.getY() - BUTTON_MARGIN - BUTTON_SIZE);
        }

        //calculate button positions
        for (ButtonFunction function : buttonCenterShelf.keySet()) {
            buttonPositions[0][function.id] = buttonX;
            buttonPositions[1][function.id] = buttonY;
            buttonX += BUTTON_SIZE + BUTTON_MARGIN;
        }

        //Set positions of elements
        for (ButtonFunction function : buttonCenterShelf.keySet()) { //update button positions (in case screen size has changed)
            buttonCenterShelf.get(function).setPosition(buttonPositions[0][function.id], buttonPositions[1][function.id]);
        }

        int i = 0;
        for (ButtonFunction function : buttonLeftShelf.keySet()) {
            buttonLeftShelf.get(function).setPosition(
                    BUTTON_MARGIN + (i * (BUTTON_SIZE + BUTTON_MARGIN)),
                    windowScaledHeight - BUTTON_SIZE - BUTTON_MARGIN - coordinateInfoLayer.getHeight()
            );
            i++;
        }

        bugReportLayer.setPosition(windowScaledWidth - bugReportLayer.getWidth(), attributionLayer.getY() - LINE_HEIGHT);
        toggleHudMapButtonLayer.setPosition(windowScaledWidth - (BUTTON_MARGIN + BUTTON_SIZE), bugReportLayer.getY() - BUTTON_SIZE - BUTTON_MARGIN);
        toggleClaimRenderingButtonLayer.setPosition(windowScaledWidth - 2 * (BUTTON_MARGIN + BUTTON_SIZE), bugReportLayer.getY() - BUTTON_SIZE - BUTTON_MARGIN);

        if (networkStatusLayer.shouldBeVisible()) networkStatusLayer.setX(instance.width - 26);
        else networkStatusLayer.setX(-100);
    }

    private void arrowNavigateSearch(int code) {
        int change;
        if (code == GLFW.GLFW_KEY_DOWN) change = 1;
        else if (code == GLFW.GLFW_KEY_UP) change = -1;
        else return;

        GuiEventListener[] searchElements = new GuiEventListener[SearchBoxLayer.getNumResults() + 1];
        searchElements[0] = searchBoxLayer;
        System.arraycopy(searchResultLayers, 0, searchElements, 1, SearchBoxLayer.getNumResults());

        for (int i = 0; i < searchElements.length; i++) {
            if (searchElements[i].isFocused()) {
                setFocused(searchElements[(i + change + searchElements.length) % searchElements.length]);
                SearchBoxLayer.ensureFocusDisplay(i + change);
                return;
            }
        }

    }

    public boolean searchElementsFocused() {
        return (getFocused() instanceof SearchBoxLayer || getFocused() instanceof SearchResultLayer || getFocused() instanceof SearchButtonLayer) && searchBoxLayer.visible;
    }

    public String getSearchBoxContents() {
        return searchBoxLayer.getValue();
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        if (input.input() == GLFW.GLFW_KEY_ESCAPE) {
            if (searchElementsFocused()) toggleSearchMenu(false);
            else onClose();
            return true;
        }

        if (input.input() == GLFW.GLFW_KEY_LEFT_ALT || input.input() == GLFW.GLFW_KEY_RIGHT_ALT) altKeyPressed = true;

        if (searchElementsFocused()) {
            if (input.input() == GLFW.GLFW_KEY_UP || input.input() == GLFW.GLFW_KEY_DOWN) {
                arrowNavigateSearch(input.input());
                return true;
            } else {
                if (input.input() == GLFW.GLFW_KEY_TAB) {
                    toggleSearchMenu(false);
                    return true;
                }
                return super.keyPressed(input);
            }
        }

        if (KeyInputHandler.getOpenFullscreenOsmMapKey().matches(input)) {
            this.onClose();
        }

        if (minecraft.options.keyChat.matches(input)) {
            chatToBeOpened = true;
        }

        if (input.input() == GLFW.GLFW_KEY_TAB) {
            toggleSearchMenu(true);
            return true;
        }

        map.keyNavigate(input.input(), input.modifiers());

        return true;
    }

    @Override
    public boolean keyReleased(KeyEvent input) {
        if (input.input() == GLFW.GLFW_KEY_LEFT_ALT || input.input() == GLFW.GLFW_KEY_RIGHT_ALT) altKeyPressed = false;
        return super.keyReleased(input);
    }

    public static void updateAltScreenMap(Screen previous) {
        updateAltScreenMap(previous, Minecraft.getInstance().gui.screen());
    }

    public static void updateAltScreenMap(Screen previous, Screen next) {
        if (next == null) {
            toggleAltScreenMap(false);
            return;
        }
        if (screenAlwaysHasAltMap(next)) toggleAltScreenMap(true);
        else if (screenCanHaveAltMap(next)) toggleAltScreenMap(previous instanceof MapScreen || renderAltMap);
        else toggleAltScreenMap(false);
    }

    private static boolean screenAlwaysHasAltMap(Screen screen) {
        if (screen instanceof ChatScreen) return true;
        if (screen instanceof ConfirmLinkScreen) return true;
        if (screen instanceof WaypointScreen) return true;
        return false;
    }

    private static boolean screenCanHaveAltMap(Screen screen) {
        if (screen instanceof ConfigScreen) return true;
        if (screen instanceof RasterScreen) return true;
        if (screen instanceof CreateRasterScreen) return true;
        if (screen instanceof RasterWarningScreen) return true;
        if (screen instanceof ViewSetRastersScreen) return true;
        if (screen instanceof MapConfigScreen) return true; //hard coded to not render, necessary to preserve configScreen render state
        return false;
    }

    private static void toggleAltScreenMap(boolean state) {
        if (state == renderAltMap) return;
        renderAltMap = state;
        map.setDraggable(!state);
        if (state) {
            hudWasHidden = Minecraft.getInstance().gui.hud.isHidden();
            setHudHidden(true);
        }
        else setHudHidden(hudWasHidden);
    }

    private static void setHudHidden(boolean hidden) {
        if (Minecraft.getInstance().gui.hud.isHidden() == !hidden) {
            Minecraft.getInstance().gui.hud.toggle();
        }
    }

    public static void addNotification(Notification notification) {
        notifications.addFirst(notification);
        if (notifications.size() > 10) notifications.removeLast();
    }

    private void purgeNotifiations() {
        int i = 0;
        while (i < notifications.size()) {
            if (notifications.get(i).timeToExpirationMs() < 0) {
                notifications.remove(i);
            } else {
                i++;
            }
        }
    }

    private void drawNotificationText(GuiGraphicsExtractor context) {
        if (notifications.isEmpty()) return;
        int maxY = buttonPositions[1][0] - 13; //top of button row
        int yPos = maxY;
        for (Notification notification : notifications) {
            Component text = notification.text;
            int textWidth = font.width(text);
            int centerX = windowScaledWidth / 2;
            float alphaPercent = Math.clamp((float) notification.timeToExpirationMs() / 1000, 0, 1);
            context.fill(
                    centerX - (textWidth / 2) - 3,
                    yPos - 3,
                    centerX + (textWidth / 2) + 3,
                    yPos + 1 + font.lineHeight,
                    ColorUtil.setAlpha((int) (alphaPercent * ColorUtil.decompose(backingColor)[0]), backingColor)
            );
            if (alphaPercent > 0.02) context.text( //under 0.02 makes it draw the text fully opaque for some reason
                    font,
                    text,
                    centerX - (textWidth / 2),
                    yPos,
                    ColorUtil.setAlpha((int) (alphaPercent * 255), plainTextColor),
                    false);
            yPos -= 13;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) { //called every frame
        UContext.setContext(context);
        super.extractRenderState(context, mouseX, mouseY, delta);

        if (chatToBeOpened) {
            if (minecraft.computeChatAbilities().canSendCommands()) { //copied from minecraftclient
                minecraft.gui.setScreen(new ChatScreen("", false));
                toggleAltScreenMap(true);
                chatIsOpened = true;
            }
            chatToBeOpened = false;
        }

        MapScreen.map.updateTimeRelatedVars();

        updateScreenDims(); //update screen dimension variables in case window has been resized
        PlayerAttributes.updatePlayerAttributes(minecraft);
        if (textIsRainbow) setPlainTextColor(ColorUtil.getCurrentRainbowColor(), false);

        updateWidgetPositions(font); //update the positions of button and text field widgets in case window has been resized

        map.setArtificialZoom(TileManager.doArtificialZoom);
        map.setMouseZoomStrength(TileManager.mouseZoomStrength);
        map.renderMap(context, MapType.FULLSCREEN);

        drawButtons(context);

        toggleHudMapButtonLayer.draw(context);
        if (ConfigOptions.CLAIMS_RENDERING.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF)) toggleClaimRenderingButtonLayer.draw(context);

        //draws the Mouse and player coordinates text fields
        coordinateInfoLayer.drawWidget(height);

        // -28 is for left shelf buttons
        // -23 on searchboxlayer is for the search box
        pinnedWaypointsLayer.setRoundedHeight(windowScaledHeight - coordinateInfoLayer.getHeight() - 28 - attributionOffset - pinnedWaypointsLayer.getY() - (int) (RasterProvider.doMapboxAttribution() ? getMapboxAttributionSize() * 1.5 : 0));
        SearchBoxLayer.setMaxDisplayedResults(windowScaledHeight - 32 - 28 - attributionOffset - 23 - (int) (RasterProvider.doMapboxAttribution() ? getMapboxAttributionSize() * 1.5 : 0));
        purgeNotifiations();
        drawNotificationText(context);

        if (ConfigOptions.__SHOW_MEMORY_CACHE_SIZE.getAsBoolean()) {
            Component text = Component.literal(TileLoader.getStylizedCacheSize()).withStyle(ChatFormatting.BOLD);
            int width = font.width(text);
            UContext.fillAndDrawText(text, (windowScaledWidth / 2) - (width / 2) - 3, 0, 3, 3, backingColor, plainTextColor, false);
        }

        if (altKeyPressed && ConfigOptions.__ALT_INFO_TOOLTIP.getAsBoolean()) {
            context.setComponentTooltipForNextFrame(font, map.getAtTooltipList(), mouseX, mouseY);
            context.requestCursor(CursorTypes.CROSSHAIR);
        }

        //draws the attribution and report bug text fields
        attributionLayer.drawWidget(context, this.font);
        bugReportLayer.drawWidget(context, this.font);

        networkStatusLayer.drawWidget(context);

        //draws the right click menu
        rightClickLayer.drawWidget(context, this.font);

        pinnedWaypointsLayer.drawWidget(context);

        for (SearchResultLayer layer : searchResultLayers) {
            layer.drawWidget(context, font);
        }
        searchButtonLayer.drawWidget(context);
        searchBoxLayer.drawWidget(context);

        if (RasterProvider.doMapboxAttribution()) {
            // 1.75x space for mapbox logo
            int size = getMapboxAttributionSize(); //pixel size for attribution asset
            int margin = size / 4;
            int yPos = coordinateInfoLayer.getY() - BUTTON_SIZE - 2 * BUTTON_MARGIN - size - 2 * margin;

            UContext.fillZone(
                    0,
                    yPos,
                    size * 4 + margin * 2,
                    size + 2 * margin,
                    ColorUtil.decompose(backingColor)[0] < 64 ? 0x3f000000 : backingColor
            );
            UContext.drawTexture(
                    Identifier.fromNamespaceAndPath("openminemap", "mapbox.png"),
                    margin,
                    yPos + margin,
                    size * 4,
                    size,
                    800,
                    200
            );
        }
    }

    //used in the hud to render a 'fake' fsmap screen when chat is opened
    public static void render(GuiGraphicsExtractor context, DeltaTracker renderTickCounter) {

        if (instance == null) return;
        if (!renderAltMap) return;
        if (Minecraft.getInstance().gui.screen() instanceof MapConfigScreen) return;
        if (getInstance().chatIsOpened && !(Minecraft.getInstance().gui.screen() instanceof ChatScreen)) {
            Minecraft.getInstance().gui.setScreen(new MapScreen());
            toggleAltScreenMap(false);
            getInstance().chatIsOpened = false;
        }

        MapScreen.map.updateTimeRelatedVars();

        //context.fill(map.getRenderAreaX(), map.getRenderAreaY(), map.getRenderAreaX2(), map.getRenderAreaY2(), 0x22FF0000);
        UContext.setContext(context);
        if (Minecraft.getInstance().gui.screen() instanceof ChatScreen) MapScreen.instance.extractBackground(context, 0, 0, 0);

        map.setRenderSize(
                Minecraft.getInstance().getWindow().getGuiScaledWidth(),
                Minecraft.getInstance().getWindow().getGuiScaledHeight()
        );
        map.renderMap(context, MapType.FULLSCREEN);

    }

}

