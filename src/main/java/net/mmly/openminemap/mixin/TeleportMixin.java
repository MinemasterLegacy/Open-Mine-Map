package net.mmly.openminemap.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
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


@Mixin(ClientPacketListener.class)
public class TeleportMixin {

    @Unique
    private final static List<String> prefixes = List.of(
            "execute in minecraft:overworld run tp",
            "execute in overworld run tp",
            "execute run tp",
            "tp",
            "minecraft:tp"
    );

    @ModifyVariable(method = "sendCommand", at = @At(value = "HEAD"), argsOnly = true, name = "string")
    private static String injected(String string) {
        if (!ConfigOptions.TELEPORT_INTERCEPT.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF)) return string;
        if (!RightClickMenu.useTpll()) return string;
        if (CommandHander.forceNoIntercept) return string;

        String prefix = null;
        for (String s : prefixes) {
            if (string.startsWith(s)) {
                prefix = s;
                break;
            }
        }
        if (prefix == null) return string;

        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(string.replaceFirst(prefix, "").split(" ")));
        arguments.removeFirst(); //remove always blank first element
        if (arguments.size() < 3) return string;

        //remove possible target selector
        if (arguments.size() > 3) {
            arguments.removeFirst();
        }

        double[] latLon;
        try {
            latLon = Projection.to_geo(Double.parseDouble(arguments.get(0)), Double.parseDouble(arguments.get(2)));
            string = "tpll " +
                latLon[0] + " " +
                latLon[1] + " " +
                arguments.get(1);
        } catch (CoordinateValueError | NumberFormatException ignored) {
            //do nothing, command will not be modified
        }

        return string;
    }
}