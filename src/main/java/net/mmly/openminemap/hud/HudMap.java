package net.mmly.openminemap.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.projection.Direction;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.UnitConvert;

public class HudMap {

    public static int hudMapX = Integer.parseInt(ConfigFile.readParameter("HudMapX"));
    public static int hudMapY = Integer.parseInt(ConfigFile.readParameter("HudMapY"));
    public static int hudMapWidth = Integer.parseInt(ConfigFile.readParameter("HudMapWidth"));
    public static int hudMapHeight = Integer.parseInt(ConfigFile.readParameter("HudMapHeight"));
    public static int hudMapX2 = hudMapX + hudMapWidth;
    public static int hudMapY2 = hudMapY + hudMapHeight;
    static boolean initialized = false;
    public static int trueZoomLevel = Integer.parseInt(ConfigFile.readParameter("§hudlastzoom"));
    static int zoomLevel = Math.min(trueZoomLevel, 18); //essentially decides what tile size folder to pull from
    static Identifier[][] identifiers;
    static double mapTilePosX = 64;
    static double mapTilePosY = 64;
    static Window window = MinecraftClient.getInstance().getWindow();
    static int windowScaledHeight;
    static int windowScaledWidth;
    public static double playerLon;
    public static double playerLat;
    static double playerMapX;
    static double playerMapY;
    public static boolean renderHud = Boolean.parseBoolean(ConfigFile.readParameter("§hudtoggle"));
    public static int reloadSkin = 4;
    public static int hudCompassX = Integer.parseInt(ConfigFile.readParameter("HudCompassX"));
    public static int hudCompassY = Integer.parseInt(ConfigFile.readParameter("HudCompassY"));
    public static int hudCompassWidth = Integer.parseInt(ConfigFile.readParameter("HudCompassWidth"));
    protected static Identifier compassIdentifier = Identifier.of("openminemap", "stripcompass.png");
    protected static Identifier snapAngleIdentifier = Identifier.of("openminemap", "snapangle.png");
    protected static int hudCompassCenter;
    static double snapAngleInput;
    public static double snapAngle; //range: (-90, 0]
    public static double direction;
    static boolean doSnapAngle = false;

    // used in conjunction with artificial zoom mode;
    // TileManager.hudTileScaledSize does not get updated with artificial zoom, this variable does instead
    // If artificialZoom is off, this variable should always equal TileManager.hudTileScaledSize
    static int renderTileSize;

    public static Identifier playerIdentifier;

    public static void setSnapAngle() {
        String receivedSnapAngle = ConfigFile.readParameter("SnapAngle");
        if (receivedSnapAngle.isEmpty()) {
            doSnapAngle = false;
        } else {
            doSnapAngle = true;
            snapAngleInput = Double.parseDouble(receivedSnapAngle);
            snapAngle = ((-snapAngleInput) % 90) - (90 * (((-snapAngleInput) % 90) > 0 ? 1 : 0));
        }
    }

    public static void initialize(DrawContext context) {
        TileManager.setArtificialZoom();
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
        ConfigFile.writeParameter("§hudlastzoom", Integer.toString(trueZoomLevel));
        ConfigFile.writeToFile();
    }

    public static void toggleRendering() {
        renderHud = !renderHud;
        ConfigFile.writeParameter("§hudtoggle", Boolean.toString(renderHud));
        ConfigFile.writeToFile();
    }

    public static void render(DrawContext context, RenderTickCounter renderTickCounter) {
        if (!renderHud) return;
        if (reloadSkin > 0) {
            playerIdentifier = MinecraftClient.getInstance().player.getSkinTextures().texture();
            reloadSkin--;
        }

        if (!initialized) initialize(context);
        PlayerAttributes.updatePlayerAttributes(MinecraftClient.getInstance());
        direction = Direction.calcDymaxionAngleDifference();
        hudCompassCenter = Math.round((float) hudCompassWidth / 2);

        windowScaledHeight = window.getScaledHeight();
        windowScaledWidth = window.getScaledWidth();

        renderTileSize = (int) Math.max(TileManager.hudTileScaledSize, Math.pow(2, trueZoomLevel - 11));
        //System.out.println("TrueZoom: "+trueZoomLevel+" | Zoom: "+zoomLevel+" | calced size: "+Math.pow(2, trueZoomLevel - 11));

        identifiers = TileManager.getRangeOfTiles((int) mapTilePosX, (int) mapTilePosY, zoomLevel, hudMapWidth, hudMapHeight, renderTileSize);
        int trueHW = renderTileSize;
        int[] TopLeftData = TileManager.getTopLeftData();

        //System.out.println(identifiers.length + ", " + identifiers[0].length);

        context.fill(hudMapX, hudMapY, hudMapX2, hudMapY2, 0, 0xFFCEE1E4);

        if (Double.isNaN(playerLon)) {
            //draw error message and exit
            MutableText text = Text.literal("Out Of Bounds").formatted(Formatting.ITALIC);
            //context.fill(hudMapX + 2, hudMapY + 2, hudMapY + 74, hudMapY + 10, 0xFFFFFFFF);
            context.drawText(MinecraftClient.getInstance().textRenderer, text, hudMapWidth > 73 ? (hudMapX + hudMapX2) / 2 - 37: hudMapX, hudMapHeight > 9 ? (hudMapY + hudMapY2) / 2 - 5 : hudMapY, 0xFFcccccc, true);
            return;
        } else {
            playerMapX = (int) (UnitConvert.longToMx(playerLon, zoomLevel, renderTileSize) - mapTilePosX - 4 + ((double) windowScaledWidth / 2));
            playerMapY = (int) (UnitConvert.latToMy(playerLat, zoomLevel, renderTileSize) - mapTilePosY - 4 + ((double) windowScaledHeight / 2));
        }

        mapTilePosX = UnitConvert.longToMx(playerLon, zoomLevel, renderTileSize);
        mapTilePosY = UnitConvert.latToMy(playerLat, zoomLevel, renderTileSize);

        if(!Double.isNaN(playerLat)) {
            for (int i = 0; i < identifiers.length; i++) {
                for (int j = 0; j < identifiers[i].length; j++) {
                    RenderSystem.setShaderTexture(0, identifiers[i][j]);
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
                    context.drawTexture(identifiers[i][j], tileX + leftCrop, tileY + topCrop, leftCrop, topCrop, trueHW - rightCrop - leftCrop, trueHW - bottomCrop - topCrop, trueHW, trueHW);
                }
            }
            context.drawTexture(playerIdentifier, hudMapX + (hudMapWidth / 2) - 4, hudMapY + (hudMapHeight / 2) - 4, 8, 8,8,8, 8, 8, 64, 64);
            context.drawTexture(playerIdentifier, hudMapX + (hudMapWidth / 2) - 4, hudMapY + (hudMapHeight / 2) - 4, 8, 8,40,8, 8, 8, 64, 64);
        } else {

        }

        //0xD9D9D9
        if (!Double.isNaN(direction)) {
            for (int i = 2; i >= 0; i--) {
                context.fill(hudCompassX + i, hudCompassY + i, hudCompassX + hudCompassWidth - i, hudCompassY + 16 - i, 0x33CCCCCC);
            }
            //System.out.println(Direction.playerMcDirection);
            //System.out.println(PlayerAttributes.yaw);
            context.drawTexture(compassIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16,  Math.round((PlayerAttributes.yaw - direction - ((double) hudCompassWidth / 2))) , 0, hudCompassWidth, 16, 360, 16);
            if (doSnapAngle) context.drawTexture(snapAngleIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16, Math.round((PlayerAttributes.yaw - direction + snapAngle - ((double) hudCompassWidth / 2))) , 0, hudCompassWidth, 16, 90, 16);
            //context.drawTexture(compassIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16, 0, 0, hudCompassWidth, 16, 360, 16);
            context.fill(hudCompassX + hudCompassCenter, hudCompassY, hudCompassX + hudCompassCenter + 1, hudCompassY + 16, 0xFFaa9d94);
        }

    }

}

