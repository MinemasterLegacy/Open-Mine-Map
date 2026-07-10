package net.mmly.openminemap.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.RightClickMenu;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

/*@Mixin(ClientPlayNetworkHandler.class)
public class TeleportMixin {

    @Inject(at = @At("HEAD"), method = "sendChatCommand")
    public void init(String command, CallbackInfo ci) {

    }*/


@Mixin(ClientPlayNetworkHandler.class)
public class TeleportMixin {

    @Inject(method = "sendChatCommand", at = @At(value = "INVOKE", target = "Lnet/minecraft/command/argument/SignedArgumentList;of(Lcom/mojang/brigadier/ParseResults;)Lnet/minecraft/command/argument/SignedArgumentList;", shift = At.Shift.BEFORE))
    private static void injected(String command, CallbackInfo ci, @Local(argsOnly = true) LocalRef<String> localRef) {
        if (!ConfigOptions.TELEPORT_INTERCEPT.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF)) return;
        if (!RightClickMenu.useTpll()) return;
        //MinecraftClient.getInstance().player.sendMessage(Text.of("Command: " + command), false);
        if (command.replaceFirst("execute in minecraft:overworld run tp", "").length() != command.length()) {
            //MinecraftClient.getInstance().player.sendMessage(Text.of("Yay!"), false);
            String[] arguments = command.replaceFirst("execute in minecraft:overworld run tp", "").split(" ");
            //MinecraftClient.getInstance().player.sendMessage(Text.of(Arrays.toString(arguments)), false);
            if (arguments.length != 5 && arguments.length != 7) return;
            double[] latLon;
            try {
                latLon = Projection.to_geo(Double.parseDouble(arguments[2]), Double.parseDouble(arguments[4]));
                //MinecraftClient.getInstance().player.sendMessage(Text.of("Converted to " + latLon[0] + ", "  + latLon[1]), false);

                if (arguments.length == 5) command =
                        "tpll " +
                                latLon[0] + " " +
                                latLon[1] + " " +
                                arguments[3];
                if (arguments.length == 7) command =
                        "tpll " +
                                latLon[0] + " " +
                                latLon[1] + " " +
                                arguments[3] + " " +
                                arguments[5] + " " +
                                arguments[6];
            } catch (CoordinateValueError | NumberFormatException ignored) {}

        }
        localRef.set(command);
    }
}