package net.mmly.openminemap.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.event.CommandHander;
import net.mmly.openminemap.gui.RightClickMenu;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.Arrays;
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
            "execute in overworld run tp",
            "execute run tp",
            "tp",
            "minecraft:tp"
    );

    @ModifyVariable(method = "sendChatCommand", at = @At(value = "HEAD"), argsOnly = true, name = "command")
    private static String injected(String command) {
        if (!ConfigOptions.TELEPORT_INTERCEPT.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF)) return command;
        if (!RightClickMenu.useTpll()) return command;
        if (CommandHander.forceNoIntercept) return command;

        String prefix = null;
        for (String s : prefixes) {
            if (command.startsWith(s)) {
                prefix = s;
                break;
            }
        }
        if (prefix == null) return command;

        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(command.replaceFirst(prefix, "").split(" ")));
        arguments.removeFirst(); //remove always blank first element
        if (arguments.size() < 3) return command;

        //remove possible target selector
        if (arguments.size() > 3) {
            arguments.removeFirst();
        }

        double[] latLon;
        try {
            latLon = Projection.to_geo(Double.parseDouble(arguments.get(0)), Double.parseDouble(arguments.get(2)));
            command = "tpll " +
                latLon[0] + " " +
                latLon[1] + " " +
                arguments.get(1);
        } catch (CoordinateValueError | NumberFormatException ignored) {
            //do nothing, command will not be modified
        }

        return command;
    }
}