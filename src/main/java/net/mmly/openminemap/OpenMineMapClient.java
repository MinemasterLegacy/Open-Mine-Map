package net.mmly.openminemap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.mmly.openminemap.event.CommandHander;
import net.mmly.openminemap.event.KeyInputHandler;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.Requester;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.TileUrlFile;

import java.util.ArrayList;

public class OpenMineMapClient implements ClientModInitializer { // client class

    public static ArrayList<String> debugMessages = new ArrayList<>();


    private static final Identifier HUD_MAP_LAYER = Identifier.of("openminemap", "hud-example-layer");

    @Override
    public void onInitializeClient() { //method where other fabric api methods for registering and adding objects and behaviors will be called

        KeyInputHandler.register(); //register all new keybinds
        CommandHander.register(); //register commands

        TileManager.createOpenminemapDir();
        ConfigFile.establishConfigFile();
        //ScreenMouseEvents.EVENT.re

        Requester osmTileRequester = new Requester();
        osmTileRequester.start();

        HudElementRegistry.attachElementBefore(VanillaHudElements.MISC_OVERLAYS, HUD_MAP_LAYER, HudMap::render);

        TileUrlFile.establishUrls();

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