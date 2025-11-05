package net.mmly.openminemap.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.OverlayVisibility;
import net.mmly.openminemap.gui.DirectionIndicator;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Direction;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.UnitConvert;
import org.joml.Matrix4f;

public class HudMap {

    public static final int MIN_SIZE = 20;
    public static int hudMapX = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_X));
    public static int hudMapY = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_Y));
    public static int hudMapWidth = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_WIDTH));
    public static int hudMapHeight = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_HEIGHT));
    public static int hudMapX2 = hudMapX + hudMapWidth;
    public static int hudMapY2 = hudMapY + hudMapHeight;
    static boolean initialized = false;
    public static int trueZoomLevel = Integer.parseInt(ConfigFile.readParameter(ConfigOptions._FS_LAST_ZOOM));
    static int zoomLevel = Math.min(trueZoomLevel, 18); //essentially decides what tile size folder to pull from
    static Identifier[][] tileIdentifiers;
    static double mapTilePosX = 64;
    static double mapTilePosY = 64;
    static Window window = MinecraftClient.getInstance().getWindow();
    static int windowScaledHeight;
    static int windowScaledWidth;
    public static double playerLon;
    public static double playerLat;
    static double playerMapX;
    static double playerMapY;
    public static boolean renderHud = Boolean.parseBoolean(ConfigFile.readParameter(ConfigOptions._HUD_TOGGLE));
    public static int reloadSkin = 4;
    public static int hudCompassX = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_X));
    public static int hudCompassY = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_Y));
    public static int hudCompassWidth = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_WIDTH));
    protected static Identifier compassIdentifier = Identifier.of("openminemap", "stripcompass.png");
    protected static Identifier snapAngleIdentifier = Identifier.of("openminemap", "snapangle.png");
    protected static int hudCompassCenter;
    static double snapAngleInput;
    public static double snapAngle; //range: (-90, 0]
    public static double direction;
    private static DirectionIndicator directionIndicator = new DirectionIndicator(0, 0, 0, 0, Text.of(""));
    public static boolean doSnapAngle = false;
    private static boolean playerIsOutOfBouds;

    // used in conjunction with artificial zoom mode;
    // TileManager.hudTileScaledSize does not get updated with artificial zoom, this variable does instead
    // If artificialZoom is off, this variable should always equal TileManager.hudTileScaledSize
    static int renderTileSize;

    public static Identifier playerIdentifier;

    public static void setSnapAngle() {
        String receivedSnapAngle = ConfigFile.readParameter(ConfigOptions.SNAP_ANGLE);
        if (receivedSnapAngle.isEmpty()) {
            doSnapAngle = false;
        } else {
            doSnapAngle = true;
            snapAngleInput = Double.parseDouble(receivedSnapAngle);
            snapAngle = ((-snapAngleInput) % 90) - (90 * (((-snapAngleInput) % 90) > 0 ? 1 : 0));
        }

    }

    public static void initialize(DrawContext context) {
        TileManager.initializeConfigParameters();
        setSnapAngle();
    }

    public static void updateX2Y2() {
        hudMapX2 = hudMapX + hudMapWidth;
        hudMapY2 = hudMapY + hudMapHeight;
    }

    public static void zoomIn() {
        if (TileManager.doArtificialZoom) {
            if (trueZoomLevel < 24) {
                trueZoomLevel++;
                zoomLevel = Math.min(18, trueZoomLevel);
                mapTilePosX *= 2;
                mapTilePosY *= 2;
                writeZoom();
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
                writeZoom();
            } else {
                zoomLevel = 18;
                trueZoomLevel = 18;
            }
        }
    }

    public static void zoomOut() {
        if (TileManager.doArtificialZoom) {
            if (trueZoomLevel > 0) {
                trueZoomLevel--;
                zoomLevel = Math.min(18, trueZoomLevel);
                mapTilePosX = (float) mapTilePosX / 2;
                mapTilePosY = (float) mapTilePosY / 2;
                writeZoom();
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
                writeZoom();
            } else {
                zoomLevel = 0;
                trueZoomLevel = 0;
            }
        }
    }

    private static void writeZoom() {
        ConfigFile.writeParameter(ConfigOptions._HUD_LAST_ZOOM, Integer.toString(trueZoomLevel));
        ConfigFile.writeToFile();
    }

    public static void toggleRendering() {
        renderHud = !renderHud;
        ConfigFile.writeParameter(ConfigOptions._HUD_TOGGLE, Boolean.toString(renderHud));
        ConfigFile.writeToFile();
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

        int rightCrop = (int) (Math.ceil((double) hudMapWidth / 2) - (mapXOffset - 4));
        rightCrop = Math.clamp(rightCrop, 0, 8);
        int downCrop = (int) (Math.ceil((double) hudMapHeight / 2) - (mapYOffset - 4));
        downCrop = Math.clamp(downCrop, 0, 8);
        int leftCrop = ((int) ((mapXOffset + 4) - Math.ceil((double) hudMapWidth / -2)) - 8) * -1;
        leftCrop = Math.clamp(leftCrop, 0, 8);
        int upCrop = ((int) ((mapYOffset + 4) - Math.ceil((double) hudMapHeight / -2)) - 8) * -1;
        upCrop = Math.clamp(upCrop, 0, 8);

        //context.drawTexture(pTexture, windowScaledWidth / 2, windowScaledHeight / 2, 0, 0, 64, 64, 64, 64);
        Identifier pTexture = PlayersManager.playerSkinList.get(player.getUuid());
        if (pTexture == null) pTexture = Identifier.of("openminemap", "skinbackup.png");

        //context.fill(hudMapX + (hudMapWidth / 2) - 4 + mapXOffset, hudMapY + (hudMapHeight / 2) - 4 + mapYOffset, hudMapX + (hudMapWidth / 2) - 4 + mapXOffset + 8 , hudMapY + (hudMapHeight / 2) - 4 + mapYOffset + 8, 0xFFFFFFFF);
        context.drawTexture(pTexture, hudMapX + (hudMapWidth / 2) - 4 + mapXOffset + leftCrop, hudMapY + (hudMapHeight / 2) - 4 + mapYOffset + upCrop, rightCrop - leftCrop, downCrop - upCrop, 8 + leftCrop,8 + upCrop, rightCrop - leftCrop, downCrop - upCrop, 64, 64);
        context.drawTexture(pTexture, hudMapX + (hudMapWidth / 2) - 4 + mapXOffset + leftCrop, hudMapY + (hudMapHeight / 2) - 4 + mapYOffset + upCrop, rightCrop - leftCrop, downCrop - upCrop, 40 + leftCrop,8 + upCrop, rightCrop - leftCrop, downCrop - upCrop, 64, 64);

        double d = player.getYaw() - Direction.calcDymaxionAngleDifference();
        if (OverlayVisibility.checkPermissionFor(TileManager.showDirectionIndicators, OverlayVisibility.LOCAL) && !Double.isNaN(d)) DirectionIndicator.draw(context, d,hudMapX + (hudMapWidth / 2) - 12 + mapXOffset, hudMapY + (hudMapHeight / 2) - 12 + mapYOffset);
    }

    public static void render(DrawContext context, RenderTickCounter renderTickCounter) {
        if (!renderHud || MinecraftClient.getInstance().options.hudHidden) return; //do not do anything if hud rendering is disabled

        if (reloadSkin > 0) { //load player skins
            if (MinecraftClient.getInstance().player == null) {
                playerIdentifier = Identifier.of("openminemap", "skinbackup.png");
            } else {
                playerIdentifier = MinecraftClient.getInstance().player.getSkinTextures().texture();
            }
            PlayersManager.updatePlayerSkinList();
            reloadSkin--;
        }

        if (!initialized) initialize(context); //initialize hudmap if not done already
        PlayerAttributes.updatePlayerAttributes(MinecraftClient.getInstance()); //refreshes values for geographic longitude, latitude and yaw
        direction = Direction.calcDymaxionAngleDifference(); //the difference between mc yaw and geo yaw
        hudCompassCenter = Math.round((float) hudCompassWidth / 2); //center of the hud compass

        //update window heights
        windowScaledHeight = window.getScaledHeight();
        windowScaledWidth = window.getScaledWidth();

        //set hud tile render size; will be constant unless artificial zoom is enabled
        renderTileSize = (int) Math.max(TileManager.hudTileScaledSize, Math.pow(2, trueZoomLevel - 11));

        tileIdentifiers = TileManager.getRangeOfTiles((int) mapTilePosX, (int) mapTilePosY, zoomLevel, hudMapWidth, hudMapHeight, renderTileSize); //get identifiers for all tiles that will be rendered this tick
        int trueHW = renderTileSize;
        int[] TopLeftData = TileManager.getTopLeftData(); //gets the xy position of the top left most tile

        //basic monocolor background
        context.fill(hudMapX, hudMapY, hudMapX2, hudMapY2, 0, 0xFFCEE1E4);

        if (Double.isNaN(playerLon)) {//if the player is out of bounds this will be NaN. all other rendering is skipped due to this
            //draw error message and exit
            MutableText text = Text.literal("Out Of Bounds").formatted(Formatting.ITALIC);
            //context.fill(hudMapX + 2, hudMapY + 2, hudMapY + 74, hudMapY + 10, 0xFFFFFFFF);
            context.drawText(MinecraftClient.getInstance().textRenderer, text, hudMapWidth > 73 ? (hudMapX + hudMapX2) / 2 - 37: hudMapX, hudMapHeight > 9 ? (hudMapY + hudMapY2) / 2 - 5 : hudMapY, 0xFFcccccc, true);
            return;
        } else {
            playerMapX = (int) (UnitConvert.longToMapX(playerLon, zoomLevel, renderTileSize) - mapTilePosX - 4 + ((double) windowScaledWidth / 2));
            playerMapY = (int) (UnitConvert.latToMapY(playerLat, zoomLevel, renderTileSize) - mapTilePosY - 4 + ((double) windowScaledHeight / 2));
        }

        //determine map tile positions
        mapTilePosX = UnitConvert.longToMapX(playerLon, zoomLevel, renderTileSize);
        mapTilePosY = UnitConvert.latToMapY(playerLat, zoomLevel, renderTileSize);

        //deaw map tiles, cropping when needed
        for (int i = 0; i < tileIdentifiers.length; i++) {
            for (int j = 0; j < tileIdentifiers[i].length; j++) {
                RenderSystem.setShaderTexture(0, tileIdentifiers[i][j]);
                int tileX = ((((TopLeftData[0] + i) * renderTileSize) + hudMapX + hudMapWidth / 2) - (int) mapTilePosX);
                int tileY = ((((TopLeftData[1] + j) * renderTileSize) + hudMapY + hudMapHeight / 2) - (int) mapTilePosY);

                int leftCrop = tileX < hudMapX ? hudMapX - tileX : 0;
                int topCrop = tileY < hudMapY ? hudMapY - tileY : 0;
                int rightCrop = tileX + renderTileSize > hudMapX + hudMapWidth ? (tileX + renderTileSize) - (hudMapX + hudMapWidth) : 0;
                int bottomCrop = tileY + renderTileSize > hudMapY + hudMapHeight ? (tileY + renderTileSize) - (hudMapY + hudMapHeight) : 0;

                //x, y define where the defined top left corner will go
                //u, v define the lop left corner of the texture
                //w, h crop the texture from right and down
                //texturewidth and textureheight should equal the scale of the tiles (64 here)

                if (trueHW - rightCrop - leftCrop < 0 || trueHW - bottomCrop - topCrop < 0) continue;
                context.drawTexture(tileIdentifiers[i][j], tileX + leftCrop, tileY + topCrop, leftCrop, topCrop, trueHW - rightCrop - leftCrop, trueHW - bottomCrop - topCrop, trueHW, trueHW);
            }
        }

        //draw all players (except self)
        if (OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.LOCAL)) {
            for (PlayerEntity player : PlayersManager.getNearPlayers()) {
                drawPlayerToMap(context, player);
            }
        }


        //draw the direction indicator
        //context.fill(hudMapX + (hudMapWidth / 2) - 12, hudMapY + (hudMapHeight / 2) - 12,hudMapX + (hudMapWidth / 2) - 12 + 24, hudMapY + (hudMapHeight / 2) - 12 + 24, 0xFFFFFFFF);
        if (directionIndicator.loadSuccess && OverlayVisibility.checkPermissionFor(TileManager.showDirectionIndicators, OverlayVisibility.SELF)) DirectionIndicator.draw(context, PlayerAttributes.geoYaw,hudMapX + (hudMapWidth / 2) - 12, hudMapY + (hudMapHeight / 2) - 12);
        //if (directionIndicator.updateDynamicTexture() && directionIndicator.loadSuccess) context.drawTexture(directionIndicator.textureId, hudMapX + (hudMapWidth / 2) - 12, hudMapY + (hudMapHeight / 2) - 12, 0, 0, 24, 24, 24, 24);

        //draw the player; two draw statements are used in order to display both skin layers
        if (OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.SELF)) {
            context.drawTexture(playerIdentifier, hudMapX + (hudMapWidth / 2) - 4, hudMapY + (hudMapHeight / 2) - 4, 8, 8, 8, 8, 8, 8, 64, 64);
            context.drawTexture(playerIdentifier, hudMapX + (hudMapWidth / 2) - 4, hudMapY + (hudMapHeight / 2) - 4, 8, 8, 40, 8, 8, 8, 64, 64);
        }

        //0xD9D9D9
        if (!Double.isNaN(direction)) { //skip drawing the compass if direction is NaN (it can be separate of long-lat due to the two-point sampling system)
            for (int i = 2; i >= 0; i--) { //draw the semi-transparent compass background
                context.fill(hudCompassX + i, hudCompassY + i, hudCompassX + hudCompassWidth - i, hudCompassY + 16 - i, 0x33CCCCCC);
            }
            //draw the compass
            context.drawTexture(compassIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16,  Math.round((PlayerAttributes.yaw - direction - ((double) hudCompassWidth / 2))) , 0, hudCompassWidth, 16, 360, 16);
            //draw the snap angle indicator
            if (doSnapAngle) context.drawTexture(snapAngleIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16, Math.round((PlayerAttributes.yaw + snapAngle - ((double) hudCompassWidth / 2))) , 0, hudCompassWidth, 16, 90, 16);
            //context.drawTexture(compassIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16, 0, 0, hudCompassWidth, 16, 360, 16);
            //draw the compass direction needle line thing (i dont have a good name for it)
            context.fill(hudCompassX + hudCompassCenter, hudCompassY, hudCompassX + hudCompassCenter + 1, hudCompassY + 16, 0xFFaa9d94);
        }

    }

}

