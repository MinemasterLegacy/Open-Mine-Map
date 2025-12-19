package net.mmly.openminemap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.event.CommandHander;
import net.mmly.openminemap.event.KeyInputHandler;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.Requester;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.TileUrlFile;
import net.mmly.openminemap.util.WaypointFile;

import java.util.ArrayList;

public class OpenMineMapClient implements ClientModInitializer { // client class

    public static ArrayList<String> debugMessages = new ArrayList<>();
    public static OmmMap newMapRenderer = new OmmMap(50, 50, 200, 140);
    public static final String MODVERSION = "1.4.0";

    @Override
    public void onInitializeClient() { //method where other fabric api methods for registering and adding objects and behaviors will be called

        KeyInputHandler.register(); //register all new keybinds
        CommandHander.register(); //register commands

        TileManager.createOpenminemapDir();
        ConfigFile.establishConfigFile();
        //ScreenMouseEvents.EVENT.re

        Requester osmTileRequester = new Requester();
        osmTileRequester.start();

        HudRenderCallback.EVENT.register(HudMap::render);
        HudRenderCallback.EVENT.register(FullscreenMapScreen::render);

        if (Boolean.parseBoolean(ConfigFile.readParameter(ConfigOptions.__WAYPOINTS))) {
            ServerLifecycleEvents.SERVER_STARTED.register(WaypointFile::setWaypointsOfThisWorld);
            WaypointFile.load();
        }

        ServerLifecycleEvents.SERVER_STARTING.register(TileUrlFile::addApplicableErrors);
        ServerLifecycleEvents.SERVER_STOPPING.register(ConfigFile::writeOnClose);

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