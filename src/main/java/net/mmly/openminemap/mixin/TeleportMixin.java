package net.mmly.openminemap.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.RightClickMenu;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

/*@Mixin(ClientPlayNetworkHandler.class)
public class TeleportMixin {

    @Inject(at = @At("HEAD"), method = "sendChatCommand")
    public void init(String command, CallbackInfo ci) {

    }*/


@Mixin(ClientPlayNetworkHandler.class)
public class TeleportMixin {

    @Unique
    private final static List<String> prefixes = List.of(
            "execute in minecraft:overworld run tp",
            "tp",
            "minecraft:tp"
    );

    @ModifyVariable(method = "sendChatCommand", at = @At(value = "HEAD"), argsOnly = true)
    private static String injected(String command) {
        if (!ConfigOptions.TELEPORT_INTERCEPT.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF)) return command;
        if (!RightClickMenu.useTpll()) return command;
        //MinecraftClient.getInstance().player.sendMessage(Text.of("Command: " + command), false);

        String prefix = null;
        for (String s : prefixes) {
            if (command.startsWith(s)) {
                prefix = s;
                break;
            }
        }
        if (prefix == null) return command;

        //MinecraftClient.getInstance().player.sendMessage(Text.of("Yay!"), false);
        String[] arguments = command.replaceFirst(prefix, "").split(" ");
        //MinecraftClient.getInstance().player.sendMessage(Text.of(Arrays.toString(arguments)), false);
        if (arguments.length != 5 && arguments.length != 7) return command;
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

        return command;
    }
}