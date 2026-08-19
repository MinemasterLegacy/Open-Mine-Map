package net.mmly.openminemap.hud;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.config.MapConfigScreen;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.event.KeyInputHandler;
import net.mmly.openminemap.http.MapType;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.projection.Direction;
import net.mmly.openminemap.util.ColorUtil;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.TileUrlFile;
import net.mmly.openminemap.util.WaypointFile;

import java.util.Locale;

public class HudMap {

    public static final int MIN_SIZE = 20;
    public static boolean initialized = false;
    private static boolean rastersInitialized;
    public static boolean renderHud = ConfigOptions._HUD_TOGGLE.getAsBoolean(); //is toggled by the keybind
    public static boolean hudEnabled = ConfigOptions._HUD_ENABLED.getAsBoolean(); //is toggled by the fullscreen map button and is dominant over the keybind
    public static int hudCompassX = ConfigOptions.HUD_COMPASS_X.getAsInt();
    public static int hudCompassY = ConfigOptions.HUD_COMPASS_Y.getAsInt();
    public static int hudCompassWidth = ConfigOptions.HUD_COMPASS_WIDTH.getAsInt();
    protected static Identifier compassIdentifier = Identifier.fromNamespaceAndPath("openminemap", "stripcompass.png");
    protected static Identifier snapAngleIdentifier = Identifier.fromNamespaceAndPath("openminemap", "snapangle.png");
    protected static int hudCompassCenter;
    static double snapAngleInput;
    public static double snapAngle; //range: (-90, 0]
    public static boolean doSnapAngle = false;
    public static final OmmMap map = new OmmMap(
            ConfigOptions.HUD_MAP_X.getAsInt(),
            ConfigOptions.HUD_MAP_Y.getAsInt(),
            ConfigOptions.HUD_MAP_WIDTH.getAsInt(),
            ConfigOptions.HUD_MAP_HEIGHT.getAsInt()
    );

    public static Identifier playerIdentifier;
    public static boolean showBorder = true;
    public static boolean showCompass = true;

    public static void clampZoom() {
        //used to decrease zoom level (if needed) when artificial zoom is disabled
       map.clampZoom();
    }

    public static void loadConfigParameters() {
        setSnapAngle();
        showBorder = ConfigOptions.HUDMAP_BORDER.getAsBooleanFromValues(ConfigOptions.Values.SHOW_HIDE);
        showCompass = ConfigOptions.COMPASS.getAsBooleanFromValues(ConfigOptions.Values.SHOW_HIDE);
    }

    public static void setSnapAngle() {
        String receivedSnapAngle = ConfigFile.readOption(ConfigOptions.SNAP_ANGLE);
        if (receivedSnapAngle.isEmpty()) {
            doSnapAngle = false;
        } else {
            doSnapAngle = true;
            snapAngleInput = Double.parseDouble(receivedSnapAngle);
            snapAngle = ((-snapAngleInput) % 90) - (90 * (((-snapAngleInput) % 90) > 0 ? 1 : 0));
        }
    }

    public static void initialize(GuiGraphicsExtractor context) {
        //TileManager.initializeConfigParameters();
        setSnapAngle();
        loadConfigParameters();

        map.initFields();
        map.setFollowPlayer(true);
        map.setArtificialZoom(TileManager.doArtificialZoom);
        map.setMapZoom(
                ConfigOptions._HUD_LAST_ZOOM.getAsDouble()
        );
        map.setTextRenderer(Minecraft.getInstance().font);
        map.doPlayerTooltipNames(false);

        initialized = true;
        WaypointFile.setWaypointsOfThisWorld(true);

        showBorder = ConfigOptions.HUDMAP_BORDER.getAsBooleanFromValues(ConfigOptions.Values.SHOW_HIDE);

        if (!ConfigOptions._FIRST_SESSION_TIP_GIVEN.getAsBoolean()) {
            Minecraft.getInstance().player
                    .sendSystemMessage(Component
                            .translatable("omm.category.openminemap")
                            .append(": ")
                            .withStyle(ChatFormatting.DARK_GREEN).withStyle(ChatFormatting.BOLD)
                    .append(Component
                            .translatable("omm.hud.first-session-tooltip.start")
                            .append(KeyMappingHelper.getBoundKeyOf(KeyInputHandler.openFullscreenOsmMapKey).getDisplayName().getString().toUpperCase(Locale.US))
                            .append(Component.translatable("omm.hud.first-session-tooltip.end"))
                            .withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.BLUE)
                    )
            );
            ConfigFile.writeParameter(ConfigOptions._FIRST_SESSION_TIP_GIVEN, "true");
        }

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

    private static void drawCompass(GuiGraphicsExtractor context) {
        drawCompassBackground(context);
        Player player = Minecraft.getInstance().player;
        //draw the compass
        context.blit(RenderPipelines.GUI_TEXTURED, compassIdentifier, hudCompassX, hudCompassY, (float) (Direction.getGeoAzimuth(player) - ((double) hudCompassWidth / 2)), 0, hudCompassWidth, 16, 360, 16);
        //draw the snap angle indicator
        if (doSnapAngle) context.blit(RenderPipelines.GUI_TEXTURED, snapAngleIdentifier, hudCompassX, hudCompassY, (float) (Direction.getGeoAzimuth(player) - Direction.getGeoAzimuth(player.getX(), player.getZ(), -snapAngle) - ((double) hudCompassWidth / 2)) , 0, hudCompassWidth, 16, hudCompassWidth, 16, 90, 16);
        //context.drawTexture(compassIdentifier, hudCompassX, hudCompassY, hudCompassWidth, 16, 0, 0, hudCompassWidth, 16, 360, 16);
        //draw the compass direction needle line thing (i dont have a good name for it)
        context.fill(hudCompassX + hudCompassCenter, hudCompassY, hudCompassX + hudCompassCenter + 1, hudCompassY + 16, 0xFFaa9d94);

    }

    private static void drawCompassBackground(GuiGraphicsExtractor context) {
        for (int i = 2; i >= 0; i--) { //draw the semi-transparent compass background
            context.fill(hudCompassX + i, hudCompassY + i, hudCompassX + hudCompassWidth - i, hudCompassY + 16 - i, 0x33CCCCCC);
        }
    }

    public static void render(GuiGraphicsExtractor context, DeltaTracker renderTickCounter) {

        //method is called every frame, so a couple of things are included here that need to run every frame
        while (!OpenMineMapClient.debugMessages.isEmpty()) {
            if (OpenMineMapClient.debugMessages.getFirst() != null) Minecraft.getInstance().player.sendSystemMessage(Component.literal(OpenMineMapClient.debugMessages.getFirst()).withStyle(ChatFormatting.RED));
            OpenMineMapClient.debugMessages.removeFirst();
        }

        //OldRequestManager.setMapType(MinecraftClient.getInstance().currentScreen == null);
        if (!rastersInitialized) {
            TileUrlFile.loadRastersFromFile();
            rastersInitialized = true;
        }

        //now do actual hudmap stuff
        if (!initialized) initialize(context); //initialize hudmap if not done already
        if (TileManager.getThemeColor() == 0xFF808080) TileManager.loadTopTile();

        if ((!renderHud || !hudEnabled || Minecraft.getInstance().options.hideGui) && !(Minecraft.getInstance().screen instanceof MapConfigScreen)) return; //do not do anything if hud rendering is disabled

        UContext.setContext(context);
        playerIdentifier = Minecraft.getInstance().player.getSkin().body().texturePath();

        PlayersManager.updatePlayerSkinList();

        PlayerAttributes.updatePlayerAttributes(Minecraft.getInstance()); //refreshes values for geographic longitude, latitude and yaw
        hudCompassCenter = Math.round((float) hudCompassWidth / 2); //center of the hud compass

        if (!PlayerAttributes.positionIsValid()) {//if the player is out of bounds this will be NaN. all other rendering is skipped due to this
            //draw error message and exit
            Component text = Component.translatable("omm.hud.out-of-bounds").withStyle(ChatFormatting.ITALIC);
            //context.fill(hudMapX + 2, hudMapY + 2, hudMapY + 74, hudMapY + 10, 0xFFFFFFFF);
            context.fill(map.getRenderAreaX(), map.getRenderAreaY(), map.getRenderAreaX2(), map.getRenderAreaY2(), TileManager.getThemeColor());
            context.text(
                    Minecraft.getInstance().font, text,
                    map.getRenderAreaX() + (map.getRenderAreaWidth() / 2) - (Minecraft.getInstance().font.width(text) / 2),
                    map.getRenderAreaY() + (map.getRenderAreaHeight() / 2) - (Minecraft.getInstance().font.lineHeight / 2),
                    0xFFcccccc, true);
            if (Minecraft.getInstance().screen instanceof MapConfigScreen) {
                drawCompassBackground(context);
            }
            return;
        }

        map.setArtificialZoom(TileManager.doArtificialZoom);
        map.renderMap(context, MapType.HUD);

        //0xD9D9D9
        if (PlayerAttributes.positionIsValid() && showCompass) { //skip drawing the compass if direction is NaN (it can be separate of long-lat due to the two-point sampling system)
            drawCompass(context);
        }

        if (showBorder) {
            int blue = ColorUtil.darken(0xFF0447D8, 0.35);
            int green = ColorUtil.darken(0xFF0BD604, 0.35);
            int mid = ColorUtil.average(blue, green);

            int x = map.getRenderAreaX();
            int y = map.getRenderAreaY();
            int x2 = map.getRenderAreaX2();
            int y2 = map.getRenderAreaY2();

            context.fill(x + 1, y2 - 2, x2 - 1, y2, green); //bottom
            context.fill(x2 - 2, y + 1, x2, y2 - 1, green); //right
            context.fill(x + 1, y, x2 - 1, y + 2, blue); //top
            context.fill(x, y + 1, x + 2, y2 - 1, blue); //left

            UContext.fillZone(x + 2, y + 2, 1, 1, blue); //top-left
            UContext.fillZone(x2 - 3, y2 - 3, 1, 1, green); //bottom-right

            UContext.fillZone(x2 - 3, y + 2, 1, 1, mid); //top-right inner
            UContext.fillZone(x + 2, y2 - 3, 1, 1, mid); //bottom-left inner

            UContext.fillZone(x2 - 2, y + 1, 1, 1, mid); //top-right outer
            UContext.fillZone(x + 1, y2 - 2, 1, 1, mid); //bottom-left outer

        }

    }

}

