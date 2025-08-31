package net.mmly.openminemap.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.hud.HudMap;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_OPENMINEMAP = "OpenMineMap"; //"key.category.osmMap.osmMapCategory";
    public static final String KEY_FULLSCREEN_OSM_MAP = "Open Fullscreen Map"; //"key.osmMap.fullscreenOsmMap";
    public static final String KEY_ZOOMIN_HUD_OSM_MAP = "Zoom In (HUD)";
    public static final String KEY_ZOOMOUT_HUD_OSM_MAP = "Zoom Out (HUD)";
    public static final String KEY_TOGGLE_HUD_OSM_MAP = "Toggle Map (HUD)";


    //objects for all custom keybindings
    private static KeyBinding openFullscreenOsmMapKey;
    private static KeyBinding hudMapZoomInKey;
    private static KeyBinding hudMapZoomOutKey;
    private static KeyBinding hudMapToggleKey;

    //event handling for when the keys are pressed
    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(openFullscreenOsmMapKey.wasPressed()) {
                //what to do when key is pressed
                //client.player.sendMessage(Text.literal("Key pressed!")); //send message in chat

                MinecraftClient.getInstance().setScreen(
                        new FullscreenMapScreen(Text.empty())
                );

            }

            if(hudMapZoomInKey.wasPressed()) {
                HudMap.zoomIn();
            }

            if(hudMapZoomOutKey.wasPressed()) {
                HudMap.zoomOut();
            }

            if(hudMapToggleKey.wasPressed()) {
                HudMap.toggle();
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

        registerKeyInputs(); //call the registerKeyInputs method defined above when the register method is called in TutorialModClient
    }

}
