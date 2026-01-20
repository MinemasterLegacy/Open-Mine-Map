package net.mmly.openminemap.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.config.MapConfigScreen;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.RequestManager;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.projection.Direction;
import net.mmly.openminemap.util.ConfigFile;
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
                Double.parseDouble(ConfigFile.readParameter(ConfigOptions._HUD_LAST_ZOOM))
        );
        map.setTextRenderer(MinecraftClient.getInstance().textRenderer);
        map.doPlayerTooltipNames(false);

        initialized = true;
        WaypointFile.setWaypointsOfThisWorld(true);
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
        drawCompassBackground(context);
        //draw the compass
        context.drawTexture(compassIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16, (float) (PlayerAttributes.yaw - direction - ((double) hudCompassWidth / 2)), 0, hudCompassWidth, 16, 360, 16);
        //draw the snap angle indicator
        if (doSnapAngle) context.drawTexture(snapAngleIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16, (float) (PlayerAttributes.yaw + snapAngle - ((double) hudCompassWidth / 2)) , 0, hudCompassWidth, 16, 90, 16);
        //context.drawTexture(compassIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16, 0, 0, hudCompassWidth, 16, 360, 16);
        //draw the compass direction needle line thing (i dont have a good name for it)
        context.fill(hudCompassX + hudCompassCenter, hudCompassY, hudCompassX + hudCompassCenter + 1, hudCompassY + 16, 0xFFaa9d94);

    }

    private static void drawCompassBackground(DrawContext context) {
        for (int i = 2; i >= 0; i--) { //draw the semi-transparent compass background
            context.fill(hudCompassX + i, hudCompassY + i, hudCompassX + hudCompassWidth - i, hudCompassY + 16 - i, 0x33CCCCCC);
        }
    }

    public static void render(DrawContext context, RenderTickCounter renderTickCounter) {
        //method is called every frame, so a couple of things are included here that need to run every frame
        while (!OpenMineMapClient.debugMessages.isEmpty()) {
            if (OpenMineMapClient.debugMessages.getFirst() != null) MinecraftClient.getInstance().player.sendMessage(Text.literal(OpenMineMapClient.debugMessages.getFirst()).formatted(Formatting.RED));
            OpenMineMapClient.debugMessages.removeFirst();
        }

        RequestManager.setMapType(MinecraftClient.getInstance().currentScreen == null);

        //now do actuall hudmap stuff
        if (!initialized) initialize(context); //initialize hudmap if not done already

        if ((!renderHud || !hudEnabled || MinecraftClient.getInstance().options.hudHidden) && !(MinecraftClient.getInstance().currentScreen instanceof MapConfigScreen)) return; //do not do anything if hud rendering is disabled

        if (TileManager.themeColor == 0xFF808080) TileManager.loadTopTile();

        playerIdentifier = MinecraftClient.getInstance().player.getSkinTextures().texture();

        PlayerAttributes.updatePlayerAttributes(MinecraftClient.getInstance()); //refreshes values for geographic longitude, latitude and yaw
        direction = Direction.calcDymaxionAngleDifference(); //the difference between mc yaw and geo yaw
        hudCompassCenter = Math.round((float) hudCompassWidth / 2); //center of the hud compass

        map.setBackgroundColor(TileManager.themeColor);
        map.setTintColor(0x10000000);

        if (!PlayerAttributes.positionIsValid()) {//if the player is out of bounds this will be NaN. all other rendering is skipped due to this
            //draw error message and exit
            Text text = Text.translatable("omm.hud.out-of-bounds").formatted(Formatting.ITALIC);
            //context.fill(hudMapX + 2, hudMapY + 2, hudMapY + 74, hudMapY + 10, 0xFFFFFFFF);
            context.fill(map.getRenderAreaX(), map.getRenderAreaY(), map.getRenderAreaX2(), map.getRenderAreaY2(), 0, TileManager.themeColor);
            context.fill(map.getRenderAreaX(), map.getRenderAreaY(), map.getRenderAreaX2(), map.getRenderAreaY2(), 0, 0x10000000);
            context.drawText(
                    MinecraftClient.getInstance().textRenderer, text,
                    map.getRenderAreaX() + (map.getRenderAreaWidth() / 2) - (MinecraftClient.getInstance().textRenderer.getWidth(text) / 2),
                    map.getRenderAreaY() + (map.getRenderAreaHeight() / 2) - (MinecraftClient.getInstance().textRenderer.fontHeight / 2),
                    0xFFcccccc, true);
            if (MinecraftClient.getInstance().currentScreen instanceof MapConfigScreen) {
                drawCompassBackground(context);
            }
            return;
        }

        map.setArtificialZoom(TileManager.doArtificialZoom);
        map.renderMap(context, null, true);

        //0xD9D9D9
        if (!Double.isNaN(direction)) { //skip drawing the compass if direction is NaN (it can be separate of long-lat due to the two-point sampling system)
            drawCompass(context);
        }

    }

    public static void deinitialize(ClientLoginNetworkHandler clientLoginNetworkHandler, MinecraftClient minecraftClient) {
        initialized = false;
        System.out.println("deinitialize hudmap");

    }
}

