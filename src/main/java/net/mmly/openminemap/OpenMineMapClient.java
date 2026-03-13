package net.mmly.openminemap;

import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.event.CommandHander;
import net.mmly.openminemap.event.KeyInputHandler;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.Requester;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.network.PlayerData;
import net.mmly.openminemap.network.PlayerDataS2CPayload;
import net.mmly.openminemap.network.PlayerInfoPacketCodec;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.TileUrlFile;
import net.mmly.openminemap.util.WaypointFile;

import java.util.ArrayList;

public class OpenMineMapClient implements ClientModInitializer { // client class

    public static ArrayList<String> debugMessages = new ArrayList<>();
    public static boolean SHOWDEVELOPEROPTIONS = false;
    public static final String MODVERSION = "1.6.3";
    public static final int MAX_PACKET_VERSION = 1;

    private static final Identifier HUD_MAP_LAYER = Identifier.of("openminemap", "hud-example-layer");
    private static final Identifier HUD_MAP_LAYER_FS = Identifier.of("openminemap", "hud-example-layer-fs");

    @Override
    public void onInitializeClient() { //method where other fabric api methods for registering and adding objects and behaviors will be called

        KeyInputHandler.register(); //register all new keybinds
        CommandHander.register(); //register commands

        TileManager.createOpenminemapDir();
        ConfigFile.establishConfigFile();
        //ScreenMouseEvents.EVENT.re

        Requester osmTileRequester = new Requester();
        osmTileRequester.start();
        //TileLoader tileLoader = new TileLoader();
        //tileLoader.start();

        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.MISC_OVERLAYS, HUD_MAP_LAYER, HudMap::render));
        HudLayerRegistrationCallback.EVENT.register(layeredDrawer -> layeredDrawer.attachLayerBefore(IdentifiedLayer.SLEEP, HUD_MAP_LAYER_FS, FullscreenMapScreen::render));

        //ClientLoginConnectionEvents.INIT.register(WaypointFile::setWaypointsOfThisWorld);
        WaypointFile.load();

        ClientLoginConnectionEvents.INIT.register(TileUrlFile::addApplicableErrors);
        ClientLoginConnectionEvents.DISCONNECT.register(ConfigFile::writeOnClose);
        ClientLoginConnectionEvents.INIT.register(HudMap::deinitialize);

        TileUrlFile.establishUrls();

        TileManager.initializeConfigParameters();

        OpenMineMapClient.SHOWDEVELOPEROPTIONS = Boolean.parseBoolean(ConfigFile.readParameter(ConfigOptions.__SHOW_DEVELOPER_OPTIONS));
        //Tpll.lonLatToMcCoords(-112.07151142039129, 33.45512716304792);
        //test t = new test();
        /*
        try {
            double[] ll = tpll.from_geo(40.651480098863274, -74.32489167373383);
            System.out.println(ll[0]);
            System.out.println(ll[1]);
        } catch (Exception e) {
            System.out.println("tpll failed");
        }

         */

        PayloadTypeRegistry.playS2C().register(PlayerDataS2CPayload.ID, PlayerDataS2CPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(PlayerDataS2CPayload.ID, ((playerDataS2CPayload, context) -> {

        }));

        //PlayerInfoPacketCodec.CODEC.encodeStart(JsonOps.IN);

    }
}

/*
This is bob

    /-----\
    |     |
    \-----/
       |
      /|\
     / | \
    /  |  \
       |
      / \
     /   \
    /     \

Say hi to bob!
 */