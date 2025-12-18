package net.mmly.openminemap.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.projection.Direction;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.Waypoint;
import net.mmly.openminemap.util.WaypointFile;

public class HudMap {

    public static final int MIN_SIZE = 20;
    static boolean initialized = false;
    public static boolean renderHud = Boolean.parseBoolean(ConfigFile.readParameter(ConfigOptions._HUD_TOGGLE)); //is toggled by the keybind
    public static boolean hudEnabled = Boolean.parseBoolean(ConfigFile.readParameter(ConfigOptions._HUD_ENABLED)); //is toggled by the fullscreen map button and is dominant over the keybind
    public static int hudCompassX = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_X));
    public static int hudCompassY = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_Y));
    public static int hudCompassWidth = Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_COMPASS_WIDTH));
    protected static Identifier compassIdentifier = Identifier.of("openminemap", "stripcompass.png");
    protected static Identifier snapAngleIdentifier = Identifier.of("openminemap", "snapangle.png");
    protected static int hudCompassCenter;
    static double snapAngleInput;
    public static double snapAngle; //range: (-90, 0]
    public static double direction;
    public static boolean doSnapAngle = false;
    public static final OmmMap map = new OmmMap(
            Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_X)),
            Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_Y)),
            Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_WIDTH)),
            Integer.parseInt(ConfigFile.readParameter(ConfigOptions.HUD_MAP_HEIGHT))
    );

    public static Identifier playerIdentifier;

    public static void clampZoom() {
        //used to decrease zoom level (if needed) when artificial zoom is disabled
       map.clampZoom();
    }

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
        //TileManager.initializeConfigParameters();
        setSnapAngle();

        map.setFollowPlayer(true);
        map.setArtificialZoom(TileManager.doArtificialZoom);
        map.setMapZoom(
                Integer.parseInt(ConfigFile.readParameter(ConfigOptions._HUD_LAST_ZOOM))
        );
        map.setTextRenderer(MinecraftClient.getInstance().textRenderer);
        map.doPlayerTooltipNames(true);

        initialized = true;
    }

    public static void zoomIn() {
        map.zoomIn(1);
        ConfigFile.writeParameter(ConfigOptions._HUD_LAST_ZOOM, Double.toString(map.getZoom()));
    }

    public static void zoomOut() {
        map.zoomOut(1);
        ConfigFile.writeParameter(ConfigOptions._HUD_LAST_ZOOM, Double.toString(map.getZoom()));
    }

    public static void toggleRendering() {
        if (!hudEnabled) return;
        renderHud = !renderHud;
        ConfigFile.writeParameter(ConfigOptions._HUD_TOGGLE, Boolean.toString(renderHud));
        ConfigFile.writeToFile();
    }

    public static void toggleEnabled() {
        hudEnabled = !hudEnabled;
        if (hudEnabled && !renderHud) toggleRendering();
        ConfigFile.writeParameter(ConfigOptions._HUD_ENABLED, Boolean.toString(hudEnabled));
        ConfigFile.writeToFile();
    }

    private static void drawCompass(DrawContext context) {
        for (int i = 2; i >= 0; i--) { //draw the semi-transparent compass background
            context.fill(hudCompassX + i, hudCompassY + i, hudCompassX + hudCompassWidth - i, hudCompassY + 16 - i, 0x33CCCCCC);
        }
        //draw the compass
        context.drawTexture(compassIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16, (float) (PlayerAttributes.yaw - direction - ((double) hudCompassWidth / 2)), 0, hudCompassWidth, 16, 360, 16);
        //draw the snap angle indicator
        if (doSnapAngle) context.drawTexture(snapAngleIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16, (float) (PlayerAttributes.yaw + snapAngle - ((double) hudCompassWidth / 2)) , 0, hudCompassWidth, 16, 90, 16);
        //context.drawTexture(compassIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16, 0, 0, hudCompassWidth, 16, 360, 16);
        //draw the compass direction needle line thing (i dont have a good name for it)
        context.fill(hudCompassX + hudCompassCenter, hudCompassY, hudCompassX + hudCompassCenter + 1, hudCompassY + 16, 0xFFaa9d94);

    }

    public static void render(DrawContext context, RenderTickCounter renderTickCounter) {
        while (!OpenMineMapClient.debugMessages.isEmpty()) {
            if (OpenMineMapClient.debugMessages.getFirst() != null) MinecraftClient.getInstance().player.sendMessage(Text.literal(OpenMineMapClient.debugMessages.getFirst()).formatted(Formatting.RED));
            OpenMineMapClient.debugMessages.removeFirst();
        }

        if (!renderHud || !hudEnabled || MinecraftClient.getInstance().options.hudHidden) return; //do not do anything if hud rendering is disabled

        if (TileManager.themeColor == 0xFF808080) TileManager.loadTopTile();

        playerIdentifier = MinecraftClient.getInstance().player.getSkinTextures().texture();

        if (!initialized) initialize(context); //initialize hudmap if not done already

        PlayerAttributes.updatePlayerAttributes(MinecraftClient.getInstance()); //refreshes values for geographic longitude, latitude and yaw
        direction = Direction.calcDymaxionAngleDifference(); //the difference between mc yaw and geo yaw
        hudCompassCenter = Math.round((float) hudCompassWidth / 2); //center of the hud compass

        map.setBackgroundColor(TileManager.themeColor);
        map.setTintColor(0x10000000);

        if (Double.isNaN(PlayerAttributes.getLongitude())) {//if the player is out of bounds this will be NaN. all other rendering is skipped due to this
            //draw error message and exit
            MutableText text = Text.literal("Out Of Bounds").formatted(Formatting.ITALIC);
            //context.fill(hudMapX + 2, hudMapY + 2, hudMapY + 74, hudMapY + 10, 0xFFFFFFFF);
            context.fill(map.getRenderAreaX(), map.getRenderAreaY(), map.getRenderAreaX2(), map.getRenderAreaY2(), 0, TileManager.themeColor);
            context.fill(map.getRenderAreaX(), map.getRenderAreaY(), map.getRenderAreaX2(), map.getRenderAreaY2(), 0, 0x10000000);
            context.drawText(MinecraftClient.getInstance().textRenderer, text, map.getRenderAreaWidth() > 73 ? (map.getRenderAreaX() + map.getRenderAreaX2()) / 2 - 37: map.getRenderAreaX(), map.getRenderAreaHeight() > 9 ? (map.getRenderAreaY() + map.getRenderAreaY2()) / 2 - 5 : map.getRenderAreaY(), 0xFFcccccc, true);
            return;
        }

        map.setArtificialZoom(TileManager.doArtificialZoom);
        map.renderMap(context, null);

        /*
        int scaleMultiplier = (int) Math.pow(2, trueZoomLevel - zoomLevel);
        DrawableMapTile[][] tiles = TileManager.getRangeOfDrawableTiles((int) mapTilePosX, (int) mapTilePosY, zoomLevel, hudMapWidth, hudMapHeight, renderTileSize);

        //draw map tiles, cropping when needed

        for (DrawableMapTile[] column : tiles) {
            for (DrawableMapTile tile : column) {
                int tileX = (int) ((tile.x + hudMapX + (double) hudMapWidth / 2) - (int) mapTilePosX);
                int tileY = (int) ((tile.y + hudMapY + (double) hudMapHeight / 2) - (int) mapTilePosY);

                int leftCrop = tileX < hudMapX ? hudMapX - tileX : 0;
                int topCrop = tileY < hudMapY ? hudMapY - tileY : 0;
                int rightCrop = tileX + renderTileSize > hudMapX + hudMapWidth ? (tileX + renderTileSize) - (hudMapX + hudMapWidth) : 0;
                int bottomCrop = tileY + renderTileSize > hudMapY + hudMapHeight ? (tileY + renderTileSize) - (hudMapY + hudMapHeight) : 0;

                //x, y define where the defined top left corner will go
                //u, v define the lop left corner of the texture
                //w, h crop the texture from right and down
                //texturewidth and textureheight should equal the scale of the tiles (64 here)

                if (renderTileSize - rightCrop - leftCrop < 0 || renderTileSize - bottomCrop - topCrop < 0) continue;

                double u = (tile.subSectionSize * tile.subSectionX) + ((double) leftCrop / ((double) renderTileSize / tile.subSectionSize)) * scaleMultiplier;
                double v = (tile.subSectionSize * tile.subSectionY) + ((double) topCrop / ((double) renderTileSize / tile.subSectionSize)) * scaleMultiplier;

                double regionWidth = (tile.subSectionSize - ((double) (rightCrop + leftCrop) / ((double) renderTileSize / tile.subSectionSize))) * scaleMultiplier;
                double regionHeight = (tile.subSectionSize - ((double) (bottomCrop + topCrop) / ((double) renderTileSize / tile.subSectionSize))) * scaleMultiplier;

                context.drawTexture(
                        tile.identifier,
                        tileX + leftCrop,
                        tileY + topCrop,
                        renderTileSize - rightCrop - leftCrop,
                        renderTileSize - bottomCrop - topCrop,
                        (float) u,
                        (float) v,
                        (int) regionWidth,
                        (int) regionHeight,
                        renderTileSize,
                        renderTileSize
                );
            }
        }

        //draw all players (except self)
        PlayersManager.updatePlayerSkinList();
        ArrayList<BufferedPlayer> players = new ArrayList<>();
        for (PlayerEntity player : PlayersManager.getNearPlayers()) {
            //direction indicators need to be drawn before players. To accomplish this, bufferedPlayer classes store the values needed to draw the players later
            players.add(drawDirectionIndicatorsToMap(context, player, !OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.LOCAL)));
        }
        //now that direction indicators have been drawn, players can be drawn
        if (OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.LOCAL))
            drawBufferedPlayersToMap(context, players);

        boolean indicatorOnly = !OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.SELF);

        //draw the direction indicator
        //context.fill(hudMapX + (hudMapWidth / 2) - 12, hudMapY + (hudMapHeight / 2) - 12,hudMapX + (hudMapWidth / 2) - 12 + 24, hudMapY + (hudMapHeight / 2) - 12 + 24, 0xFFFFFFFF);
        if (directionIndicator.loadSuccess && OverlayVisibility.checkPermissionFor(TileManager.showDirectionIndicators, OverlayVisibility.SELF))
            DirectionIndicator.draw(context, PlayerAttributes.geoYaw,hudMapX + (hudMapWidth / 2) - 12, hudMapY + (hudMapHeight / 2) - 12, true, indicatorOnly);
        //if (directionIndicator.updateDynamicTexture() && directionIndicator.loadSuccess) context.drawTexture(directionIndicator.textureId, hudMapX + (hudMapWidth / 2) - 12, hudMapY + (hudMapHeight / 2) - 12, 0, 0, 24, 24, 24, 24);

        //draw the player; two draw statements are used in order to display both skin layers
        if (OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.SELF)) {
            context.drawTexture(playerIdentifier, hudMapX + (hudMapWidth / 2) - 4, hudMapY + (hudMapHeight / 2) - 4, 8, 8, 8, 8, 8, 8, 64, 64);
            context.drawTexture(playerIdentifier, hudMapX + (hudMapWidth / 2) - 4, hudMapY + (hudMapHeight / 2) - 4, 8, 8, 40, 8, 8, 8, 64, 64);
        }

        */

        //0xD9D9D9
        if (!Double.isNaN(direction)) { //skip drawing the compass if direction is NaN (it can be separate of long-lat due to the two-point sampling system)
            drawCompass(context);
        }

    }

}

