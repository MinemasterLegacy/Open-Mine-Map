package net.mmly.openminemap.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.UnitConvert;

public class FullscreenMapScreen extends Screen { //Screen object that represents the fullscreen map
    public FullscreenMapScreen() {
        super(Text.of("OMM Fullscreen Map"));
    }

    public static int zoomLevel = Integer.parseInt(ConfigFile.readParameter("§fslastzoom")); //indicates the current zoom level of the map | default 0
    Identifier identifiers[][];

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

    protected static Identifier waypointIdentifiers[];

    /*
    // describaes how many tiles are needed to fill the screen in each direction.
    // Tiles are 256px wide | equation: Ceiling(screenSize / 256)
    // EX: a 1000 px screen would need 8 tiles
    int tileRangeX;
    int tileRangeY;
     */

    // mouse x, y and down of previous frame, recorded here specifically so that changes in mousexy while left mouse is clicked can be detected.
    int lastMouseX = 0;
    int lastMouseY = 0;
    protected static boolean lastMouseDown = false;

    protected static double mouseTilePosX = 64;
    protected static double mouseTilePosY = 64;

    // modifiers used to offset the map so it can be moved relative to the screen
    // these modifiers should be scaled when the screen is zoomed in or zoomed out
    // Ex: zoom 0, range -128 - 127 | zoom 1, range -256 - 255 | zoom 2, range -512 - 511 | etc.
    static double mapTilePosX = Double.parseDouble(ConfigFile.readParameter("§fslastx")); //64 by default
    static double mapTilePosY = Double.parseDouble(ConfigFile.readParameter("§fslasty")); //64 by default

    MinecraftClient mClient = MinecraftClient.getInstance();
    Window window = mClient.getWindow();

    private static RightClickMenu rightClickLayer = new RightClickMenu(0, 0);
    static boolean rightClickMenuEnabled = false;

    private static AttributionLayer attributionLayer = new AttributionLayer(0, 0, 157, 16);
    private static BugReportLayer bugReportLayer = new BugReportLayer(0, 0, 157, 16);

    private static ButtonLayer zoominButtonLayer;
    private static ButtonLayer zoomoutButtonLayer;
    private static ButtonLayer resetButtonLayer;
    private static ButtonLayer followButtonLayer;
    private static ButtonLayer configButtonLayer;
    private static ButtonLayer exitButtonLayer;

    private Identifier rightClickCursor = Identifier.of("openminemap", "selectcursor.png");

    private static Identifier[][] buttonIdentifiers = new Identifier[3][6];
    public static int buttonTheme = 0; // 0 is vanilla, 1 is sodify

    private static PlayerLayer playerLayer;
    private Identifier playerIdentifier;
    int playerMapX;
    int playerMapY;
    public static double playerLon;
    public static double playerLat;
    int playerGetCoordsDelay = 20;
    String playerDisplayLon = "0.00000";
    String playerDisplayLat = "0.00000";
    public static boolean doFollowPlayer;

    public static void followPlayer() {
        if (!Double.isNaN(playerLon)) {
            doFollowPlayer = true;
        }
    }

    @Override
    public void close() {
        disableRightClickMenu();
        ConfigFile.writeParameter("§fslastzoom", Integer.toString(zoomLevel));
        ConfigFile.writeParameter("§fslastx", Double.toString(mapTilePosX));
        ConfigFile.writeParameter("§fslasty", Double.toString(mapTilePosY));
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
        switch (buttonTheme) {
            case 1: path = "buttons/sodify/";
            default: path = "buttons/vanilla/"; // 0
        };
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 6; j++) {
                buttonIdentifiers[i][j] = Identifier.of("openminemap", path + states[i] + names[j]);
            }
        }
    }

    static protected void mouseZoomIn() {
        if (zoomLevel >= 18) return;
        if (doFollowPlayer) zoomIn();
        mapTilePosX -= (mapTilePosX - mouseTilePosX) / 2;
        mapTilePosY -= (mapTilePosY - mouseTilePosY) / 2;
        zoomIn();
    }

    static protected void mouseZoomOut() {
        if (zoomLevel <= 0) return;
        if (doFollowPlayer) zoomOut();
        mapTilePosX += (mapTilePosX - mouseTilePosX);
        mapTilePosY += (mapTilePosY - mouseTilePosY);
        zoomOut();
    }

    static protected void zoomIn() {
        if (zoomLevel < 18) {
            zoomLevel++;
            mapTilePosX *= 2;
            mapTilePosY *= 2;
        } else {
            zoomLevel = 18;
        }
    }

    static protected void zoomOut() {
        if (zoomLevel > 0) {
            zoomLevel--;
            mapTilePosX = (float) mapTilePosX / 2;
            mapTilePosY = (float) mapTilePosY / 2;
        } else {
            zoomLevel = 0;
        }
    }

    static protected void resetMap() {
        doFollowPlayer = false;
        mapTilePosX = 64; //(((double) TileManager.tileScaledSize /2) * Math.pow(2, zoomLevel));
        mapTilePosY = 64; //(((double) TileManager.tileScaledSize /2) * Math.pow(2, zoomLevel));
        zoomLevel = 0;
    }

    protected boolean mouseIsOutOfBounds() {
        return mouseTilePosX < 0 || mouseTilePosY < 0 || mouseTilePosX > Math.pow(2, zoomLevel + 7) || mouseTilePosY > Math.pow(2, zoomLevel + 7);
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
        rightClickMenuEnabled = false;
        rightClickLayer.setPosition(-500, 500);
    }

    protected static void enableRightClickMenu(double x, double y) {
        rightClickMenuEnabled = true;
        rightClickLayer.setPosition((int) x, (int) y);
        rightClickLayer.setSavedMouseLatLong(mouseLong, mouseLat);
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
        System.out.println(zoomLevel);
        System.out.println(mapTilePosX);
        System.out.println(mapTilePosY);

        //playerLayer = new PlayerLayer(100, 100, 8, 8);
        playerIdentifier = mClient.player.getSkinTextures().texture();
        HudMap.playerIdentifier = playerIdentifier;
        //this.addDrawableChild(playerLayer);

        //buttons should probably be turned into one buttons array
        zoominButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[0][0],windowScaledHeight - buttonPositionModifiers[0][1], buttonSize, buttonSize, 0);
        zoomoutButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[1][0],windowScaledHeight - buttonPositionModifiers[1][1], buttonSize, buttonSize, 1);
        resetButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[2][0],windowScaledHeight - buttonPositionModifiers[2][1], buttonSize, buttonSize, 2);
        followButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[3][0],windowScaledHeight - buttonPositionModifiers[3][1], buttonSize, buttonSize, 3);
        configButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[4][0],windowScaledHeight - buttonPositionModifiers[4][1], buttonSize, buttonSize, 4);
        exitButtonLayer = new ButtonLayer(windowScaledWidth / 2 + buttonPositionModifiers[5][0],windowScaledHeight - buttonPositionModifiers[5][1], buttonSize, buttonSize, 5);

        this.addDrawableChild(zoominButtonLayer);
        this.addDrawableChild(zoomoutButtonLayer);
        this.addDrawableChild(resetButtonLayer);
        this.addDrawableChild(followButtonLayer);
        this.addDrawableChild(configButtonLayer);
        this.addDrawableChild(exitButtonLayer);

        rightClickLayer = new RightClickMenu(0, 0);
        this.addDrawableChild(rightClickLayer);

        attributionLayer = new AttributionLayer(windowScaledWidth - 157, windowScaledHeight - 16, 157, 16);
        this.addDrawableChild(attributionLayer); //windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight,
        bugReportLayer = new BugReportLayer(windowScaledWidth - 157, windowScaledHeight - 32, 157, 16);
        this.addDrawableChild(bugReportLayer); //windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight,

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


        InteractionLayer interactionLayer = new InteractionLayer(0, 0, 5000, 5000);
        this.addDrawableChild(interactionLayer);

        updateTileSet();

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

        this.updateScreenDims(); //update screen dimension variables in case screen has been resized
        //this.updateTileRange();
        //identifiers = TileManager.getRangeOfTiles(mapTilePosX, mapTilePosY, zoomLevel, windowWidth, windowHeight);

        PlayerAttributes.updatePlayerLocations(mClient);
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
            playerMapX = (int) (UnitConvert.longToMx(playerLon, zoomLevel, TileManager.tileScaledSize) - mapTilePosX - 4 + ((double) windowScaledWidth / 2));
            playerMapY = (int) (UnitConvert.latToMy(playerLat, zoomLevel, TileManager.tileScaledSize) - mapTilePosY - 4 + ((double) windowScaledHeight / 2));
            //playerLayer.setPosition(playerMapX, playerMapY);
        }

        if (lastMouseDown) {
            mapTilePosX += (UnitConvert.pixelToScaledCoords((float) (lastMouseX - mClient.mouse.getX())));
            mapTilePosY += (UnitConvert.pixelToScaledCoords((float) (lastMouseY - mClient.mouse.getY())));
        }

        lastMouseX = (int) mClient.mouse.getX();
        lastMouseY = (int) mClient.mouse.getY();

        if (doFollowPlayer) {
            mapTilePosX = UnitConvert.longToMx(playerLon, zoomLevel, TileManager.tileScaledSize);
            mapTilePosY = UnitConvert.latToMy(playerLat, zoomLevel, TileManager.tileScaledSize);
        }

        if (mapTilePosX < 0) mapTilePosX = 0;
        if (mapTilePosY < 0) mapTilePosY = 0;
        if (mapTilePosX > TileManager.tileScaledSize * Math.pow(2, zoomLevel)) mapTilePosX = TileManager.tileScaledSize * Math.pow(2, zoomLevel);
        if (mapTilePosY > TileManager.tileScaledSize * Math.pow(2, zoomLevel)) mapTilePosY = TileManager.tileScaledSize * Math.pow(2, zoomLevel);

        //basically a list of tile references to be rendered
        identifiers = TileManager.getRangeOfTiles((int) mapTilePosX, (int) mapTilePosY, zoomLevel, windowScaledWidth, windowScaledHeight, TileManager.tileScaledSize);

        //int trueHW = this.pixelToScaledCoords(256);
        int trueHW = TileManager.tileScaledSize;
        int[] TopLeftData = TileManager.getTopLeftData();

        for (int i = 0; i < identifiers.length; i++) {
            for (int j = 0; j < identifiers[i].length; j++) {
                RenderSystem.setShaderTexture(0, identifiers[i][j]);
                //System.out.println("Position "+((((TopLeftData[0] + i) * 256) + (float) windowWidth / 2) - mapTilePosX)+", "+((((TopLeftData[1]+j) * 256) + (float) windowHeight / 2) - mapTilePosY)+" calculated for tile ["+i+","+j+"]");
                //context.drawTexture(identifiers[i][j], this.pixelToScaledCoords((((TopLeftData[0] + i) * 256) + (float) windowWidth / 2) - mapTilePosX), this.pixelToScaledCoords((((TopLeftData[1]+j) * 256) + (float) windowHeight / 2) - mapTilePosY), 0, 0, trueHW, trueHW, trueHW, trueHW);
                context.drawTexture(identifiers[i][j], (int) ((((TopLeftData[0] + i) * TileManager.tileScaledSize) + (float) windowScaledWidth / 2) - (int) mapTilePosX), (int) ((((TopLeftData[1]+j) * TileManager.tileScaledSize) + (float) windowScaledHeight / 2) - (int) mapTilePosY), 0, 0, trueHW, trueHW, trueHW, trueHW);

            }
        }

        //this gui code is ugly af ):
        context.drawTexture(playerIdentifier, doFollowPlayer ? windowScaledWidth / 2 - 4 : playerMapX, doFollowPlayer ? windowScaledHeight / 2 - 4 : playerMapY, 8, 8,8,8, 8, 8, 64, 64);
        context.drawTexture(playerIdentifier, doFollowPlayer ? windowScaledWidth / 2 - 4 : playerMapX, doFollowPlayer ? windowScaledHeight / 2 - 4 : playerMapY, 8, 8,40,8, 8, 8, 64, 64);

        context.drawTexture(zoomLevel < 18 ? (zoominButtonLayer.isHovered() ? buttonIdentifiers[2][0] : buttonIdentifiers[1][0]) : buttonIdentifiers[0][0], windowScaledWidth / 2 + buttonPositionModifiers[0][0], windowScaledHeight - buttonPositionModifiers[0][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(zoomLevel > 0 ? (zoomoutButtonLayer.isHovered() ? buttonIdentifiers[2][1] : buttonIdentifiers[1][1]) : buttonIdentifiers[0][1], windowScaledWidth / 2 + buttonPositionModifiers[1][0], windowScaledHeight - buttonPositionModifiers[1][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(resetButtonLayer.isHovered() ? buttonIdentifiers[2][2] : buttonIdentifiers[1][2], windowScaledWidth / 2 + buttonPositionModifiers[2][0], windowScaledHeight - buttonPositionModifiers[2][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(Double.isNaN(playerLon) || doFollowPlayer ? buttonIdentifiers[0][3] : (followButtonLayer.isHovered() ? buttonIdentifiers[2][3] : buttonIdentifiers[1][3]), windowScaledWidth / 2 + buttonPositionModifiers[3][0], windowScaledHeight - buttonPositionModifiers[3][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(configButtonLayer.isHovered() ? buttonIdentifiers[2][4] : buttonIdentifiers[1][4], windowScaledWidth / 2 + buttonPositionModifiers[4][0], windowScaledHeight - buttonPositionModifiers[4][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);
        context.drawTexture(exitButtonLayer.isHovered() ? buttonIdentifiers[2][5] : buttonIdentifiers[1][5], windowScaledWidth / 2 + buttonPositionModifiers[5][0], windowScaledHeight - buttonPositionModifiers[5][1], 0, 0, buttonSize, buttonSize, buttonSize, buttonSize);

        //Double.isNaN(playerLon)

        // Minecraft doesn't have a "label" widget, so we'll have to draw our own text.
        // We'll subtract the font height from the Y position to make the text appear above the button.
        // Subtracting an extra 10 pixels will give the text some padding.
        // textRenderer, text, x, y, color, hasShadow

        mouseTilePosX = mapTilePosX + UnitConvert.pixelToScaledCoords((float) mClient.mouse.getX()) - (windowScaledWidth/2);
        mouseTilePosY = mapTilePosY + UnitConvert.pixelToScaledCoords((float) mClient.mouse.getY()) - (windowScaledHeight/2);

        if (mouseIsOutOfBounds()) {
            mouseDisplayLat = "-.-";
            mouseDisplayLong = "-.-";
        } else {
            mouseLong = UnitConvert.mxToLong(mouseTilePosX, zoomLevel);
            mouseLat = UnitConvert.myToLat(mouseTilePosY, zoomLevel);
            mouseDisplayLong = UnitConvert.floorToPlace(mouseLong, 5);
            mouseDisplayLat = UnitConvert.floorToPlace(mouseLat, 5);
        }

        //draw on the fullscreen map
        context.fill(0, windowScaledHeight - 16, 53 + (mouseDisplayLong.length() * 6) + (mouseDisplayLat.length() * 6), windowScaledHeight, 0x88000000);
        context.drawText(this.textRenderer, "Mouse: " + mouseDisplayLat + "°, " + mouseDisplayLong + "°", 4, windowScaledHeight + 7 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
        context.fill(0, windowScaledHeight - 32, 55 + (playerDisplayLon.length() * 6) + (playerDisplayLat.length() * 6), windowScaledHeight - 16, 0x88000000);
        context.drawText(this.textRenderer, "Player: " + playerDisplayLat + "°, " + playerDisplayLon + "°", 4, windowScaledHeight + 7  - this.textRenderer.fontHeight - 10 - 16, 0xFFFFFFFF, true);

        context.fill(windowScaledWidth - 157, windowScaledHeight - 16, windowScaledWidth, windowScaledHeight, 0x88000000);
        context.drawText(this.textRenderer, "Map data from", windowScaledWidth - 152, windowScaledHeight + 7 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, Text.of("OpenStreetMap"), windowScaledWidth - 77, windowScaledHeight + 7 - this.textRenderer.fontHeight - 10, 0xFF548AF7, true); //0xFF1b75d0
        context.fill(windowScaledWidth - 70, windowScaledHeight - 32, windowScaledWidth, windowScaledHeight - 16, 0x88000000);
        context.drawText(this.textRenderer, Text.of("Report Bugs"), windowScaledWidth - 65, windowScaledHeight + 7 - this.textRenderer.fontHeight - 10 - 16, 0xFF0B9207, true);


        /* Seems that ~1.21.4+ needs an extra argument for context.drawTexture
        context.drawTexture(RenderLayer::getGuiTextured, ...); */

        /*
        context.drawText(this.textRenderer, "Tile Pos X: " + mapTilePosX , 20, 20 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "Tile Pos Y: " + mapTilePosY , 20, 40 - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "Mouse X: " + mouseTilePosX, 20, 60   - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
        context.drawText(this.textRenderer, "Mouse Y: " + mouseTilePosY, 20, 80   - this.textRenderer.fontHeight - 10, 0xFFFFFFFF, true);
         */

        if (rightClickMenuEnabled) {
            context.fill(rightClickLayer.getX(), rightClickLayer.getY(), rightClickLayer.getX() + RightClickMenu.width, rightClickLayer.getY() + RightClickMenu.height, 0x88000000);
            context.drawText(this.textRenderer, "Teleport Here", rightClickLayer.getX() + 4, rightClickLayer.getY() + 4, RightClickMenu.hoverOn == 1 ? 0xFFa8afff : 0xFFFFFFFF, false);
            context.drawText(this.textRenderer, "Copy Coordinates", rightClickLayer.getX() + 4, rightClickLayer.getY() + 20, RightClickMenu.hoverOn == 2 ? 0xFFa8afff : 0xFFFFFFFF, false);
            context.drawTexture(rightClickCursor, rightClickLayer.getX() - 4, rightClickLayer.getY() - 4, 0, 0, 9, 9, 9, 9);
        }

        /* uncomment for adding waypoints
        for (int i = 0; i < 10; i++) {
            context.drawTexture(waypointIdentifiers[i], 10, 10 + (12 * i), 0, 0, 9, 9, 9, 9);
        }
         */
    }

}
