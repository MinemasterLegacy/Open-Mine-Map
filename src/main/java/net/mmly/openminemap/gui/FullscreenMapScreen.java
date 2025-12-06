package net.mmly.openminemap.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
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
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.util.*;

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
    private static final int numHotbarButtons = TileManager.doWaypoints ? 7 : 6; //determines number of buttons expected for the bottom bar of the screen
    private static int[][] buttonPositions = new int[2][numHotbarButtons];
    // modifiers used to offset the map so it can be moved relative to the screen
    // these modifiers should be scaled when the screen is zoomed in or zoomed out
    // Ex: zoom 0, range -128 - 127 | zoom 1, range -256 - 255 | zoom 2, range -512 - 511 | etc.
    MinecraftClient mClient = MinecraftClient.getInstance();
    Window window = mClient.getWindow();
    private static RightClickMenu rightClickLayer = new RightClickMenu(0, 0);
    public static WebAppSelectLayer webAppSelectLayer = new WebAppSelectLayer();
    private static AttributionLayer attributionLayer = new AttributionLayer(0, 0, 157, 16);
    private static BugReportLayer bugReportLayer = new BugReportLayer(0, 0);
    private static HashMap<ButtonFunction, ButtonLayer> buttonlayers = new HashMap<>();
    private static ToggleHudMapButtonLayer toggleHudMapButtonLayer;
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
            Integer.parseInt(ConfigFile.readParameter(ConfigOptions._FS_LAST_ZOOM)),
            Double.parseDouble(ConfigFile.readParameter(ConfigOptions._FS_LAST_X)),
            Double.parseDouble(ConfigFile.readParameter(ConfigOptions._FS_LAST_Y))
    );

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
        ConfigFile.writeParameter(ConfigOptions._FS_LAST_ZOOM, Integer.toString(map.getZoom()));
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
        map.zoomIn();
    }

    static protected void zoomOut() {
        map.zoomOut();
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

    public static void disableRightClickMenu() {
        rightClickLayer.enabled = false;
        rightClickLayer.setPosition(-500, 500);
        rightClickLayer.selectingSite = false;
    }

    public static void enableRightClickMenu(double x, double y) {
        rightClickLayer.enabled = true;
        rightClickLayer.selectingSite = false;
        rightClickLayer.clickX = x;
        rightClickLayer.clickY = y;
        rightClickLayer.setPosition((int) x, (int) y);
        rightClickLayer.setSavedMouseLatLong(map.getMouseLong(), map.getMouseLat());
        //System.out.println(mouseLong);
        //System.out.println(mouseLat);
        //System.out.println(rightClickLayer.getX());
        //System.out.println(rightClickLayer.getWidth());
        //System.out.println(windowScaledWidth);
        if (windowScaledWidth > rightClickLayer.getWidth() && rightClickLayer.getX() + rightClickLayer.getWidth() > windowScaledWidth) {
            rightClickLayer.setX(rightClickLayer.getX() - rightClickLayer.getWidth() + 1);
            rightClickLayer.horizontalSide = -1;
        } else rightClickLayer.horizontalSide = 1;
        if (windowScaledHeight > rightClickLayer.getHeight() && rightClickLayer.getY() + rightClickLayer.getHeight() > windowScaledHeight) {
            rightClickLayer.setY(rightClickLayer.getY() - rightClickLayer.getHeight() + 1);
            rightClickLayer.verticalSize = -1;
        } else rightClickLayer.verticalSize = 1;
    }

    private static Identifier getButtonTexture(ButtonFunction buttonFunction, ButtonState buttonState) {
        return buttonIdentifiers[buttonState.ordinal()][buttonFunction.ordinal()];
    }

    private static void onLeftClick() {
        FullscreenMapScreen.disableRightClickMenu();
    }

    private static void onRightClick() {
        if (!map.mouseIsOutOfBounds()) { //checks if mouse is positioned on the map (this variable will be "-.-" if it isn't)
            FullscreenMapScreen.enableRightClickMenu(map.getMouseX(), map.getMouseY());
        } else {
            FullscreenMapScreen.disableRightClickMenu();
        }
    }

    private static boolean blockZoomOnZoom() {
        return rightClickLayer.enabled;
    }

    @Override
    protected void init() { //called when screen is being initialized
        instance = this;

        for (int i = 0; i < numHotbarButtons; i++) {
            buttonlayers.put(ButtonFunction.getEnumOf(i), new ButtonLayer(0, 0, buttonSize, buttonSize, ButtonFunction.getEnumOf(i)));
            this.addDrawableChild(buttonlayers.get(ButtonFunction.getEnumOf(i)));
        }

        toggleHudMapButtonLayer = new ToggleHudMapButtonLayer(windowScaledWidth - 25, windowScaledHeight - 57);
        this.addDrawableChild(toggleHudMapButtonLayer);

        rightClickLayer = new RightClickMenu(0, 0);
        this.addDrawableChild(rightClickLayer);
        webAppSelectLayer = new WebAppSelectLayer();
        this.addDrawableChild(webAppSelectLayer);

        attributionLayer = new AttributionLayer(windowScaledWidth - 157, windowScaledHeight - 16, 157, 16);
        this.addDrawableChild(attributionLayer); //windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight,
        bugReportLayer = new BugReportLayer(windowScaledWidth - 157, windowScaledHeight - 32);
        this.addDrawableChild(bugReportLayer); //windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight,

        TileManager.initializeConfigParameters();

        updateTileSet();

        this.addDrawableChild(map); //added last so it's checked last for clicking

        map.setDraggable(true);
        map.rightClickProcedure = FullscreenMapScreen::onRightClick;
        map.leftClickProcedure = FullscreenMapScreen::onLeftClick;
        map.blockZoomProcedure = FullscreenMapScreen::blockZoomOnZoom;

    }

    private static void drawButtons(DrawContext context) {
        //draws the buttons
        context.drawTexture( //zoom in
                map.getZoom() < (TileManager.doArtificialZoom ? OmmMap.TILEMAXARTIFICIALZOOM : OmmMap.TILEMAXZOOM) ?
                        (buttonlayers.get(ButtonFunction.ZOOMIN).isHovered() ?
                                getButtonTexture(ButtonFunction.ZOOMIN, ButtonState.HOVER) :
                                getButtonTexture(ButtonFunction.ZOOMIN, ButtonState.DEFAULT)) :
                        getButtonTexture(ButtonFunction.ZOOMIN, ButtonState.LOCKED),
                buttonPositions[0][0], buttonPositions[1][0], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture( //zoom out
                map.getZoom() > 0 ?
                        (buttonlayers.get(ButtonFunction.ZOOMOUT).isHovered() ?
                                getButtonTexture(ButtonFunction.ZOOMOUT, ButtonState.HOVER) :
                                getButtonTexture(ButtonFunction.ZOOMOUT, ButtonState.DEFAULT)) :
                        getButtonTexture(ButtonFunction.ZOOMOUT, ButtonState.LOCKED),
                buttonPositions[0][1], buttonPositions[1][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture( //reset map
                buttonlayers.get(ButtonFunction.RESET).isHovered() ?
                        getButtonTexture(ButtonFunction.RESET, ButtonState.HOVER) :
                        getButtonTexture(ButtonFunction.RESET, ButtonState.DEFAULT),
                buttonPositions[0][2], buttonPositions[1][2], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture( //follow player
                !PlayerAttributes.positionIsValid() || map.followingPlayer() ?
                        getButtonTexture(ButtonFunction.FOLLOW, ButtonState.LOCKED) :
                        (buttonlayers.get(ButtonFunction.FOLLOW).isHovered() ?
                                getButtonTexture(ButtonFunction.FOLLOW, ButtonState.HOVER) :
                                getButtonTexture(ButtonFunction.FOLLOW, ButtonState.DEFAULT)),
                buttonPositions[0][3], buttonPositions[1][3], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture( //config
                buttonlayers.get(ButtonFunction.CONFIG).isHovered() ?
                        getButtonTexture(ButtonFunction.CONFIG, ButtonState.HOVER) :
                        getButtonTexture(ButtonFunction.CONFIG, ButtonState.DEFAULT),
                buttonPositions[0][4], buttonPositions[1][4], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture( //exit
                buttonlayers.get(ButtonFunction.EXIT).isHovered() ?
                        getButtonTexture(ButtonFunction.EXIT, ButtonState.HOVER) :
                        getButtonTexture(ButtonFunction.EXIT, ButtonState.DEFAULT),
                buttonPositions[0][5], buttonPositions[1][5], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        if (TileManager.doWaypoints) {
            context.drawTexture( //waypoints
                    buttonlayers.get(ButtonFunction.WAYPOINTS).isHovered() ?
                            getButtonTexture(ButtonFunction.WAYPOINTS, ButtonState.HOVER) :
                            getButtonTexture(ButtonFunction.WAYPOINTS, ButtonState.DEFAULT),
                    buttonPositions[0][6], buttonPositions[1][6], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        }
    }

    private static void updateWidgetPositions(TextRenderer textRenderer) {
        //if attribution would overlay the coordinate display
        //coordinate sample is meant to simulate the longest possible case so movement doesn't occur when the mouse is moved
        if (attributionLayer.getWidth() + textRenderer.getWidth("Mouse: -99.99999°, -999.99999°") + 8 > windowScaledWidth) {
            attributionOffset = attributionLayer.getHeight();
        } else {
            attributionOffset = 0;
        }

        int buttonShelfWidth = (buttonSize * buttonlayers.size()) + (buttonMargin * (buttonlayers.size() - 1));
        int shelfX = (int) ((float) (windowScaledWidth - buttonShelfWidth) / 2);
        int buttonX = shelfX;
        int buttonY = windowScaledHeight - (buttonSize + 20);

        if (textRenderer.getWidth("Player: -99.99999°, -999.99999°") + 8 > shelfX) {
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
        map.keyNavigate(keyCode, modifiers);
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) { //called every frame
        super.render(context, mouseX, mouseY, delta);

        updateScreenDims(); //update screen dimension variables in case window has been resized

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
        }

        updateWidgetPositions(textRenderer); //update the positions of button and text field widgets in case window has been resized

        map.setArtificialZoom(TileManager.doArtificialZoom);
        map.renderMap(context, null);

        PlayerAttributes.updatePlayerAttributes(mClient);

        drawButtons(context);

        int buttonStyle = HudMap.hudEnabled ? 1 : 0;
        context.drawTexture(toggleHudMapButtonLayer.isHovered() ? showIdentifiers[1][buttonStyle] : showIdentifiers[0][buttonStyle], toggleHudMapButtonLayer.getX(), toggleHudMapButtonLayer.getY(), 0, 0, 20, 20, 20, 20);

        //draws the Mouse and player coordinates text fields
        context.fill(0, windowScaledHeight - 16 - attributionOffset, 53 + (mouseDisplayLong.length() * 6) + (mouseDisplayLat.length() * 6), windowScaledHeight - attributionOffset, 0x88000000);
        context.drawText(this.textRenderer, "Mouse: " + mouseDisplayLat + "°, " + mouseDisplayLong + "°", 4, windowScaledHeight + 7 - this.textRenderer.fontHeight - 10 - attributionOffset, 0xFFFFFFFF, true);
        context.fill(0, windowScaledHeight - 32 - attributionOffset, 55 + (playerDisplayLon.length() * 6) + (playerDisplayLat.length() * 6), windowScaledHeight - 16 - attributionOffset, 0x88000000);
        context.drawText(this.textRenderer, "Player: " + playerDisplayLat + "°, " + playerDisplayLon + "°", 4, windowScaledHeight + 7  - this.textRenderer.fontHeight - 10 - 16 - attributionOffset, 0xFFFFFFFF, true);

        //draws the attribution and report bug text fields
        attributionLayer.drawWidget(context, this.textRenderer);
        bugReportLayer.drawWidget(context, this.textRenderer);

        //draws the right click menu
        rightClickLayer.drawWidget(context, this.textRenderer);
        webAppSelectLayer.drawWidget(context);


    }

}

