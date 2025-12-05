package net.mmly.openminemap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.mmly.openminemap.event.CommandHander;
import net.mmly.openminemap.event.KeyInputHandler;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.Requester;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.TileUrlFile;

import java.util.ArrayList;

public class OpenMineMapClient implements ClientModInitializer { // client class

    public static ArrayList<String> debugMessages = new ArrayList<>();
    public static OmmMap newMapRenderer = new OmmMap(50, 50, 200, 140);

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

/*
        newMapRenderer.setBackground(0x44000000);
        newMapRenderer.zoomIn();
        newMapRenderer.setArtificialZoom(true);
        HudRenderCallback.EVENT.register(newMapRenderer::render);

 */

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