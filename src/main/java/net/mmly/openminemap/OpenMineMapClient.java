package net.mmly.openminemap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.event.CommandHander;
import net.mmly.openminemap.event.KeyInputHandler;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.Requester;
import net.mmly.openminemap.map.TileLoader;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.TileUrlFile;
import net.mmly.openminemap.util.WaypointFile;

import java.util.ArrayList;

public class OpenMineMapClient implements ClientModInitializer { // client class

    public static ArrayList<String> debugMessages = new ArrayList<>();
    public static final String MODVERSION = "1.6.0";

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

        HudElementRegistry.attachElementBefore(VanillaHudElements.MISC_OVERLAYS, HUD_MAP_LAYER, HudMap::render);
        HudElementRegistry.attachElementBefore(VanillaHudElements.MISC_OVERLAYS, HUD_MAP_LAYER_FS, FullscreenMapScreen::render);

        //ClientLoginConnectionEvents.INIT.register(WaypointFile::setWaypointsOfThisWorld);
        WaypointFile.load();

        ClientLoginConnectionEvents.INIT.register(TileUrlFile::addApplicableErrors);
        ClientLoginConnectionEvents.DISCONNECT.register(ConfigFile::writeOnClose);
        ClientLoginConnectionEvents.INIT.register(HudMap::deinitialize);


        TileUrlFile.establishUrls();

        TileManager.initializeConfigParameters();

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