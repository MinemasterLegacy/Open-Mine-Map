package net.mmly.openminemap.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ButtonState;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.OverlayVisibility;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Direction;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class FullscreenMapScreen extends Screen { //Screen object that represents the fullscreen map
    public FullscreenMapScreen() {
        super(Text.of("OMM Fullscreen Map"));
    }

    public static int trueZoomLevel = Integer.parseInt(ConfigFile.readParameter(ConfigOptions._FS_LAST_ZOOM)); //indicates the 'actual' zoom level, which includes artificial zoom | range 0-24 inclusive
    public static int zoomLevel = Math.min(trueZoomLevel, 18); //indicates the current zoom level of the map | default 0 | range 0-18 inclusive
    Identifier[][] identifiers;
    public static int windowHeight;
    public static int windowWidth;
    public static int windowScaledHeight;
    public static int windowScaledWidth;
    public static double mouseLong = 0;
    public static double mouseLat = 0;
    protected static String mouseDisplayLong = "0.00000";
    protected static String mouseDisplayLat = "0.00000";
    protected static final int buttonSize = 20;
    private static final int numHotbarButtons = 6; //determines number of buttons expected for the bottom bar of the screen
    protected static final int[][] buttonPositionModifiers = new int[][] {
        {-70,(8 + buttonSize + 12)},
        {-46,(8 + buttonSize + 12)},
        {-22,(8 + buttonSize + 12)},
        {2,(8 + buttonSize + 12)},
        {26,(8 + buttonSize + 12)},
        {50,(8 + buttonSize + 12)}
    };
    //protected static Identifier waypointIdentifiers[];
    // mouse x, y and down of previous frame, recorded here specifically so that changes in mousexy while left mouse is clicked can be detected.
    int lastMouseX = 0;
    int lastMouseY = 0;
    protected static boolean lastMouseDown = false;
    protected static double mouseTilePosX = 64;
    protected static double mouseTilePosY = 64;
    // modifiers used to offset the map so it can be moved relative to the screen
    // these modifiers should be scaled when the screen is zoomed in or zoomed out
    // Ex: zoom 0, range -128 - 127 | zoom 1, range -256 - 255 | zoom 2, range -512 - 511 | etc.
    static double mapTilePosX = Double.parseDouble(ConfigFile.readParameter(ConfigOptions._FS_LAST_X)); //64 by default
    static double mapTilePosY = Double.parseDouble(ConfigFile.readParameter(ConfigOptions._FS_LAST_Y)); //64 by default
    MinecraftClient mClient = MinecraftClient.getInstance();
    Window window = mClient.getWindow();
    private static RightClickMenu rightClickLayer = new RightClickMenu(0, 0);
    public static WebAppSelectLayer webAppSelectLayer = new WebAppSelectLayer();
    private static AttributionLayer attributionLayer = new AttributionLayer(0, 0, 157, 16);
    private static BugReportLayer bugReportLayer = new BugReportLayer(0, 0);
    private static HashMap<ButtonFunction, ButtonLayer> buttonlayers = new HashMap<>();
    private static ToggleHudMapButtonLayer toggleHudMapButtonLayer;
    private static final Identifier[][] buttonIdentifiers = new Identifier[3][6];
    private static final Identifier[][] showIdentifiers = new Identifier[2][2];
    private Identifier playerIdentifier;
    protected static int playerMapX;
    protected static int playerMapY;
    public static double playerLon;
    public static double playerLat;
    int playerGetCoordsDelay = 20;
    String playerDisplayLon = "0.00000";
    String playerDisplayLat = "0.00000";
    public static boolean doFollowPlayer;
    public static int renderTileSize;
    DirectionIndicator directionIndicator;
    static FullscreenMapScreen instance;

    public static void clampZoom() {
        //used to decrease zoom level (if needed) when artificial zoom is disabled
        while (trueZoomLevel > 18) {
            zoomOut();
        }
    }

    public static void followPlayer() {
        if (!Double.isNaN(playerLon)) {
            doFollowPlayer = true;
        }
    }

    public static FullscreenMapScreen getInstance() {
        return instance;
    }

    @Override
    public void close() {
        disableRightClickMenu();
        ConfigFile.writeParameter(ConfigOptions._FS_LAST_ZOOM, Integer.toString(trueZoomLevel));
        ConfigFile.writeParameter(ConfigOptions._FS_LAST_X, Double.toString(mapTilePosX));
        ConfigFile.writeParameter(ConfigOptions._FS_LAST_Y, Double.toString(mapTilePosY));
        ConfigFile.writeToFile();
        this.client.setScreen(null);
    }

    private void updateScreenDims() {
        windowHeight = window.getHeight();
        windowWidth = window.getWidth();
        windowScaledHeight = window.getScaledHeight();
        windowScaledWidth = window.getScaledWidth();
    }

    static protected void updateTileSet() {
        String path;
        String[] names = new String[] {"zoomin.png", "zoomout.png", "reset.png", "follow.png", "config.png", "exit.png"};
        String[] states = new String[] {"locked/", "default/", "hover/"};
        path = "buttons/vanilla/";

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
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

    static protected void mouseZoomIn() {
        if (rightClickLayer.enabled) return;
        FullscreenMapScreen.getInstance().updateTilePositions();
        if ((!TileManager.doArtificialZoom && zoomLevel >= 18) || (TileManager.doArtificialZoom && trueZoomLevel >= 24)) return;
        if (!doFollowPlayer) {
            mapTilePosX -= (mapTilePosX - mouseTilePosX) / 2;
            mapTilePosY -= (mapTilePosY - mouseTilePosY) / 2;
        }
        zoomIn();
    }

    static protected void mouseZoomOut() {
        if (rightClickLayer.enabled) return;
        FullscreenMapScreen.getInstance().updateTilePositions();
        if (zoomLevel <= 0) return;
        if (!doFollowPlayer) {
            mapTilePosX += (mapTilePosX - mouseTilePosX);
            mapTilePosY += (mapTilePosY - mouseTilePosY);
        }
        zoomOut();
    }

    static protected void zoomIn() {
        if (TileManager.doArtificialZoom) {
            if (trueZoomLevel < 24) {
                trueZoomLevel++;
                zoomLevel = Math.min(18, trueZoomLevel);
                mapTilePosX *= 2;
                mapTilePosY *= 2;
            } else {
                zoomLevel = 18;
                trueZoomLevel = 24;
            }
        } else {
            if (trueZoomLevel < 18) {
                zoomLevel++;
                trueZoomLevel++;
                mapTilePosX *= 2;
                mapTilePosY *= 2;
            } else {
                zoomLevel = 18;
                trueZoomLevel = 18;
            }
        }

    }

    static protected void zoomOut() {
        if (TileManager.doArtificialZoom) {
            if (trueZoomLevel > 0) {
                trueZoomLevel--;
                zoomLevel = Math.min(18, trueZoomLevel);
                mapTilePosX = (float) mapTilePosX / 2;
                mapTilePosY = (float) mapTilePosY / 2;
            } else {
                zoomLevel = 0;
                trueZoomLevel = 0;
            }
        } else {
            if (trueZoomLevel > 0) {
                zoomLevel--;
                trueZoomLevel--;
                mapTilePosX = (float) mapTilePosX / 2;
                mapTilePosY = (float) mapTilePosY / 2;
            } else {
                zoomLevel = 0;
                trueZoomLevel = 0;
            }
        }

    }

    static protected void resetMap() {
        doFollowPlayer = false;
        mapTilePosX = 64; //(((double) TileManager.tileScaledSize /2) * Math.pow(2, zoomLevel));
        mapTilePosY = 64; //(((double) TileManager.tileScaledSize /2) * Math.pow(2, zoomLevel));
        zoomLevel = 0;
        trueZoomLevel = 0;
    }

    public static boolean mouseIsOutOfBounds() {
        return mouseTilePosX < 0 || mouseTilePosY < 0 || mouseTilePosX > Math.pow(2, trueZoomLevel + 7) || mouseTilePosY > Math.pow(2, trueZoomLevel + 7);
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

    protected static void openOSMAttrScreen() {
        MinecraftClient.getInstance().setScreen(
                new ConfirmLinkScreen(new BooleanConsumer() {
                    @Override
                    public void accept(boolean b) {
                        if(b) {
                            Util.getOperatingSystem().open(TileUrlFile.getCurrentUrl().attribution_links[0]);
                        }
                        MinecraftClient.getInstance().setScreen(new FullscreenMapScreen());
                    }

                }, TileUrlFile.getCurrentUrl().attribution_links[0], true)

        );
    }

    protected static void openBugReportScreen() {
        MinecraftClient.getInstance().setScreen(
                new ConfirmLinkScreen(new BooleanConsumer() {
                    @Override
                    public void accept(boolean b) {
                        if(b) {
                            Util.getOperatingSystem().open("https://github.com/MinemasterLegacy/Open-Mine-Map/issues/new");
                        }
                        MinecraftClient.getInstance().setScreen(new FullscreenMapScreen());
                    }

                }, "https://github.com/MinemasterLegacy/Open-Mine-Map/issues/new", true)

        );
    }

    protected static void disableRightClickMenu() {
        rightClickLayer.enabled = false;
        rightClickLayer.setPosition(-500, 500);
        rightClickLayer.selectingSite = false;
    }

    protected static void enableRightClickMenu(double x, double y) {
        rightClickLayer.enabled = true;
        rightClickLayer.selectingSite = false;
        rightClickLayer.clickX = x;
        rightClickLayer.clickY = y;
        rightClickLayer.setPosition((int) x, (int) y);
        rightClickLayer.setSavedMouseLatLong(mouseLong, mouseLat);
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

    @Override
    protected void init() { //called when screen is being initialized
        instance = this;

        if (mClient.player == null) {
            playerIdentifier = Identifier.of("openminemap", "skinbackup.png");
        } else {
            playerIdentifier = mClient.player.getSkin().body().texturePath();
        }
        HudMap.playerIdentifier = playerIdentifier;
        //this.addDrawableChild(playerLayer);

        for (int i = 0; i < numHotbarButtons; i++) {
            buttonlayers.put(ButtonFunction.getEnumOf(i), new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[i][0], windowScaledHeight - buttonPositionModifiers[i][1], buttonSize, buttonSize, ButtonFunction.getEnumOf(i)));
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

        directionIndicator = new DirectionIndicator(0, 0, 0, 0, Text.of(""));

        /* uncomment for adding waypoints
        WaypointLayer[] waypointLayer = new WaypointLayer[10];
        for (int i = 0; i < 10; i++) {
            waypointLayer[i] = new WaypointLayer(10, 10 + (12 * i), i);
            this.addDrawableChild(waypointLayer[i]);
        }

        waypointIdentifiers = new Identifier[10];
        for (int i = 0; i < 10; i++) {
            waypointIdentifiers[i] = Identifier.of("openminemap", "waypoints/waypoint"+i+".png");
        }
        */
        TileManager.initializeConfigParameters();

        InteractionLayer interactionLayer = new InteractionLayer(0, 0, 5000, 5000);
        this.addDrawableChild(interactionLayer);

        updateTileSet();

    }

    private static BufferedPlayer drawDirectionIndicatorsToMap(DrawContext context, PlayerEntity player, boolean indicatorsOnly) {
        if (MinecraftClient.getInstance().player.getUuid().equals(player.getUuid())) return null; //cancel the call if the player is the user/client (it has seperate draw code)

        double mcX = player.getX();
        double mcZ = player.getZ();
        double[] geoCoords;
        try {
            geoCoords = Projection.to_geo(mcX, mcZ);
        } catch (CoordinateValueError e) {
            return null;
        }
        if (Double.isNaN(geoCoords[0])) return null;
        double lon = geoCoords[1];
        double lat = geoCoords[0];
        double mapX = UnitConvert.longToMapX(lon, zoomLevel, renderTileSize);
        double mapY = UnitConvert.latToMapY(lat, zoomLevel, renderTileSize);
        int mapXOffset = (int) ((mapX - mapTilePosX)); //from center of both the map and player
        int mapYOffset = (int) ((mapY - mapTilePosY)); //from center of both the map and player

        //context.drawTexture(pTexture, windowScaledWidth / 2, windowScaledHeight / 2, 0, 0, 64, 64, 64, 64);
        Identifier pTexture = PlayersManager.playerSkinList.get(player.getUuid());
        if (pTexture == null) pTexture = Identifier.of("openminemap", "skinbackup.png");

        //context.fill( (windowScaledWidth / 2) - 4 + mapXOffset, (windowScaledHeight / 2) - 4 + mapYOffset, (windowScaledWidth / 2) - 4 + mapXOffset + 8 , (windowScaledHeight / 2) - 4 + mapYOffset + 8, 0xFFFFFFFF);

        double d = player.getYaw() - Direction.calcDymaxionAngleDifference();
        if (OverlayVisibility.checkPermissionFor(TileManager.showDirectionIndicators, OverlayVisibility.LOCAL) && !Double.isNaN(d)) DirectionIndicator.draw(RenderPipelines.GUI_TEXTURED, context, d,(windowScaledWidth / 2) - 12 + mapXOffset, (windowScaledHeight / 2) - 12 + mapYOffset, true, indicatorsOnly);
        if (OverlayVisibility.checkPermissionFor(TileManager.showDirectionIndicators, OverlayVisibility.LOCAL) && !Double.isNaN(d) && OverlayVisibility.checkPermissionFor(TileManager.showDirectionIndicators, OverlayVisibility.LOCAL))
            DirectionIndicator.draw(RenderPipelines.GUI_TEXTURED, context, d,(windowScaledWidth / 2) - 12 + mapXOffset, (windowScaledHeight / 2) - 12 + mapYOffset, false, indicatorsOnly);

        return new BufferedPlayer(mapXOffset, mapYOffset, pTexture, player.getY());
    }

    private static void drawBufferedPlayersToMap(DrawContext context, ArrayList<BufferedPlayer> players) {
        for (BufferedPlayer player : players) {
            if (player == null) continue;
            int x = (windowScaledWidth / 2) - 4 + player.offsetX;
            int y = (windowScaledHeight / 2) - 4 + player.offsetY;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, player.texture, x, y, 8, 8, 8, 8, 8, 8, 64, 64);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, player.texture, x, y, 40, 8, 8, 8, 8, 8, 64, 64);
            if (!Objects.equals(ConfigFile.readParameter(ConfigOptions.ALTITUDE_SHADING), "on")) return;
            double altitudeOffset = player.y - PlayerAttributes.altitude;
            int alpha = (int) (Math.clamp(Math.abs(altitudeOffset) - 16, 0, 80) * 1.5);;
            if (altitudeOffset > 0) {
                context.fill(x, y, x + 8, y + 8, UnitConvert.argb(alpha, 255, 255, 255));
            } else {
                context.fill(x, y, x + 8, y + 8, UnitConvert.argb(alpha, 0, 0, 0));
            }

        }
    }

    private static void drawButtons(DrawContext context) {
        //draws the buttons
        context.drawTexture(RenderPipelines.GUI_TEXTURED,
                zoomLevel < 18 ?
                        (buttonlayers.get(ButtonFunction.ZOOMIN).isHovered() ?
                                getButtonTexture(ButtonFunction.ZOOMIN, ButtonState.HOVER) :
                                getButtonTexture(ButtonFunction.ZOOMIN, ButtonState.DEFAULT)) :
                        getButtonTexture(ButtonFunction.ZOOMIN, ButtonState.LOCKED),
                windowScaledWidth / 2 + buttonPositionModifiers[0][0], windowScaledHeight - buttonPositionModifiers[0][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderPipelines.GUI_TEXTURED,
                zoomLevel > 0 ?
                        (buttonlayers.get(ButtonFunction.ZOOMOUT).isHovered() ?
                                getButtonTexture(ButtonFunction.ZOOMOUT, ButtonState.HOVER) :
                                getButtonTexture(ButtonFunction.ZOOMOUT, ButtonState.DEFAULT)) :
                        getButtonTexture(ButtonFunction.ZOOMOUT, ButtonState.LOCKED),
                windowScaledWidth / 2 + buttonPositionModifiers[1][0], windowScaledHeight - buttonPositionModifiers[1][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderPipelines.GUI_TEXTURED,
                buttonlayers.get(ButtonFunction.RESET).isHovered() ?
                        getButtonTexture(ButtonFunction.RESET, ButtonState.HOVER) :
                        getButtonTexture(ButtonFunction.RESET, ButtonState.DEFAULT),
                windowScaledWidth / 2 + buttonPositionModifiers[2][0], windowScaledHeight - buttonPositionModifiers[2][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderPipelines.GUI_TEXTURED,
                Double.isNaN(playerLon) || doFollowPlayer ?
                        getButtonTexture(ButtonFunction.FOLLOW, ButtonState.LOCKED) :
                        (buttonlayers.get(ButtonFunction.FOLLOW).isHovered() ?
                                getButtonTexture(ButtonFunction.FOLLOW, ButtonState.HOVER) :
                                getButtonTexture(ButtonFunction.FOLLOW, ButtonState.DEFAULT)),
                windowScaledWidth / 2 + buttonPositionModifiers[3][0], windowScaledHeight - buttonPositionModifiers[3][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderPipelines.GUI_TEXTURED,
                buttonlayers.get(ButtonFunction.CONFIG).isHovered() ?
                        getButtonTexture(ButtonFunction.CONFIG, ButtonState.HOVER) :
                        getButtonTexture(ButtonFunction.CONFIG, ButtonState.DEFAULT),
                windowScaledWidth / 2 + buttonPositionModifiers[4][0], windowScaledHeight - buttonPositionModifiers[4][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(RenderPipelines.GUI_TEXTURED,
                buttonlayers.get(ButtonFunction.EXIT).isHovered() ?
                        getButtonTexture(ButtonFunction.EXIT, ButtonState.HOVER) :
                        getButtonTexture(ButtonFunction.EXIT, ButtonState.DEFAULT),
                windowScaledWidth / 2 + buttonPositionModifiers[5][0], windowScaledHeight - buttonPositionModifiers[5][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
    }

    private static void updateWidgetPositions() {
        for (int i = 0; i < numHotbarButtons; i++) { //update button positions (in case screen size has changed)
            buttonlayers.get(ButtonFunction.getEnumOf(i)).setPosition(windowScaledWidth / 2 + buttonPositionModifiers[i][0], windowScaledHeight - buttonPositionModifiers[i][1]);
        }
        toggleHudMapButtonLayer.setPosition(windowScaledWidth - 25, windowScaledHeight - 57);
        attributionLayer.setDimensionsAndPosition(attributionLayer.textWidth + 10,  16, windowScaledWidth - attributionLayer.textWidth - 10, windowScaledHeight - 16);
        bugReportLayer.setPosition(windowScaledWidth - bugReportLayer.getWidth(), windowScaledHeight - 32);
    }

    private void updateTilePositions() {
        if (lastMouseDown) {
            mapTilePosX += (UnitConvert.pixelToScaledCoords((float) (lastMouseX - mClient.mouse.getX())));
            mapTilePosY += (UnitConvert.pixelToScaledCoords((float) (lastMouseY - mClient.mouse.getY())));
        }

        renderTileSize = (int) Math.max(TileManager.tileScaledSize, Math.pow(2, trueZoomLevel - 11));
        //System.out.println("TrueZoom: "+trueZoomLevel+" | Zoom: "+zoomLevel+" | calced size: "+Math.pow(2, trueZoomLevel - 11));

        if (doFollowPlayer) {
            mapTilePosX = UnitConvert.longToMapX(playerLon, zoomLevel, renderTileSize);
            mapTilePosY = UnitConvert.latToMapY(playerLat, zoomLevel, renderTileSize);
        }

        if (mapTilePosX < 0) mapTilePosX = 0;
        if (mapTilePosY < 0) mapTilePosY = 0;
        if (mapTilePosX > renderTileSize * Math.pow(2, zoomLevel)) mapTilePosX = renderTileSize * Math.pow(2, zoomLevel);
        if (mapTilePosY > renderTileSize * Math.pow(2, zoomLevel)) mapTilePosY = renderTileSize * Math.pow(2, zoomLevel);

        mouseTilePosX = mapTilePosX + UnitConvert.pixelToScaledCoords((float) mClient.mouse.getX()) - ((double) windowScaledWidth / 2);
        mouseTilePosY = mapTilePosY + UnitConvert.pixelToScaledCoords((float) mClient.mouse.getY()) - ((double) windowScaledHeight / 2);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) { //called every frame
        super.render(context, mouseX, mouseY, delta);

        updateScreenDims(); //update screen dimension variables in case window has been resized

        updateWidgetPositions(); //update the positions of button and text field widgets in case window has been resized

        //this.updateTileRange();
        //identifiers = TileManager.getRangeOfTiles(mapTilePosX, mapTilePosY, zoomLevel, windowWidth, windowHeight);

        updateTilePositions();

        //basically a list of tile references to be rendered
        //identifiers = TileManager.getRangeOfTiles((int) mapTilePosX, (int) mapTilePosY, zoomLevel, windowScaledWidth, windowScaledHeight, renderTileSize);

        lastMouseX = (int) mClient.mouse.getX();
        lastMouseY = (int) mClient.mouse.getY();

        /*
        for (int i = 0; i < identifiers.length; i++) {
            for (int j = 0; j < identifiers[i].length; j++) {
                context.drawTexture(RenderPipelines.GUI_TEXTURED, identifiers[i][j], (int) ((((TopLeftData[0] + i) * renderTileSize) + (float) windowScaledWidth / 2) - (int) mapTilePosX), (int) ((((TopLeftData[1]+j) * renderTileSize) + (float) windowScaledHeight / 2) - (int) mapTilePosY), 0, 0, trueHW, trueHW, trueHW, trueHW);
            }
        }
        */

        int scaleMultiplier = (int) Math.pow(2, trueZoomLevel - zoomLevel);

        //draws the map tiles
        DrawableMapTile[][] tiles = TileManager.getRangeOfDrawableTiles((int) mapTilePosX, (int) mapTilePosY, zoomLevel, windowScaledWidth, windowScaledHeight, renderTileSize);
        for (DrawableMapTile[] column : tiles) {
            for (DrawableMapTile tile : column) {
                context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        tile.identifier,
                        (int) (tile.x + ((double) windowScaledWidth / 2) - mapTilePosX),
                        (int) (tile.y + ((double) windowScaledHeight / 2) - mapTilePosY),
                        tile.subSectionSize * scaleMultiplier * tile.subSectionX,
                        tile.subSectionSize * scaleMultiplier * tile.subSectionY,
                        renderTileSize,
                        renderTileSize,
                        tile.subSectionSize * scaleMultiplier,
                        tile.subSectionSize * scaleMultiplier,
                        renderTileSize,
                        renderTileSize
                );
            }
        }

        PlayerAttributes.updatePlayerAttributes(mClient);
        if (playerGetCoordsDelay > -1) {
            try {
                if (Double.isNaN(playerLon)) throw new CoordinateValueError(playerLon, playerLat);
                playerDisplayLon = UnitConvert.floorToPlace(playerLon, 5);
                playerDisplayLat = UnitConvert.floorToPlace(playerLat, 5);
            } catch (CoordinateValueError e) {
                playerDisplayLon = "-.-";
                playerDisplayLat = "-.-";
            }
            playerGetCoordsDelay = 0;
        } else {
            playerGetCoordsDelay++;
        }

        if (Double.isNaN(playerLon)) {
            playerMapX = -9999;
            playerMapY = -9999;
            //playerLayer.setPosition(-9999, -9999);
        } else {
            playerMapX = (int) (UnitConvert.longToMapX(playerLon, zoomLevel, renderTileSize) - mapTilePosX - 4 + ((double) windowScaledWidth / 2));
            playerMapY = (int) (UnitConvert.latToMapY(playerLat, zoomLevel, renderTileSize) - mapTilePosY - 4 + ((double) windowScaledHeight / 2));
            //playerLayer.setPosition(playerMapX, playerMapY);
        }

        //this gui code is ugly af ):

        PlayersManager.updatePlayerSkinList();
        ArrayList<BufferedPlayer> players = new ArrayList<>();
        for (PlayerEntity player : PlayersManager.getNearPlayers()) {
            //direction indicators need to be drawn before players. To accomplish this, bufferedPlayer classes store the values needed to draw the players later
            players.add(drawDirectionIndicatorsToMap(context, player, !OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.LOCAL)));
        }
        //now that direction indicators have been drawn, players can be drawn
        if (OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.LOCAL))
            drawBufferedPlayersToMap(context, players);

        //draws the direction indicator (for self)
        boolean indicatorOnly = !OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.SELF);

        if (directionIndicator.loadSuccess && OverlayVisibility.checkPermissionFor(TileManager.showDirectionIndicators, OverlayVisibility.SELF))
            DirectionIndicator.draw(RenderPipelines.GUI_TEXTURED, context, PlayerAttributes.geoYaw, playerMapX - 8, playerMapY - 8, false, indicatorOnly);

        //draws the player (for self)
        if (OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.SELF)) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, playerIdentifier, doFollowPlayer ? windowScaledWidth / 2 - 4 : playerMapX, doFollowPlayer ? windowScaledHeight / 2 - 4 : playerMapY, 8, 8, 8, 8, 8, 8, 64, 64);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, playerIdentifier, doFollowPlayer ? windowScaledWidth / 2 - 4 : playerMapX, doFollowPlayer ? windowScaledHeight / 2 - 4 : playerMapY, 40, 8, 8, 8, 8, 8, 64, 64);
        }

        drawButtons(context);

        int buttonStyle = HudMap.hudEnabled ? 1 : 0;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, toggleHudMapButtonLayer.isHovered() ? showIdentifiers[1][buttonStyle] : showIdentifiers[0][buttonStyle], toggleHudMapButtonLayer.getX(), toggleHudMapButtonLayer.getY(), 0, 0, 20, 20, 20, 20);

        //Double.isNaN(playerLon)

        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow

        if (mouseIsOutOfBounds()) {
            mouseDisplayLat = "-.-";
            mouseDisplayLong = "-.-";
        } else {
            mouseLong = UnitConvert.mxToLong(mouseTilePosX, trueZoomLevel);
            mouseLat = UnitConvert.myToLat(mouseTilePosY, trueZoomLevel);
            mouseDisplayLong = UnitConvert.floorToPlace(mouseLong, 5);
            mouseDisplayLat = UnitConvert.floorToPlace(mouseLat, 5);
        }

        int attributionOffset;
        //if attribution would overlay the coordinate display
        if (attributionLayer.getWidth() + textRenderer.getWidth("Mouse: " + mouseDisplayLat + "°, " + mouseDisplayLong + "°") > windowScaledWidth) {
            attributionOffset = attributionLayer.getHeight();
        } else {
            attributionOffset = 0;
        }

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

        /* uncomment for adding waypoints
        for (int i = 0; i < 10; i++) {
            context.drawTexture(waypointIdentifiers[i], 10, 10 + (12 * i), 0, 0, 9, 9, 9, 9);
        }
         */
    }



}

