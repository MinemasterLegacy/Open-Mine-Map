package net.mmly.openminemap.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.raster.ViewSetRastersScreen;
import net.mmly.openminemap.util.UnitConvert;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final KeyMapping.Category KEY_CATEGORY_OPENMINEMAP = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("openminemap", "keycategory")); //"key.category.osmMap.osmMapCategory";
    public static final String KEY_FULLSCREEN_OSM_MAP = "omm.key.open-fullscreen-map"; //"key.osmMap.fullscreenOsmMap";
    public static final String KEY_ZOOMIN_HUD_OSM_MAP = "omm.key.zoom-in";
    public static final String KEY_ZOOMOUT_HUD_OSM_MAP = "omm.key.zoom-out";
    public static final String KEY_TOGGLE_HUD_OSM_MAP = "omm.key.toggle-map";
    public static final String KEY_COPY_COORDINATES = "omm.key.copy-coordinates";
    public static final String KEY_SNAP_ANGLE = "omm.key.snap-angle";
    public static final String KEY_RASTER_SCREEN = "omm.key.open-raster-screen";

    //objects for all custom keybindings
    public static KeyMapping openFullscreenOsmMapKey;
    public static KeyMapping hudMapZoomInKey;
    public static KeyMapping hudMapZoomOutKey;
    public static KeyMapping hudMapToggleKey;
    public static KeyMapping copyCoordinatesKey;
    public static KeyMapping snapAngleKey;
    public static KeyMapping rasterScreenKey;
    private static int stopIt = 0;

    public static KeyMapping getOpenFullscreenOsmMapKey() {
        return openFullscreenOsmMapKey;
    }

    //event handling for when the keys are pressed
    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(openFullscreenOsmMapKey.consumeClick()) {
                //what to do when key is pressed
                //client.player.sendMessage(Text.literal("Key pressed!")); //send message in chat
                Minecraft.getInstance().setScreen(
                        new MapScreen()
                );

            }

            if(hudMapZoomInKey.consumeClick()) {
                HudMap.zoomIn();
            }

            if(hudMapZoomOutKey.consumeClick()) {
                HudMap.zoomOut();
            }

            if(hudMapToggleKey.consumeClick()) {
                HudMap.toggleRendering();
            }

            if(copyCoordinatesKey.consumeClick()) {
                copyPlayerCoordinates();
            }

            if(snapAngleKey.consumeClick()) {
                if (HudMap.doSnapAngle) snapToAngle();
            }

            if (rasterScreenKey.consumeClick()) {
                Minecraft.getInstance().setScreen(new ViewSetRastersScreen(true));
            }

        });
    }

    public static void register() { //function for registering the new keybinds; called in TutorialModClient
        openFullscreenOsmMapKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                KEY_FULLSCREEN_OSM_MAP, //translation key of the keybinding's name
                InputConstants.Type.KEYSYM, //the type of the keybinding, KEYSYM for keyboard, MOUSE for mouse
                GLFW.GLFW_KEY_N, //the keycode of the key
                KEY_CATEGORY_OPENMINEMAP //the translation key of the keybinding's category
        ));

        hudMapZoomInKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                KEY_ZOOMIN_HUD_OSM_MAP,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_EQUAL,
                KEY_CATEGORY_OPENMINEMAP
        ));

        hudMapZoomOutKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                KEY_ZOOMOUT_HUD_OSM_MAP,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS,
                KEY_CATEGORY_OPENMINEMAP
        ));

        hudMapToggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                KEY_TOGGLE_HUD_OSM_MAP,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                KEY_CATEGORY_OPENMINEMAP
        ));

        copyCoordinatesKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                KEY_COPY_COORDINATES,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY_OPENMINEMAP
        ));

        snapAngleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                KEY_SNAP_ANGLE,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY_OPENMINEMAP
        ));

        rasterScreenKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                KEY_RASTER_SCREEN,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY_OPENMINEMAP
        ));

        registerKeyInputs(); //call the registerKeyInputs method defined above when the register method is called in TutorialModClient
    }

    public static void snapToAngle() {
        snapToAngle(HudMap.snapAngle);
    }

    public static void snapToAngle(double angle) {
        Minecraft minecraftClient = Minecraft.getInstance();
        PlayerAttributes.updatePlayerAttributes(minecraftClient);
        //int cardinalDirection = minecraftClient.player.getFacing().getHorizontal();//0 is south, 1 is west, 2 is north, 3 is east
        double facing = (Math.round(PlayerAttributes.yaw));
        facing += (360 * (facing < 0 ? 1 : 0));
        double snapAngle = -angle; //range: [0, 90]
        //System.out.println(snapAngle);
        while (Math.abs(facing - snapAngle) >=45 && snapAngle <= 360) {
            snapAngle += 90;
        }

        //System.out.println(snapAngle);
        if (minecraftClient.player == null) {
            minecraftClient.player.displayClientMessage(Component.translatable("omm.key.execute.error.snap-angle")
                    .withStyle(ChatFormatting.RED)
                    .withStyle(ChatFormatting.ITALIC), false);
        } else {
            minecraftClient.player.setYRot((float) snapAngle);
            minecraftClient.player.displayClientMessage(Component.translatable("omm.key.execute.snap-angle")
                    .withStyle(ChatFormatting.GRAY)
                    .withStyle(ChatFormatting.ITALIC), false);
        }

    }

    private static void copyPlayerCoordinates() {
        Minecraft minecraftClient = Minecraft.getInstance();
        PlayerAttributes.updatePlayerAttributes(minecraftClient);
        try {
            //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("test"), null);
            if (Double.isNaN(PlayerAttributes.latitude)) {
                stopIt++;
                if (stopIt >= 10) {
                    minecraftClient.player.displayClientMessage(Component.literal("stop it.")
                            .withStyle(ChatFormatting.RED)
                            .withStyle(ChatFormatting.ITALIC)
                            .withStyle(ChatFormatting.BOLD), false);
                    stopIt = 0;
                } else {
                    minecraftClient.player.displayClientMessage(Component.translatable("omm.key.execute.error.out-of-bounds")
                            .withStyle(ChatFormatting.GRAY)
                            .withStyle(ChatFormatting.ITALIC), false);
                }

            } else {
                Minecraft.getInstance().keyboardHandler.setClipboard(UnitConvert.floorToPlace(PlayerAttributes.latitude, 7) + " " + UnitConvert.floorToPlace(PlayerAttributes.longitude, 7));
                minecraftClient.player.displayClientMessage(Component.translatable("omm.key.execute.copy-coordinates")
                        .withStyle(ChatFormatting.GRAY)
                        .withStyle(ChatFormatting.ITALIC), false);
            }
        } catch (Exception e) {
                minecraftClient.player.displayClientMessage(Component.translatable("omm.key.execute.error.copy-coordinates")
                        .withStyle(ChatFormatting.RED)
                        //.formatted(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/say e"))));
                        .withStyle(ChatFormatting.ITALIC), false);
        }
    }

}
