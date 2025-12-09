package net.mmly.openminemap.maps;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.Window;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.enums.OverlayVisibility;
import net.mmly.openminemap.event.KeyInputHandler;
import net.mmly.openminemap.gui.DirectionIndicator;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Direction;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.*;
import org.lwjgl.glfw.GLFW;

import javax.imageio.ImageIO;
import javax.swing.plaf.basic.BasicTreeUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

public class OmmMap extends ClickableWidget {

    public final static int TILEMAXZOOM = 18;
    public final static int TILEMAXARTIFICIALZOOM = 24;
    public static final int tileSize = 128;
    public final static int WAYPOINTSIZE = 9;

    private boolean fieldsInitialized = false;
    private MinecraftClient client;
    private Window window;
    private ClientPlayerEntity player;
    private Mouse mouse;

    private int renderAreaX = 0;
    private int renderAreaY = 0;
    private int renderAreaX2 = 0;
    private int renderAreaY2 = 0;
    private int renderAreaWidth = 0;
    private int renderAreaHeight = 0;

    private int zoom = 0;
    private double mapCenterX = 64;
    private double mapCenterY = 64;
    private double playerMapX = 64;
    private double playerMapY = 64;

    private double mousePixelX;
    private double mousePixelY;
    private double mouseX = 0;
    private double mouseY = 0;
    private double mouseTileX = 0;
    private double mouseTileY = 0;

    private double mouseHoldY = 64;
    private double mouseHoldX = 64;

    private int maxZoom = 18;
    private int backgroundColor = 0x00000000;
    private int tintColor = 0x00000000;

    private boolean doArtificialZoom = false;
    private boolean cropMapTiles = true;
    private boolean cropPlayers = true;
    private boolean draggable = false;

    private boolean followPlayer = false;
    private boolean mouseDown = false;
    private boolean mouseIsOutOfBounds = true;

    public MethodInterface rightClickProcedure;
    public MethodInterface leftClickProcedure;
    public BooleanInterface blockZoomProcedure;

    private static Waypoint[] waypoints;

    private void initFields() {
        client = MinecraftClient.getInstance();
        window = client.getWindow();
        player = client.player;
    }

    public OmmMap(int x, int y, int width, int height) {
        super(x, y, width, height, Text.of(""));
        this.setRenderPositionAndSize(x, y, width, height);

    }

    public OmmMap(int x, int y, int width, int height, int zoom, double mapCenterX, double mapCenterY) {
        this(x, y, width, height);
        this.zoom = zoom;
        this.mapCenterX = mapCenterX;
        this.mapCenterY = mapCenterY;
    }

    public static void setWaypoints(Waypoint[] waypoints1) {
        waypoints = waypoints1;
    }

    public static Waypoint[] getWaypoints() {
        return waypoints;
    }

    public void clampZoom() {
        while (zoom > TILEMAXZOOM) {
            zoomOut();
        }
    }

    public void setMapPosition(double x, double y) {
        this.mapCenterX = x;
        this.mapCenterY = y;
    }
    public void setMapZoom(int zoom) {
        if (zoom > this.zoom) {
            while (zoom > this.zoom) {
                zoomIn();
            }
        } else if (zoom < this.zoom) {
            while (zoom < this.zoom) {
                zoomOut();
            }
        }
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }
    public void setCropMapTiles(boolean cropMapTiles) {
        this.cropMapTiles = cropMapTiles;
    }
    public void setCropPlayers(boolean cropPlayers) {
        this.cropPlayers = cropPlayers;
    }
    public void setArtificialZoom(boolean setTo) {
        doArtificialZoom = setTo;
        if (doArtificialZoom) maxZoom = TILEMAXARTIFICIALZOOM;
        else {
            maxZoom = TILEMAXZOOM;
            clampZoom();
        }
    }

    public void setWidthFromRight(int widthFromX, int minValue) {
        setRenderAreaWidth(Math.max(widthFromX, minValue));
    }
    public void setWidthFromLeft(int widthFromX2, int minValue) {
        setRenderSizeInverted(
                Math.max(widthFromX2, minValue),
                renderAreaHeight
        );
    }
    public void setHeightFromBottom(int heightFromY, int minValue) {
        setRenderAreaHeight(Math.max(heightFromY, minValue));
    }
    public void setHeightFromTop(int heightFromY2, int minValue) {
        setRenderSizeInverted(
                renderAreaWidth,
                Math.max(heightFromY2, minValue)
        );
    }

    public double getMapCenterX() {
        return mapCenterX;
    }
    public double getMapCenterY() {
        return mapCenterY;
    }
    public int getZoom() {
        return zoom;
    }

    public void setRenderAreaX(int x) {
        setRenderPosition(x, renderAreaY);
    }
    public void setRenderAreaY(int y) {
        setRenderPosition(renderAreaX, y);
    }
    public void setRenderAreaX2(int x2) {
        setRenderZone(renderAreaX, renderAreaY, x2, renderAreaY2);
    }
    public void setRenderAreaY2(int y2){
        setRenderZone(renderAreaX, renderAreaY, renderAreaX2, y2);
    }
    public void setRenderAreaWidth(int width) {
        setRenderSize(width, renderAreaHeight);
    }
    public void setRenderAreaHeight(int height) {
        setRenderSize(renderAreaWidth, height);
    }

    public void setRenderSize(int width, int height) {
        setRenderPositionAndSize(this.renderAreaX, this.renderAreaY, width, height);
    }
    public void setRenderPosition(int x, int y) {
        setRenderPositionAndSize(x, y, x + this.width, y + this.height);
    }
    public void setRenderPositionAndSize(int x, int y, int width, int height) {
        setRenderZone(x, y, x + width, y + height);
    }
    public void setRenderZone(int x, int y, int x2, int y2) {
        renderAreaX = x;
        renderAreaY = y;
        renderAreaX2 = x2;
        renderAreaY2 = y2;
        renderAreaWidth = x2 - x;
        renderAreaHeight = y2 - y;
        if (x > x2) {
            int a = x;
            x = x2;
            x2 = a;
        }
        if (y > y2) {
            int a = y;
            y = y2;
            y2 = a;
        }
        setDimensionsAndPosition(renderAreaWidth, renderAreaHeight, renderAreaX, renderAreaY);
    }
    public void setRenderSizeInverted(int widthFromRight, int heightFromBottom) {
        setRenderZone(
                renderAreaX2 - widthFromRight,
                renderAreaY2 - heightFromBottom,
                renderAreaX2,
                renderAreaY2
        );
    }

    public int getRenderAreaX() {
        return renderAreaX;
    }
    public int getRenderAreaY() {
        return renderAreaY;
    }
    public int getRenderAreaWidth() {
        return renderAreaWidth;
    }
    public int getRenderAreaHeight() {
        return renderAreaHeight;
    }
    public int getRenderAreaX2() {
        return renderAreaX2;
    }
    public int getRenderAreaY2() {
        return renderAreaY2;
    }

    public double getWidthMidpoint() {
        return renderAreaX + ((double) renderAreaWidth / 2);
    }
    public double getHeightMidpoint() {
        return renderAreaY + ((double) renderAreaHeight / 2);
    }

    public double getMouseX() {
        return mouseX;
    }
    public double getMouseY() {
        return mouseY;
    }
    public double getMouseTileX() {
        return mouseTileX;
    }
    public double getMouseTileY() {
        return mouseTileY;
    }
    public double getMouseLat() {
        return UnitConvert.myToLat(mouseTileY, zoom);
    }
    public double getMouseLong() {
        return UnitConvert.mxToLong(mouseTileX, zoom);
    }

    public void setFollowPlayer(boolean followPlayer) {
        this.followPlayer = followPlayer;
    }

    @Override
    protected void renderWidget(DrawContext context, int mX, int mY, float delta) {
        mouseX = mX;
        mouseY = mY;
        if (fieldsInitialized){
            mousePixelX = mouse.getX();
            mousePixelY = mouse.getY();
        }

        mouseIsOutOfBounds = mouseTileX < 0 || mouseTileY < 0 || mouseTileX > Math.pow(2, zoom + 7) || mouseTileY > Math.pow(2, zoom + 7);
    }

    public void resetMap() {
        followPlayer = false;
        mapCenterY = 64;
        mapCenterX = 64;
        zoom = 0;
    }

    public void keyNavigate(int keyCode, int modifiers) {
        if (!draggable) return;
        //modifiers: bit 1 is shift, bit 2 is control, but 3 is alt

        modifiers %= 4;
        int change = 8;

        if (modifiers < 3) {
            if (modifiers == 2) {
                change = 128;
            }
            if (modifiers == 1) {
                change = 1;
            }
        }

        if (keyCode == GLFW.GLFW_KEY_UP || client.options.forwardKey.matchesKey(keyCode, 0)) {
            mapCenterY -= change;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT || client.options.rightKey.matchesKey(keyCode, 0)) {
            mapCenterX += change;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN || client.options.backKey.matchesKey(keyCode, 0)) {
            mapCenterY += change;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT || client.options.leftKey.matchesKey(keyCode, 0)) {
            mapCenterX -= change;
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { //left click
            mouseDown = true;
            mouseHoldX = mouseTileX;
            mouseHoldY = mouseTileY;
            leftClickProcedure.execute();
        }
        if (button == 1) { //right click
            rightClickProcedure.execute();
        }
        followPlayer = false;
        return false;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        mouseDown = false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount * (TileManager.doReverseScroll ? -1 : 1) > 0) {
            mouseZoomIn();
        } else {
            mouseZoomOut();
        }
        return false;
    }

    public boolean mouseIsDown() {
        return mouseDown;
    }

    public boolean followingPlayer() {
        return followPlayer;
    }

    public boolean mouseIsOutOfBounds() {
       // return mouseTilePosX < 0 || mouseTilePosY < 0 || mouseTilePosX > Math.pow(2, zoom + 7) || mouseTilePosY > Math.pow(2, zoom + 7);;
        return mouseIsOutOfBounds;
    }

    public void zoomIn() {
        if (zoom + 1 > maxZoom) return;
        else {
            zoom++;
            mapCenterX *= 2;
            mapCenterY *= 2;
        }
    }
    public void zoomOut() {
        if (zoom - 1 < 0) return;
        else {
            zoom--;
            mapCenterX /= 2;
            mapCenterY /= 2;
        }
    }

    public void mouseZoomIn() {
        if (blockZoomProcedure.evaluate()) return;
        updateFields();

        if (zoom >= (doArtificialZoom ? TILEMAXARTIFICIALZOOM : TILEMAXZOOM)) return;

        if (!followPlayer) {
            mapCenterX -= (mapCenterX - mouseTileX) / 2;
            mapCenterY -= (mapCenterY - mouseTileY) / 2;
        }

        zoomIn();

    }
    public void mouseZoomOut() {
        if (blockZoomProcedure.evaluate()) return;
        updateFields();

        if (zoom <= 0) return;

        if (!followPlayer) {
            mapCenterX += (mapCenterX - mouseTileX);
            mapCenterY += (mapCenterY - mouseTileY);
        }

        zoomOut();

    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        // play no sound
    }

    private void drawMap(DrawContext context) {

        context.fill(renderAreaX, renderAreaY, renderAreaX2, renderAreaY2, backgroundColor);
        context.fill(renderAreaX, renderAreaY, renderAreaX2, renderAreaY2, tintColor);

        DrawableMapTile[][] tiles = TileManager.getRangeOfDrawableTiles((int) mapCenterX, (int) mapCenterY, zoom, renderAreaWidth, renderAreaHeight, tileSize);
        for (DrawableMapTile[] column : tiles) {
            for (DrawableMapTile tile : column) {
                drawTile(context, tile);
            }
        }



        //context.fill(renderAreaX, renderAreaY, renderAreaX2, renderAreaY2, 0x33FFFFFF);
    }

    public void setBackgroundColor(int red, int green, int blue, int alpha) {
        backgroundColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    public void setBackgroundColor(int argb) {
        backgroundColor = argb;
    }
    public void setTintColor(int red, int green, int blue, int alpha) {
        tintColor = (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    public void setTintColor(int argb) {
        tintColor = argb;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    private void drawBufferedPlayer(DrawContext context, BufferedPlayer bufferedPlayer) {

        //get position of player relative to te screen (number passed to draw methods)
        int relativeX = (renderAreaWidth / 2) - 4 + bufferedPlayer.offsetX + renderAreaX;
        int relativeY = (renderAreaHeight / 2) - 4 + bufferedPlayer.offsetY + renderAreaY;

        //if outside render area in positive direction (right/down), return
        if (relativeX > renderAreaX2 || relativeY > renderAreaY2) return;

        //variable initialization
        int width = 8;
        int height = 8;
        int u = 8;
        int v = 8;
        int regionWidth = 8;
        int regionHeight = 8;


        /*
        //crop leftward edge
        if (relativeX < renderAreaX) {
            int difference = renderAreaX - relativeX;
            relativeX += difference;
            width -= difference;
            u += difference;
            regionWidth -= difference;
        }


        //crop upward edge
        if (relativeY < renderAreaY) {
            int difference = renderAreaY - relativeY;
            relativeY += difference;
            height -= difference;
            v += difference;
            regionHeight -= difference;
        }

        //crop rightward edge
        if (relativeX + width > renderAreaX2) {
            int difference = (relativeX + width) - renderAreaX2;
            width -= difference;
            regionWidth -= difference;
        }

        //crop downward edge
        if (relativeY + height > renderAreaY2) {
            int difference = (relativeY + height) - renderAreaY2;
            height -= difference;
            regionHeight -= difference;
        }

        if (regionWidth <= 0 || regionHeight <= 0) return;


         */

        //draw player

        //context.fill(relativeX, relativeY, relativeX + 8, relativeY + 8, 0xFF000000);

        context.drawTexture(bufferedPlayer.texture, relativeX, relativeY, width, height, u, v, regionWidth, regionHeight, 64, 64);
        context.drawTexture(bufferedPlayer.texture, relativeX, relativeY, width, height, u + 32, v, regionWidth, regionHeight, 64, 64);

        //do altitude shading if enabled, return early if not enabled
        if (!Objects.equals("on", ConfigFile.readParameter(ConfigOptions.ALTITUDE_SHADING)) || Double.isNaN(bufferedPlayer.y)) return;
        double altitudeOffset = bufferedPlayer.y - PlayerAttributes.altitude;
        int alpha = (int) (Math.clamp(Math.abs(altitudeOffset) -16, 0, 80) * 1.5);
        if (altitudeOffset > 0) {
            context.fill(
                    relativeX, relativeY, relativeX + width, relativeY + height,
                    UnitConvert.argb(alpha, 255, 255, 255)
            );
        } else {
            context.fill(
                    relativeX, relativeY, relativeX + width, relativeY + height,
                    UnitConvert.argb(alpha, 0, 0, 0)
            );
        }

    }

    private void drawWaypoint(DrawContext context) {

    }

    private BufferedPlayer drawDirectionIndicator(DrawContext context, PlayerEntity playerDraw, boolean indicatorsOnly) {
        //Draws a direction indicator
        //May also return a BufferedPlayer if other players are to be drawn

        //Convert mc coordinates to geo coordinates, returning null if the conversion fails
        double[] geoCoords;
        try {
            geoCoords = Projection.to_geo(playerDraw.getX(), playerDraw.getZ());
        } catch (CoordinateValueError e) {
            return null;
        }
        if (Double.isNaN(geoCoords[0])) return null;

        //convert geo coordinates to map coordinates
        double mapX = UnitConvert.longToMapX(geoCoords[1], zoom, tileSize);
        double mapY = UnitConvert.latToMapY(geoCoords[0], zoom, tileSize);

        //calculate player offset
        int mapCenterOffsetX = (int) Math.round(mapX - mapCenterX);
        int mapCenterOffsetY = (int) Math.round(mapY - mapCenterY);

        //get player texture
        Identifier playerTexture = PlayersManager.playerSkinList.get(playerDraw.getUuid());
        if (playerTexture == null) playerTexture = Identifier.of("openminemap", "skinbackup.png");

        //calculate the direction the player is facing
        double direction = playerDraw.getYaw() - Direction.calcDymaxionAngleDifference();

        //Draw a direction indicator if the direction is a valid number and visibility permission is adequete
        if (OverlayVisibility.checkPermissionFor(TileManager.showDirectionIndicators, OverlayVisibility.LOCAL) && !Double.isNaN(direction))
            DirectionIndicator.draw(
                    context,
                    direction,
                    renderAreaX + (renderAreaWidth / 2) - 12 + mapCenterOffsetX,
                    renderAreaY + (renderAreaHeight / 2) - 12 + mapCenterOffsetY,
                    indicatorsOnly
            );

        return new BufferedPlayer(mapCenterOffsetX, mapCenterOffsetY, playerTexture, player.getY());
    }

    private int roundTowardsZero(double num) {
        return num < 0 ?
                (int) Math.ceil(num) :
                (int) num;
    }

    private void drawTile(DrawContext context, DrawableMapTile tile) {

        //offset the tiles so that the map is centered on the render area
        int relativeX = (roundTowardsZero(tile.x) + renderAreaX + (int) (((double) renderAreaWidth / 2) - mapCenterX));
        int relativeY = (roundTowardsZero(tile.y) + renderAreaY + (int) (((double) renderAreaHeight / 2) - mapCenterY));

        //if out of bounds in the positive (right/down) direction, return
        if (relativeX > renderAreaX2 || relativeY > renderAreaY2) return;

        //calculate u and v based on sub tile position
        int u = tileSize * tile.subSectionX;
        int v = tileSize * tile.subSectionY;

        //calculate scale multiplier, will be a power of 2
        int scaleMultiplier = tileSize / tile.subSectionSize;

        // texture width doubles for every offset of zoom level:
        // 0 zoom tile for zoom 0 : scaleMult = 1
        // 0 zoom tile for zoom 1 : scaleMult = 2
        // 0 zoom tile for zoom 2 : scaleMult = 4
        // etc.
        int textureWidth = tileSize * scaleMultiplier;
        int textureHeight = tileSize * scaleMultiplier;

        // define region + normal width and height to be modified momentarily (if needed)
        int regionWidth = tileSize;
        int regionHeight = tileSize;
        int width = tileSize;
        int height = tileSize;

        /*
        //crop leftward edge
        if (relativeX < renderAreaX) {
            int difference = renderAreaX - relativeX;
            relativeX += difference;
            width -= difference;
            u += difference;
            regionWidth -= difference;
        }

        //crop upward edge
        if (relativeY < renderAreaY) {
            int difference = renderAreaY - relativeY;
            relativeY += difference;
            height -= difference;
            v += difference;
            regionHeight -= difference;
        }

        //crop rightward edge
        if (relativeX + width > renderAreaX2) {
            int difference = (relativeX + width) - renderAreaX2;
            width -= difference;
            regionWidth -= difference;
        }

        //crop downward edge
        if (relativeY + height > renderAreaY2) {
            int difference = (relativeY + height) - renderAreaY2;
            height -= difference;
            regionHeight -= difference;
        }



        if (regionWidth <= 0 || regionHeight <= 0) return;
        */

        context.drawTexture(
                tile.identifier,
                relativeX,
                relativeY,
                width,
                height,
                u,
                v,
                regionWidth,
                regionHeight,
                textureWidth,
                textureHeight
        );

        //context.drawBorder(relativeX, relativeY, width, height, 0xFF000000);
    }

    private void updateFields() {
        //any variable changes/calculations go here
        if (mouseDown) {
            mapCenterX = mouseHoldX + (mapCenterX - mouseTileX);
            mapCenterY = mouseHoldY + (mapCenterY - mouseTileY);
        }

        if (followPlayer) {
            mapCenterX = UnitConvert.longToMapX(PlayerAttributes.getLongitude(), zoom, tileSize);
            mapCenterY = UnitConvert.latToMapY(PlayerAttributes.getLatitude(), zoom, tileSize);
        }

        //update player map position
        PlayerAttributes.updatePlayerAttributes(client);
        if (!PlayerAttributes.positionIsValid()) {
            playerMapX = -9999;
            playerMapY = -9999;
        } else {
            playerMapX = (int) (UnitConvert.longToMapX(PlayerAttributes.getLongitude(), zoom, tileSize) - mapCenterX - 4 + ((double) renderAreaWidth / 2));
            playerMapY = (int) (UnitConvert.latToMapY(PlayerAttributes.getLatitude(), zoom, tileSize) - mapCenterY - 4 + ((double) renderAreaHeight / 2));
        }

        if (mapCenterX < 0) 
            mapCenterX = 0;
        if (mapCenterY < 0) 
            mapCenterY = 0;
        if (mapCenterX > tileSize * Math.pow(2, zoom))
            mapCenterX = tileSize * Math.pow(2, zoom);
        if (mapCenterY > tileSize * Math.pow(2, zoom))
            mapCenterY = tileSize * Math.pow(2, zoom);

        mouseTileX = mapCenterX + mouseX - ((double) renderAreaWidth / 2);
        mouseTileY = mapCenterY + mouseY - ((double) renderAreaHeight / 2);


    }

    private void drawClientPlayerCentered(DrawContext context) {
        context.drawTexture(
                PlayerAttributes.getIdentifier(),
                renderAreaX + (renderAreaWidth / 2) - 4,
                renderAreaY + (renderAreaHeight / 2) - 4,
                8, 8,
                8, 8,
                8, 8,
                64, 64
        );
        context.drawTexture(
                PlayerAttributes.getIdentifier(),
                renderAreaX + (renderAreaWidth / 2) - 4,
                renderAreaY + (renderAreaHeight / 2) - 4,
                8, 8,
                32, 8,
                8, 8,
                64, 64
        );
    }

    public void renderMap(DrawContext context, RenderTickCounter renderTickCounter) {

        if (!fieldsInitialized) initFields();
        updateFields();

        context.enableScissor(renderAreaX, renderAreaY, renderAreaX2, renderAreaY2);
        drawMap(context); //draw the map tiles + background

        if (TileManager.doWaypoints) {
            for (Waypoint waypoint : waypoints) {
                if (!waypoint.visible) continue;
                context.drawTexture(
                        waypoint.identifier,
                        (int) (((double) renderAreaWidth / 2) - 4 + (waypoint.getMapX(zoom) - mapCenterX)) + renderAreaX,
                        (int) (((double) renderAreaHeight / 2) - 4 + (waypoint.getMapY(zoom) - mapCenterY)) + renderAreaY,
                        WAYPOINTSIZE,
                        WAYPOINTSIZE,
                        0,
                        0,
                        WAYPOINTSIZE,
                        WAYPOINTSIZE,
                        WAYPOINTSIZE,
                        WAYPOINTSIZE
                );
            }
        }

        ArrayList<BufferedPlayer> players = new ArrayList<>();
        //draw other players' direction indicators
        for (PlayerEntity player : PlayersManager.getNearPlayers()) {
            if (player.getUuid().equals(client.player.getUuid())) continue; //self player should be drawn last so that it's on top, so don't draw it here
            players.add(drawDirectionIndicator(
                    context,
                    player,
                    !OverlayVisibility.checkPermissionFor(
                            TileManager.showPlayers,
                            OverlayVisibility.LOCAL)
                    )
            );
        }

        if (OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.LOCAL)) {
            for (BufferedPlayer bufferedPlayer : players) {
                if (player == null) continue;
                drawBufferedPlayer(context, bufferedPlayer);
            }
        }

        BufferedPlayer self = null;

        if (OverlayVisibility.checkPermissionFor(TileManager.showDirectionIndicators, OverlayVisibility.SELF)) {
            if (followPlayer) {
                DirectionIndicator.draw(
                        context,
                        PlayerAttributes.geoYaw,
                        renderAreaX + (renderAreaWidth / 2) - 12,
                        renderAreaY + (renderAreaHeight / 2) - 12,
                        !OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.SELF)
                );
            } else {
                self = drawDirectionIndicator(context, player, !OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.SELF));
            }

        }

        if (OverlayVisibility.checkPermissionFor(TileManager.showPlayers, OverlayVisibility.SELF)) {
            if (followPlayer) {
                drawClientPlayerCentered(context);
            } else {
                drawBufferedPlayer(context, self);
            }
        }

        context.disableScissor();

    }

}