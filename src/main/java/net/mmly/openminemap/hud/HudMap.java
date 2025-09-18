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
    static int zoomLevel = 0;
    static Identifier[][] identifiers;
    static double mapTilePosX = 32;
    static double mapTilePosY = 32;
    static Window window = MinecraftClient.getInstance().getWindow();
    static int windowScaledHeight;
    static int windowScaledWidth;
    public static double playerLon;
    public static double playerLat;
    static double playerMapX;
    static double playerMapY;
    public static boolean renderHud = true;
    public static int reloadSkin = 4;

    public static Identifier playerIdentifier = MinecraftClient.getInstance().player.getSkinTextures().texture();

    public static void initialize(DrawContext context) {

    }

    public static void updateX2Y2() {
        hudMapX2 = hudMapX + hudMapWidth;
        hudMapY2 = hudMapY + hudMapHeight;
    }

    public static void zoomIn() {
        if (zoomLevel < 18) {
            zoomLevel++;
            mapTilePosX *= 2;
            mapTilePosY *= 2;
        } else {
            zoomLevel = 18;
        }
    }

    public static void zoomOut() {
        if (zoomLevel > 0) {
            zoomLevel--;
            mapTilePosX = (float) mapTilePosX / 2;
            mapTilePosY = (float) mapTilePosY / 2;
        } else {
            zoomLevel = 0;
        }
    }

    public static void toggle() {
        renderHud = !renderHud;
    }

    public static void render(DrawContext context, RenderTickCounter renderTickCounter) {
        if (reloadSkin > 0) {
            playerIdentifier = MinecraftClient.getInstance().player.getSkinTextures().texture();
            reloadSkin--;
        }
        if (!renderHud) return;
        if (!initialized) initialize(context);
        PlayerAttributes.updatePlayerLocations(MinecraftClient.getInstance());

        windowScaledHeight = window.getScaledHeight();
        windowScaledWidth = window.getScaledWidth();

        identifiers = TileManager.getRangeOfTiles((int) mapTilePosX, (int) mapTilePosY, zoomLevel, hudMapWidth, hudMapHeight, TileManager.hudTileScaledSize);
        int trueHW = TileManager.hudTileScaledSize;
        int[] TopLeftData = TileManager.getTopLeftData();

        context.fill(hudMapX, hudMapY, hudMapX2, hudMapY2, 0, 0xFFCEE1E4);

        if (Double.isNaN(playerLon)) {
            //draw error message and exit
            MutableText text = Text.literal("Out Of Bounds").formatted(Formatting.ITALIC);
            //context.fill(hudMapX + 2, hudMapY + 2, hudMapY + 74, hudMapY + 10, 0xFFFFFFFF);
            context.drawText(MinecraftClient.getInstance().textRenderer, text, hudMapWidth > 73 ? (hudMapX + hudMapX2) / 2 - 37: hudMapX, hudMapHeight > 9 ? (hudMapY + hudMapY2) / 2 - 5 : hudMapY, 0xFFcccccc, true);
            return;
        } else {
            playerMapX = (int) (UnitConvert.longToMx(playerLon, zoomLevel, TileManager.hudTileScaledSize) - mapTilePosX - 4 + ((double) windowScaledWidth / 2));
            playerMapY = (int) (UnitConvert.latToMy(playerLat, zoomLevel, TileManager.hudTileScaledSize) - mapTilePosY - 4 + ((double) windowScaledHeight / 2));
        }

        mapTilePosX = UnitConvert.longToMx(playerLon, zoomLevel, TileManager.hudTileScaledSize);
        mapTilePosY = UnitConvert.latToMy(playerLat, zoomLevel, TileManager.hudTileScaledSize);

        if(!Double.isNaN(playerLat)) {
            for (int i = 0; i < identifiers.length; i++) {
                for (int j = 0; j < identifiers[i].length; j++) {
                    RenderSystem.setShaderTexture(0, identifiers[i][j]);
                    int tileX = ((((TopLeftData[0] + i) * TileManager.hudTileScaledSize) + hudMapX + hudMapWidth / 2) - (int) mapTilePosX);
                    int tileY = ((((TopLeftData[1] + j) * TileManager.hudTileScaledSize) + hudMapY + hudMapHeight / 2) - (int) mapTilePosY);

                    int leftCrop = tileX < hudMapX ? hudMapX - tileX : 0;
                    int topCrop = tileY < hudMapY ? hudMapY - tileY : 0;
                    int rightCrop = tileX + TileManager.hudTileScaledSize > hudMapX + hudMapWidth ? (tileX + TileManager.hudTileScaledSize) - (hudMapX + hudMapWidth) : 0;
                    int bottomCrop = tileY + TileManager.hudTileScaledSize > hudMapY + hudMapHeight ? (tileY + TileManager.hudTileScaledSize) - (hudMapY + hudMapHeight) : 0;

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

    }

}

