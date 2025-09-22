package net.mmly.openminemap.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.util.UnitConvert;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_OPENMINEMAP = "OpenMineMap"; //"key.category.osmMap.osmMapCategory";
    public static final String KEY_FULLSCREEN_OSM_MAP = "Open Fullscreen Map"; //"key.osmMap.fullscreenOsmMap";
    public static final String KEY_ZOOMIN_HUD_OSM_MAP = "Zoom In (HUD)";
    public static final String KEY_ZOOMOUT_HUD_OSM_MAP = "Zoom Out (HUD)";
    public static final String KEY_TOGGLE_HUD_OSM_MAP = "Toggle Map (HUD)";
    public static final String KEY_COPY_COORDINATES = "Copy Coordinates to Clipboard";
    public static final String KEY_SNAP_ANGLE = "Snap to Angle";

    //objects for all custom keybindings
    private static KeyBinding openFullscreenOsmMapKey;
    private static KeyBinding hudMapZoomInKey;
    private static KeyBinding hudMapZoomOutKey;
    private static KeyBinding hudMapToggleKey;
    private static KeyBinding copyCoordinatesKey;
    private static KeyBinding snapAngleKey;
    private static int stopIt = 0;

    //event handling for when the keys are pressed
    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(openFullscreenOsmMapKey.wasPressed()) {
                //what to do when key is pressed
                //client.player.sendMessage(Text.literal("Key pressed!")); //send message in chat

                MinecraftClient.getInstance().setScreen(
                        new FullscreenMapScreen()
                );

            }

            if(hudMapZoomInKey.wasPressed()) {
                HudMap.zoomIn();
            }

            if(hudMapZoomOutKey.wasPressed()) {
                HudMap.zoomOut();
            }

            if(hudMapToggleKey.wasPressed()) {
                HudMap.toggleRendering();
            }

            if(copyCoordinatesKey.wasPressed()) {
                copyPlayerCoordinates();
            }

            if(snapAngleKey.wasPressed()) {
                snapToAngle();
            }

        });
    }

    public static void register() { //function for registering the new keybinds; called in TutorialModClient
        openFullscreenOsmMapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_FULLSCREEN_OSM_MAP, //translation key of the keybinding's name
                InputUtil.Type.KEYSYM, //the type of the keybinding, KEYSYM for keyboard, MOUSE for mouse
                GLFW.GLFW_KEY_N, //the keycode of the key
                KEY_CATEGORY_OPENMINEMAP //the translation key of the keybinding's category
        ));

        hudMapZoomInKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ZOOMIN_HUD_OSM_MAP,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_EQUAL,
                KEY_CATEGORY_OPENMINEMAP
        ));

        hudMapZoomOutKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ZOOMOUT_HUD_OSM_MAP,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_MINUS,
                KEY_CATEGORY_OPENMINEMAP
        ));

        hudMapToggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_TOGGLE_HUD_OSM_MAP,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                KEY_CATEGORY_OPENMINEMAP
        ));

        copyCoordinatesKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_COPY_COORDINATES,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY_OPENMINEMAP
        ));

        snapAngleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_SNAP_ANGLE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KEY_CATEGORY_OPENMINEMAP
        ));

        registerKeyInputs(); //call the registerKeyInputs method defined above when the register method is called in TutorialModClient
    }

    public static void snapToAngle() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        //int cardinalDirection = minecraftClient.player.getFacing().getHorizontal();//0 is south, 1 is west, 2 is north, 3 is east
        double offsetFromMC = HudMap.direction;
        double facing = (Math.round(PlayerAttributes.yaw) - offsetFromMC - 1);
        facing += (360 * (facing < 0 ? 1 : 0));
        double snapAngle = -HudMap.snapAngle; //range: [0, 90]
        System.out.println(snapAngle);
        double faceInDirection;
        while (Math.abs(facing - snapAngle) >=45 && snapAngle <= 360) {
            snapAngle += 90;
        }

        System.out.println(snapAngle);
        minecraftClient.player.setYaw((float) (offsetFromMC + snapAngle));
        minecraftClient.player.sendMessage(Text.literal("Snap!")
                .formatted(Formatting.GRAY)
                .formatted(Formatting.ITALIC));
    }

    private static void copyPlayerCoordinates() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        PlayerAttributes.updatePlayerAttributes(minecraftClient);
        try {
            //Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection("test"), null);
            if (Double.isNaN(PlayerAttributes.latitude)) {
                stopIt++;
                if (stopIt >= 10) {
                    minecraftClient.player.sendMessage(Text.literal("stop it.")
                            .formatted(Formatting.RED)
                            .formatted(Formatting.ITALIC)
                            .formatted(Formatting.BOLD));
                    stopIt = 0;
                } else {
                    minecraftClient.player.sendMessage(Text.literal("Seems like you're outside the bounds of the projection. Please re-enter into reality and try again.")
                            .formatted(Formatting.GRAY)
                            .formatted(Formatting.ITALIC));
                }

            } else {
                MinecraftClient.getInstance().keyboard.setClipboard(UnitConvert.floorToPlace(PlayerAttributes.latitude, 7) + " " + UnitConvert.floorToPlace(PlayerAttributes.longitude, 7));
                minecraftClient.player.sendMessage(Text.literal("Coordinates copied to clipboard")
                        .formatted(Formatting.GRAY)
                        .formatted(Formatting.ITALIC));
            }
        } catch (Exception e) {
            try {
                minecraftClient.player.sendMessage(Text.literal("There was an error while doing that. Most likely a skill issue though.")
                        .formatted(Formatting.RED)
                        //.formatted(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/say e"))));
                        .formatted(Formatting.ITALIC));
            } catch (Exception e2) {
                OpenMineMap.somethingF__kedUpReallyBadIfThisMethodIsBeingCalled();
            }
        }
    }

}
