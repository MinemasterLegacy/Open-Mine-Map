package net.mmly.openminemap.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ButtonState;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.UnitConvert;
import net.mmly.openminemap.util.Waypoint;

import java.util.HashMap;

public class FullscreenMapScreen extends Screen { //Screen object that represents the fullscreen map
    public FullscreenMapScreen() {
        super(Text.of("OMM Fullscreen Map"));
    }

    public static int windowHeight;
    public static int windowWidth;
    public static int windowScaledHeight;
    public static int windowScaledWidth;
    protected static String mouseDisplayLong = "0.00000";
    protected static String mouseDisplayLat = "0.00000";
    private static final int buttonSize = 20;
    private static final int buttonMargin = 4;
    private static final int numHotbarButtons = 7; //determines number of buttons expected for the bottom bar of the screen
    private static int[][] buttonPositions = new int[2][numHotbarButtons];
    // modifiers used to offset the map so it can be moved relative to the screen
    // these modifiers should be scaled when the screen is zoomed in or zoomed out
    // Ex: zoom 0, range -128 - 127 | zoom 1, range -256 - 255 | zoom 2, range -512 - 511 | etc.
    MinecraftClient mClient = MinecraftClient.getInstance();
    Window window = mClient.getWindow();
    private static RightClickMenu rightClickLayer;
    public static WebAppSelectLayer webAppSelectLayer = new WebAppSelectLayer();
    private static AttributionLayer attributionLayer = new AttributionLayer(0, 0, 157, 16);
    private static BugReportLayer bugReportLayer = new BugReportLayer(0, 0);
    private static HashMap<ButtonFunction, ButtonLayer> buttonlayers = new HashMap<>();
    private static ToggleHudMapButtonLayer toggleHudMapButtonLayer;
    private static PinnedWaypointsLayer pinnedWaypointsLayer;
    private static final Identifier[][] buttonIdentifiers = new Identifier[3][numHotbarButtons];
    private static final Identifier[][] showIdentifiers = new Identifier[2][2];
    String playerDisplayLon = "0.00000";
    String playerDisplayLat = "0.00000";
    static FullscreenMapScreen instance;
    private static int attributionOffset = 0;
    public static final OmmMap map = new OmmMap(
            0,
            0,
            640,
            480,
            Double.parseDouble(ConfigFile.readParameter(ConfigOptions._FS_LAST_ZOOM)),
            Double.parseDouble(ConfigFile.readParameter(ConfigOptions._FS_LAST_X)),
            Double.parseDouble(ConfigFile.readParameter(ConfigOptions._FS_LAST_Y))
    );
    public static boolean renderWithChat = false;
    private boolean chatToBeOpened = false;
    private static boolean hudWasHidden = false;

    public static void clampZoom() {
        //used to decrease zoom level (if needed) when artificial zoom is disabled
        map.clampZoom();
    }

    public static void followPlayer() {
        map.setFollowPlayer(true);
    }

    public static FullscreenMapScreen getInstance() {
        return instance;
    }

    @Override
    public void close() {
        disableRightClickMenu();
        ConfigFile.writeParameter(ConfigOptions._FS_LAST_ZOOM, Double.toString(map.getZoom()));
        ConfigFile.writeParameter(ConfigOptions._FS_LAST_X, Double.toString(map.getMapCenterX()));
        ConfigFile.writeParameter(ConfigOptions._FS_LAST_Y, Double.toString(map.getMapCenterY()));
        ConfigFile.writeToFile();
        this.client.setScreen(null);
    }

    private void updateScreenDims() {
        windowHeight = window.getHeight();
        windowWidth = window.getWidth();
        windowScaledHeight = window.getScaledHeight();
        windowScaledWidth = window.getScaledWidth();
        map.setRenderSize(windowScaledWidth, windowScaledHeight);
    }

    static protected void updateTileSet() {
        String path;
        String[] names = new String[] {"zoomin.png", "zoomout.png", "reset.png", "follow.png", "config.png", "exit.png", "waypoint.png"};
        String[] states = new String[] {"locked/", "default/", "hover/"};
        path = "buttons/vanilla/";

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < numHotbarButtons; j++) {
                buttonIdentifiers[i][j] = Identifier.of("openminemap", path + states[i] + names[j]);
            }
        }
        names = new String[] {"mapoff.png", "mapon.png"};
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                showIdentifiers[i][j] = Identifier.of("openminemap", path + states[i + 1] + names[j]);
            }
        }
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

    public static void openLinkScreen(String link, Screen returnScreen) {
        MinecraftClient.getInstance().setScreen(
                new ConfirmLinkScreen(new BooleanConsumer() {
                    @Override
                    public void accept(boolean b) {
                        if(b) {
                            Util.getOperatingSystem().open(link);
                        }
                        MinecraftClient.getInstance().setScreen(returnScreen);
                    }

                }, link, true)

        );
    }

    public static RightClickMenuType getRightClickMenuType() {
        return rightClickLayer.getDisplayType();
    }

    public static Waypoint getRightClickMenuWaypoint() {return rightClickLayer.selectedWaypoint;}

    public static void disableRightClickMenu() {
        rightClickLayer.setDisplayType(RightClickMenuType.HIDDEN);
        rightClickLayer.setPosition(-500, 500);
        rightClickLayer.selectingSite = false;
        PinnedWaypointsLayer.menuSelection = -1;
    }

    public static Waypoint getSelectedPinnedWaypoint() {
        return pinnedWaypointsLayer.getSelectedWaypoint();
    }

    public static void enableRightClickMenu(double x, double y, RightClickMenuType type) {
        if (type == RightClickMenuType.HIDDEN) {
            disableRightClickMenu();
            return;
        }
        PinnedWaypointsLayer.menuSelection = -1;
        rightClickLayer.setDisplayType(type);
        RightClickMenu.selectingSite = false;
        rightClickLayer.clickX = x;
        rightClickLayer.clickY = y;
        rightClickLayer.setPosition((int) x, (int) y);
        if (type == RightClickMenuType.PINNED_WAYPOINT) {
            rightClickLayer.setSavedMouseLatLong(pinnedWaypointsLayer.getSelectedWaypoint().longitude, pinnedWaypointsLayer.getSelectedWaypoint().latitude);
        } else {
            rightClickLayer.setSavedMouseLatLong(map.getMouseLong(), map.getMouseLat());
            if (windowScaledWidth > rightClickLayer.getWidth() && rightClickLayer.getX() + rightClickLayer.getWidth() > windowScaledWidth) {
                rightClickLayer.setX(rightClickLayer.getX() - rightClickLayer.getWidth() + 1);
                rightClickLayer.horizontalSide = -1;
            } else rightClickLayer.horizontalSide = 1;
            if (windowScaledHeight > rightClickLayer.getHeight() && rightClickLayer.getY() + rightClickLayer.getHeight() > windowScaledHeight) {
                rightClickLayer.setY(rightClickLayer.getY() - rightClickLayer.getHeight() + 1);
                rightClickLayer.verticalSize = -1;
            } else rightClickLayer.verticalSize = 1;
        }

    }

    private static Identifier getButtonTexture(ButtonFunction buttonFunction, ButtonState buttonState) {
        return buttonIdentifiers[buttonState.ordinal()][buttonFunction.ordinal()];
    }

    private static void onLeftClick() {
        disableRightClickMenu();
    }

    private static void onRightClick() {
        if (!map.mouseIsOutOfBounds()) { //checks if mouse is positioned on the map (this variable will be "-.-" if it isn't)
            if (map.getHoveredWaypoint() != null) enableRightClickMenu(map.getMouseX(), map.getMouseY(), RightClickMenuType.WAYPOINT);
            else enableRightClickMenu(map.getMouseX(), map.getMouseY(), RightClickMenuType.DEFAULT);
        } else {
            disableRightClickMenu();
        }
    }

    private static boolean blockZoomOnZoom() {
        return rightClickLayer.getDisplayType() != RightClickMenuType.HIDDEN;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        map.setMouseDown(false);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    protected void init() { //called when screen is being initialized
        instance = this;

        rightClickLayer = new RightClickMenu(-500, -500, this.textRenderer);
        this.addDrawableChild(rightClickLayer);

        for (int i = 0; i < numHotbarButtons; i++) {
            buttonlayers.put(ButtonFunction.getEnumOf(i), new ButtonLayer(0, 0, buttonSize, buttonSize, ButtonFunction.getEnumOf(i)));
            this.addDrawableChild(buttonlayers.get(ButtonFunction.getEnumOf(i)));
        }

        toggleHudMapButtonLayer = new ToggleHudMapButtonLayer(windowScaledWidth - 25, windowScaledHeight - 57);
        this.addDrawableChild(toggleHudMapButtonLayer);

        webAppSelectLayer = new WebAppSelectLayer();
        this.addDrawableChild(webAppSelectLayer);

        attributionLayer = new AttributionLayer(windowScaledWidth - 157, windowScaledHeight - 16, 157, 16);
        this.addDrawableChild(attributionLayer); //windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight,
        bugReportLayer = new BugReportLayer(windowScaledWidth - 157, windowScaledHeight - 32);
        this.addDrawableChild(bugReportLayer); //windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight,

        TileManager.initializeConfigParameters();

        updateTileSet();

        pinnedWaypointsLayer = new PinnedWaypointsLayer(0, 0, 20, 2, this.textRenderer);
        this.addDrawableChild(pinnedWaypointsLayer);

        this.addDrawableChild(map); //added last so it's checked last for clicking

        map.setDraggable(true);
        map.rightClickProcedure = FullscreenMapScreen::onRightClick;
        map.leftClickProcedure = FullscreenMapScreen::onLeftClick;
        map.blockZoomProcedure = FullscreenMapScreen::blockZoomOnZoom;
        map.waypointClickedProcedure = FullscreenMapScreen::onRightClick;
        map.setTextRenderer(this.textRenderer);
        map.doPlayerTooltipNames(true);

    }

    private static void drawButtons(DrawContext context) {
        //draws the buttons
        context.drawTexture(RenderLayer::getGuiTextured, //zoom in
                map.getZoom() < map.getMaxZoom() ?
                        (buttonlayers.get(ButtonFunction.ZOOMIN).isHovered() ?
                                getButtonTexture(ButtonFunction.ZOOMIN, ButtonState.HOVER) :
                                getButtonTexture(ButtonFunction.ZOOMIN, ButtonState.DEFAULT)) :
                        getButtonTexture(ButtonFunction.ZOOMIN, ButtonState.LOCKED),
                buttonPositions[0][0], buttonPositions[1][0], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderLayer::getGuiTextured, //zoom out
                map.getZoom() > 0 ?
                        (buttonlayers.get(ButtonFunction.ZOOMOUT).isHovered() ?
                                getButtonTexture(ButtonFunction.ZOOMOUT, ButtonState.HOVER) :
                                getButtonTexture(ButtonFunction.ZOOMOUT, ButtonState.DEFAULT)) :
                        getButtonTexture(ButtonFunction.ZOOMOUT, ButtonState.LOCKED),
                buttonPositions[0][1], buttonPositions[1][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderLayer::getGuiTextured, //reset map
                buttonlayers.get(ButtonFunction.RESET).isHovered() ?
                        getButtonTexture(ButtonFunction.RESET, ButtonState.HOVER) :
                        getButtonTexture(ButtonFunction.RESET, ButtonState.DEFAULT),
                buttonPositions[0][2], buttonPositions[1][2], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderLayer::getGuiTextured, //follow player
                !PlayerAttributes.positionIsValid() || map.followingPlayer() ?
                        getButtonTexture(ButtonFunction.FOLLOW, ButtonState.LOCKED) :
                        (buttonlayers.get(ButtonFunction.FOLLOW).isHovered() ?
                                getButtonTexture(ButtonFunction.FOLLOW, ButtonState.HOVER) :
                                getButtonTexture(ButtonFunction.FOLLOW, ButtonState.DEFAULT)),
                buttonPositions[0][3], buttonPositions[1][3], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderLayer::getGuiTextured, //config
                buttonlayers.get(ButtonFunction.CONFIG).isHovered() ?
                        getButtonTexture(ButtonFunction.CONFIG, ButtonState.HOVER) :
                        getButtonTexture(ButtonFunction.CONFIG, ButtonState.DEFAULT),
                buttonPositions[0][4], buttonPositions[1][4], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderLayer::getGuiTextured, //exit
                buttonlayers.get(ButtonFunction.EXIT).isHovered() ?
                        getButtonTexture(ButtonFunction.EXIT, ButtonState.HOVER) :
                        getButtonTexture(ButtonFunction.EXIT, ButtonState.DEFAULT),
                buttonPositions[0][5], buttonPositions[1][5], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderLayer::getGuiTextured, //waypoints
                buttonlayers.get(ButtonFunction.WAYPOINTS).isHovered() ?
                        getButtonTexture(ButtonFunction.WAYPOINTS, ButtonState.HOVER) :
                        getButtonTexture(ButtonFunction.WAYPOINTS, ButtonState.DEFAULT),
                buttonPositions[0][6], buttonPositions[1][6], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
    }

    private static void updateWidgetPositions(TextRenderer textRenderer) {
        //if attribution would overlay the coordinate display
        //coordinate sample is meant to simulate the longest possible case so movement doesn't occur when the mouse is moved
        if (attributionLayer.getWidth() + textRenderer.getWidth(Text.translatable("omm.fullscreen.mouse-coordinates-label").getString() + "-99.99999°, -999.99999°") + 8 > windowScaledWidth) {
            attributionOffset = attributionLayer.getHeight();
        } else {
            attributionOffset = 0;
        }

        int buttonShelfWidth = (buttonSize * buttonlayers.size()) + (buttonMargin * (buttonlayers.size() - 1));
        int shelfX = (int) ((float) (windowScaledWidth - buttonShelfWidth) / 2);
        int buttonX = shelfX;
        int buttonY = windowScaledHeight - (buttonSize + 20);

        if (textRenderer.getWidth(Text.translatable("omm.fullscreen.player-coordinates-label").getString() + "-99.99999°, -999.99999°") + 8 > shelfX) {
            buttonY -= attributionOffset != 0 ? 32 : 16;
        }

        for (int i = 0; i < buttonlayers.size(); i++) { //calculate button positions
            buttonPositions[0][i] = buttonX;
            buttonPositions[1][i] = buttonY;
            buttonX += buttonSize + buttonMargin;
        }

        //Set positions of elements
        for (int i = 0; i < numHotbarButtons; i++) { //update button positions (in case screen size has changed)
            buttonlayers.get(ButtonFunction.getEnumOf(i)).setPosition(buttonPositions[0][i], buttonPositions[1][i]);
        }

        toggleHudMapButtonLayer.setPosition(windowScaledWidth - 25, windowScaledHeight - 57);
        attributionLayer.setDimensionsAndPosition(attributionLayer.textWidth + 10,  16, windowScaledWidth - attributionLayer.textWidth - 10, windowScaledHeight - 16);
        bugReportLayer.setPosition(windowScaledWidth - bugReportLayer.getWidth(), windowScaledHeight - 32);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.shouldCloseOnEsc()) {
            this.close();
            return true;
        }

        if (mClient.options.chatKey.matchesKey(keyCode, 0)) {
            chatToBeOpened = true;
        }

        map.keyNavigate(keyCode, modifiers);
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) { //called every frame
        super.render(context, mouseX, mouseY, delta);

        if (chatToBeOpened) {
            if (mClient.getChatRestriction().allowsChat(mClient.isInSingleplayer())) { //copied from minecraftclient
                renderWithChat = true;
                mClient.setScreen(new ChatScreen(""));
                map.setDraggable(false);
                hudWasHidden = MinecraftClient.getInstance().options.hudHidden;
                MinecraftClient.getInstance().options.hudHidden = false;
            }
            chatToBeOpened = false;
        }

        updateScreenDims(); //update screen dimension variables in case window has been resized
        PlayerAttributes.updatePlayerAttributes(mClient);

        if (map.mouseIsOutOfBounds()) {
            mouseDisplayLat = "-.-";
            mouseDisplayLong = "-.-";
        } else {
            mouseDisplayLong = UnitConvert.floorToPlace(map.getMouseLong(), 5);
            mouseDisplayLat = UnitConvert.floorToPlace(map.getMouseLat(), 5);
        }

        if (PlayerAttributes.positionIsValid()) {
            playerDisplayLon = UnitConvert.floorToPlace(PlayerAttributes.getLongitude(), 5);
            playerDisplayLat = UnitConvert.floorToPlace(PlayerAttributes.getLatitude(), 5);
        } else {
            playerDisplayLon = "-.-";
            playerDisplayLat = "-.-";
            map.setFollowPlayer(false);
        }

        updateWidgetPositions(textRenderer); //update the positions of button and text field widgets in case window has been resized

        map.setArtificialZoom(TileManager.doArtificialZoom);
        map.setMouseZoomStrength(TileManager.mouseZoomStrength);
        map.renderMap(context, null);

        drawButtons(context);

        int buttonStyle = HudMap.hudEnabled ? 1 : 0;
        context.drawTexture(RenderLayer::getGuiTextured, toggleHudMapButtonLayer.isHovered() ? showIdentifiers[1][buttonStyle] : showIdentifiers[0][buttonStyle], toggleHudMapButtonLayer.getX(), toggleHudMapButtonLayer.getY(), 0, 0, 20, 20, 20, 20);

        //draws the Mouse and player coordinates text fields
        String mouseLabelText = Text.translatable("omm.fullscreen.mouse-coordinates-label").getString() + mouseDisplayLat + "°, " + mouseDisplayLong + "°";
        String playerLabelText = Text.translatable("omm.fullscreen.player-coordinates-label").getString() + playerDisplayLat + "°, " + playerDisplayLon + "°";
        context.fill(0, windowScaledHeight - 16 - attributionOffset, 8 + textRenderer.getWidth(mouseLabelText), windowScaledHeight - attributionOffset, 0x88000000);
        context.drawText(this.textRenderer, mouseLabelText, 4, windowScaledHeight + 7 - this.textRenderer.fontHeight - 10 - attributionOffset, 0xFFFFFFFF, true);
        context.fill(0, windowScaledHeight - 32 - attributionOffset,  8 + textRenderer.getWidth(playerLabelText), windowScaledHeight - 16 - attributionOffset, 0x88000000);
        context.drawText(this.textRenderer, playerLabelText, 4, windowScaledHeight + 7  - this.textRenderer.fontHeight - 10 - 16 - attributionOffset, 0xFFFFFFFF, true);

        pinnedWaypointsLayer.setRoundedHeight(windowScaledHeight - 32 - attributionOffset);

        //draws the attribution and report bug text fields
        attributionLayer.drawWidget(context, this.textRenderer);
        bugReportLayer.drawWidget(context, this.textRenderer);

        //draws the right click menu
        rightClickLayer.drawWidget(context, this.textRenderer);
        webAppSelectLayer.drawWidget(context);

        pinnedWaypointsLayer.drawWidget(context);

    }

    //used in the hud to render a 'fake' fsmap screen when chat is opened
    public static void render(DrawContext context, RenderTickCounter renderTickCounter) {

        if (!renderWithChat) return;

        if (!(MinecraftClient.getInstance().currentScreen instanceof ChatScreen)) {
            renderWithChat = false;
            MinecraftClient.getInstance().setScreen(
                    new FullscreenMapScreen()
            );
            map.setDraggable(true);
            MinecraftClient.getInstance().options.hudHidden = FullscreenMapScreen.hudWasHidden;
            return;
        }

        //context.fill(map.getRenderAreaX(), map.getRenderAreaY(), map.getRenderAreaX2(), map.getRenderAreaY2(), 0x22FF0000);

        FullscreenMapScreen.instance.renderBackground(context, 0, 0, 0);

        map.setRenderSize(
                MinecraftClient.getInstance().getWindow().getScaledWidth(),
                MinecraftClient.getInstance().getWindow().getScaledHeight()
        );
        map.renderMap(context, null);

    }

}

