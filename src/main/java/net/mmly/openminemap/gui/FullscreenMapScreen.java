package net.mmly.openminemap.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.mmly.openminemap.enums.ButtonFunction;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.OverlayVisibility;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Direction;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.UnitConvert;

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
    protected final int[][] buttonPositionModifiers = new int[][] {
        {-70,(8 + buttonSize)},
        {-46,(8 + buttonSize)},
        {-22,(8 + buttonSize)},
        {2,(8 + buttonSize)},
        {26,(8 + buttonSize)},
        {50,(8 + buttonSize)}
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
    private static BugReportLayer bugReportLayer = new BugReportLayer(0, 0, 157, 16);
    private static ButtonLayer zoominButtonLayer;
    private static ButtonLayer zoomoutButtonLayer;
    private static ButtonLayer resetButtonLayer;
    private static ButtonLayer followButtonLayer;
    private static ButtonLayer configButtonLayer;
    private static ButtonLayer exitButtonLayer;
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
    static int renderTileSize;
    DirectionIndicator directionIndicator;

    public static void followPlayer() {
        if (!Double.isNaN(playerLon)) {
            doFollowPlayer = true;
        }
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
        if ((!TileManager.doArtificialZoom && zoomLevel >= 18) || (TileManager.doArtificialZoom && trueZoomLevel >= 24)) return;
        if (!doFollowPlayer) {
            mapTilePosX -= (mapTilePosX - mouseTilePosX) / 2;
            mapTilePosY -= (mapTilePosY - mouseTilePosY) / 2;
        }
        zoomIn();
    }

    static protected void mouseZoomOut() {
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

    protected static void openOSMAttrScreen() {
        MinecraftClient.getInstance().setScreen(
                new ConfirmLinkScreen(new BooleanConsumer() {
                    @Override
                    public void accept(boolean b) {
                        if(b) {
                            Util.getOperatingSystem().open("https://openstreetmap.org/copyright");
                        }
                        MinecraftClient.getInstance().setScreen(new FullscreenMapScreen());
                    }

                }, "https://openstreetmap.org/copyright", true)

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

    @Override
    protected void init() { //called when screen is being initialized
        /*
        // It's recommended to use the fixed height of 20 to prevent rendering issues with the button textures
        ButtonWidget zoomInWidget = ButtonWidget.builder(Text.of("Zoom in"), (btn) -> {
            this.zoomIn();
        }).dimensions(40, 30, 120, 20).build();
        ButtonWidget zoomOutWidget = ButtonWidget.builder(Text.of("Zoom out"), (btn) -> {
            this.zoomOut();
        }).dimensions(40, 10, 120, 20).build();
        ButtonWidget resetWidget = ButtonWidget.builder(Text.of("Center"), (btn) -> {
            mapTilePosX = (((double) TileManager.tileScaledSize /2) * Math.pow(2, zoomLevel));
            mapTilePosY = (((double) TileManager.tileScaledSize /2) * Math.pow(2, zoomLevel));
        }).dimensions(40, 50, 120, 20).build();
        // Register the button widget(s).
        this.addDrawableChild(zoomInWidget); //add the button widget to the fullscreen map
        this.addDrawableChild(zoomOutWidget);
        this.addDrawableChild(resetWidget);
        */
        //System.out.println(zoomLevel);
        //System.out.println(mapTilePosX);
        //System.out.println(mapTilePosY);

        //playerLayer = new PlayerLayer(100, 100, 8, 8);

        if (mClient.player == null) {
            playerIdentifier = Identifier.of("openminemap", "skinbackup.png");
        } else {
            playerIdentifier = mClient.player.getSkinTextures().texture();
        }
        HudMap.playerIdentifier = playerIdentifier;
        //this.addDrawableChild(playerLayer);

        //buttons should probably be turned into one buttons array
        zoominButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[0][0],windowScaledHeight - buttonPositionModifiers[0][1], buttonSize, buttonSize, ButtonFunction.ZOOM_IN);
        zoomoutButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[1][0],windowScaledHeight - buttonPositionModifiers[1][1], buttonSize, buttonSize, ButtonFunction.ZOOM_OUT);
        resetButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[2][0],windowScaledHeight - buttonPositionModifiers[2][1], buttonSize, buttonSize, ButtonFunction.RESET);
        followButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[3][0],windowScaledHeight - buttonPositionModifiers[3][1], buttonSize, buttonSize, ButtonFunction.FOLLOW);
        configButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[4][0],windowScaledHeight - buttonPositionModifiers[4][1], buttonSize, buttonSize, ButtonFunction.CONFIG);
        exitButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[5][0],windowScaledHeight - buttonPositionModifiers[5][1], buttonSize, buttonSize, ButtonFunction.EXIT);
        toggleHudMapButtonLayer = new ToggleHudMapButtonLayer(windowScaledWidth - 25, windowScaledHeight - 57);
        toggleHudMapButtonLayer.setTooltip(Tooltip.of(Text.of("Toggle HUD Elements")));

        this.addDrawableChild(zoominButtonLayer);
        this.addDrawableChild(zoomoutButtonLayer);
        this.addDrawableChild(resetButtonLayer);
        this.addDrawableChild(followButtonLayer);
        this.addDrawableChild(configButtonLayer);
        this.addDrawableChild(exitButtonLayer);
        this.addDrawableChild(toggleHudMapButtonLayer);

        rightClickLayer = new RightClickMenu(0, 0);
        this.addDrawableChild(rightClickLayer);
        webAppSelectLayer = new WebAppSelectLayer();
        this.addDrawableChild(webAppSelectLayer);

        attributionLayer = new AttributionLayer(windowScaledWidth - 157, windowScaledHeight - 16, 157, 16);
        this.addDrawableChild(attributionLayer); //windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight,
        bugReportLayer = new BugReportLayer(windowScaledWidth - 157, windowScaledHeight - 32, 157, 16);
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

    private static void drawPlayerToMap(DrawContext context, PlayerEntity player) {
        if (MinecraftClient.getInstance().player.getUuid().equals(player.getUuid())) return; //cancel the call if the player is the user/client (it has seperate draw code)

        double mcX = player.getX();
        double mcZ = player.getZ();
        double[] geoCoords;
        try {
            geoCoords = Projection.to_geo(mcX, mcZ);
        } catch (CoordinateValueError e) {
            return;
        }
        if (Double.isNaN(geoCoords[0])) return;
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
        context.drawTexture(pTexture, (windowScaledWidth / 2) - 4 + mapXOffset, (windowScaledHeight / 2) - 4 + mapYOffset, 8, 8, 8,8, 8, 8, 64, 64);
        context.drawTexture(pTexture, (windowScaledWidth / 2) - 4 + mapXOffset, (windowScaledHeight / 2) - 4 + mapYOffset, 8, 8, 40,8, 8, 8, 64, 64);

        double d = player.getYaw() - Direction.calcDymaxionAngleDifference();
        if (OverlayVisibility.checkPermissionFor(TileManager.showDirectionIndicators, OverlayVisibility.LOCAL) && !Double.isNaN(d)) DirectionIndicator.draw(context, d,(windowScaledWidth / 2) - 12 + mapXOffset, (windowScaledHeight / 2) - 12 + mapYOffset, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) { //called every frame
        super.render(context, mouseX, mouseY, delta);

        attributionLayer.setDimensionsAndPosition(157, 16, windowScaledWidth - 157, windowScaledHeight - 16);
        bugReportLayer.setDimensionsAndPosition(157, 16, windowScaledWidth - 157, windowScaledHeight - 32);

        zoominButtonLayer.setPosition(windowScaledWidth / 2 + buttonPositionModifiers[0][0],windowScaledHeight - buttonPositionModifiers[0][1]);
        zoomoutButtonLayer.setPosition(windowScaledWidth / 2 + buttonPositionModifiers[1][0],windowScaledHeight - buttonPositionModifiers[1][1]);
        resetButtonLayer.setPosition(windowScaledWidth / 2 + buttonPositionModifiers[2][0],windowScaledHeight - buttonPositionModifiers[2][1]);
        followButtonLayer.setPosition(windowScaledWidth / 2 + buttonPositionModifiers[3][0],windowScaledHeight - buttonPositionModifiers[3][1]);
        configButtonLayer.setPosition(windowScaledWidth / 2 + buttonPositionModifiers[4][0],windowScaledHeight - buttonPositionModifiers[4][1]);
        exitButtonLayer.setPosition(windowScaledWidth / 2 + buttonPositionModifiers[5][0],windowScaledHeight - buttonPositionModifiers[5][1]);
        toggleHudMapButtonLayer.setPosition(windowScaledWidth - 25, windowScaledHeight - 57);

        this.updateScreenDims(); //update screen dimension variables in case screen has been resized
        //this.updateTileRange();
        //identifiers = TileManager.getRangeOfTiles(mapTilePosX, mapTilePosY, zoomLevel, windowWidth, windowHeight);

        if (lastMouseDown) {
            mapTilePosX += (UnitConvert.pixelToScaledCoords((float) (lastMouseX - mClient.mouse.getX())));
            mapTilePosY += (UnitConvert.pixelToScaledCoords((float) (lastMouseY - mClient.mouse.getY())));
        }

        lastMouseX = (int) mClient.mouse.getX();
        lastMouseY = (int) mClient.mouse.getY();

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

        //basically a list of tile references to be rendered
        identifiers = TileManager.getRangeOfTiles((int) mapTilePosX, (int) mapTilePosY, zoomLevel, windowScaledWidth, windowScaledHeight, renderTileSize);

        //int trueHW = this.pixelToScaledCoords(256);
        int trueHW = renderTileSize;
        int[] TopLeftData = TileManager.getTopLeftData();

        //draws the map tiles
        for (int i = 0; i < identifiers.length; i++) {
            for (int j = 0; j < identifiers[i].length; j++) {
                RenderSystem.setShaderTexture(0, identifiers[i][j]);
                //System.out.println("Position "+((((TopLeftData[0] + i) * 256) + (float) windowWidth / 2) - mapTilePosX)+", "+((((TopLeftData[1]+j) * 256) + (float) windowHeight / 2) - mapTilePosY)+" calculated for tile ["+i+","+j+"]");
                //context.drawTexture(identifiers[i][j], this.pixelToScaledCoords((((TopLeftData[0] + i) * 256) + (float) windowWidth / 2) - mapTilePosX), this.pixelToScaledCoords((((TopLeftData[1]+j) * 256) + (float) windowHeight / 2) - mapTilePosY), 0, 0, trueHW, trueHW, trueHW, trueHW);
                context.drawTexture(identifiers[i][j], (int) ((((TopLeftData[0] + i) * renderTileSize) + (float) windowScaledWidth / 2) - (int) mapTilePosX), (int) ((((TopLeftData[1]+j) * renderTileSize) + (float) windowScaledHeight / 2) - (int) mapTilePosY), 0, 0, trueHW, trueHW, trueHW, trueHW);

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

        if (OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.LOCAL)) {
            PlayersManager.updatePlayerSkinList();
            for (PlayerEntity player : PlayersManager.getNearPlayers()) {
                drawPlayerToMap(context, player);
            }
        }

        //draws the direction indicator
        if (directionIndicator.loadSuccess && OverlayVisibility.checkPermissionFor(TileManager.showDirectionIndicators, OverlayVisibility.SELF)) DirectionIndicator.draw(context, PlayerAttributes.geoYaw, playerMapX - 8, playerMapY - 8, false);

        //draws the player
        if (OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.SELF)) {
            context.drawTexture(playerIdentifier, doFollowPlayer ? windowScaledWidth / 2 - 4 : playerMapX, doFollowPlayer ? windowScaledHeight / 2 - 4 : playerMapY, 8, 8,8,8, 8, 8, 64, 64);
            context.drawTexture(playerIdentifier, doFollowPlayer ? windowScaledWidth / 2 - 4 : playerMapX, doFollowPlayer ? windowScaledHeight / 2 - 4 : playerMapY, 8, 8,40,8, 8, 8, 64, 64);
        }

        //draws the buttons
        context.drawTexture(zoomLevel < 18 ? (zoominButtonLayer.isHovered() ? buttonIdentifiers[2][0] : buttonIdentifiers[1][0]) : buttonIdentifiers[0][0], windowScaledWidth / 2 + buttonPositionModifiers[0][0], windowScaledHeight - buttonPositionModifiers[0][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(zoomLevel > 0 ? (zoomoutButtonLayer.isHovered() ? buttonIdentifiers[2][1] : buttonIdentifiers[1][1]) : buttonIdentifiers[0][1], windowScaledWidth / 2 + buttonPositionModifiers[1][0], windowScaledHeight - buttonPositionModifiers[1][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(resetButtonLayer.isHovered() ? buttonIdentifiers[2][2] : buttonIdentifiers[1][2], windowScaledWidth / 2 + buttonPositionModifiers[2][0], windowScaledHeight - buttonPositionModifiers[2][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(Double.isNaN(playerLon) || doFollowPlayer ? buttonIdentifiers[0][3] : (followButtonLayer.isHovered() ? buttonIdentifiers[2][3] : buttonIdentifiers[1][3]), windowScaledWidth / 2 + buttonPositionModifiers[3][0], windowScaledHeight - buttonPositionModifiers[3][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(configButtonLayer.isHovered() ? buttonIdentifiers[2][4] : buttonIdentifiers[1][4], windowScaledWidth / 2 + buttonPositionModifiers[4][0], windowScaledHeight - buttonPositionModifiers[4][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(exitButtonLayer.isHovered() ? buttonIdentifiers[2][5] : buttonIdentifiers[1][5], windowScaledWidth / 2 + buttonPositionModifiers[5][0], windowScaledHeight - buttonPositionModifiers[5][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        int buttonStyle = HudMap.renderHud ? 1 : 0;
        context.drawTexture(toggleHudMapButtonLayer.isHovered() ? showIdentifiers[1][buttonStyle] : showIdentifiers[0][buttonStyle], toggleHudMapButtonLayer.getX(), toggleHudMapButtonLayer.getY(), 0, 0, 20, 20, 20, 20);

        //Double.isNaN(playerLon)

        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow

        mouseTilePosX = mapTilePosX + UnitConvert.pixelToScaledCoords((float) mClient.mouse.getX()) - (windowScaledWidth / 2);
        mouseTilePosY = mapTilePosY + UnitConvert.pixelToScaledCoords((float) mClient.mouse.getY()) - (windowScaledHeight / 2);

        if (mouseIsOutOfBounds()) {
            mouseDisplayLat = "-.-";
            mouseDisplayLong = "-.-";
        } else {
            mouseLong = UnitConvert.mxToLong(mouseTilePosX, trueZoomLevel);
            mouseLat = UnitConvert.myToLat(mouseTilePosY, trueZoomLevel);
            mouseDisplayLong = UnitConvert.floorToPlace(mouseLong, 5);
            mouseDisplayLat = UnitConvert.floorToPlace(mouseLat, 5);
        }

        //draws the Mouse and player coordinates text fields
        context.fill(0, windowScaledHeight - 16, 53 + (mouseDisplayLong.length() * 6) + (mouseDisplayLat.length() * 6), windowScaledHeight, 0x88000000);
        context.drawText(this.textRenderer, "Mouse: " + mouseDisplayLat + "°, " + mouseDisplayLong + "°", 4, windowScaledHeight + 7 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
        context.fill(0, windowScaledHeight - 32, 55 + (playerDisplayLon.length() * 6) + (playerDisplayLat.length() * 6), windowScaledHeight - 16, 0x88000000);
        context.drawText(this.textRenderer, "Player: " + playerDisplayLat + "°, " + playerDisplayLon + "°", 4, windowScaledHeight + 7  - this.textRenderer.fontHeight - 10 - 16, 0xFFFFFFFF, true);

        //draws the attribution and report bug text fields
        context.fill(windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight, 0x88000000);
        context.drawText(this.textRenderer, "Map data from", windowScaledWidth - 152, windowScaledHeight + 7 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, Text.of("OpenStreetMap"), windowScaledWidth - 77, windowScaledHeight + 7 - this.textRenderer.fontHeight - 10, 0xFF548AF7, true); //0xFF1b75d0
        context.fill(windowScaledWidth - 70, windowScaledHeight - 32, windowScaledWidth, windowScaledHeight - 16, 0x88000000);
        context.drawText(this.textRenderer, Text.of("Report Bugs"), windowScaledWidth - 65, windowScaledHeight + 7 - this.textRenderer.fontHeight - 10 - 16, 0xFF0B9207, true);

        /* Seems that ~1.21.4+ needs an extra argument for context.drawTexture
        context.drawTexture(RenderLayer::getGuiTextured, ...); */

        //draws the right click menu
        rightClickLayer.drawWidget(context, this.textRenderer);
        webAppSelectLayer.drawWidget(context);

        directionIndicator.render(context);

        /* uncomment for adding waypoints
        for (int i = 0; i < 10; i++) {
            context.drawTexture(waypointIdentifiers[i], 10, 10 + (12 * i), 0, 0, 9, 9, 9, 9);
        }
         */
    }

}
