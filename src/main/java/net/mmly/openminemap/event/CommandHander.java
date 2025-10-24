package net.mmly.openminemap.event;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;

public class CommandHander {

    public static void registerCommands() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

        });
    }

    public static void register() { //this chaining is f***ing horrible
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("omm")
                    .then(ClientCommandManager.literal("tpllwtp")
                            .then(ClientCommandManager.argument("latitude", DoubleArgumentType.doubleArg())
                            .then(ClientCommandManager.argument("longitude", DoubleArgumentType.doubleArg())
                            .executes(CommandHander::tpllwtp)
                            .then(ClientCommandManager.argument("altitude", DoubleArgumentType.doubleArg())
                            .executes(CommandHander::tpllwtp)))))
                    .then(ClientCommandManager.literal("tpwtpll")
                            .then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
                            .then(ClientCommandManager.argument("y", DoubleArgumentType.doubleArg())
                            .then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
                            .executes(CommandHander::tpwtpll)))))
        );});

        registerCommands();
    }

    //TODO set style attributes for all command text

    private static int tpllwtp(CommandContext<FabricClientCommandSource> context) {
        double altitude;
        try { altitude = context.getArgument("altitude", Double.class); }
        catch (IllegalArgumentException e) { altitude = Double.NaN; }

        double lat = context.getArgument("latitude", Double.class);
        double lon = context.getArgument("longitude", Double.class);

        //variable value output is temporary; TODO remove
        context.getSource().sendFeedback(Text.literal("Called /omm sub1. Args:"));
        context.getSource().sendFeedback(Text.literal(String.valueOf(lat)));
        context.getSource().sendFeedback(Text.literal(String.valueOf(lon)));
        if (Double.isNaN(altitude)) context.getSource().sendFeedback(Text.literal("No altitude argument"));
        else context.getSource().sendFeedback(Text.literal(String.valueOf(context.getArgument("altitude", Double.class))));

        try {
            double[] coordsToTp = Projection.from_geo(lat, lon);
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tp "+String.format("%.7f", coordsToTp[0])+" ~ "+String.format("%.7f", coordsToTp[1])); //TODO disable /tp output or create own protocols
            return 1;
        } catch (CoordinateValueError e) {
            context.getSource().sendFeedback(Text.literal("An error occurred. You many have entered coordinates that are invalid or out of bounds."));
            return 0;
        }


    }

    private static int tpwtpll(CommandContext<FabricClientCommandSource> context) {
        double x = context.getArgument("x", Double.class);
        double y = context.getArgument("y", Double.class); //TODO make it so that ~ is recognised as a valid character (and maybe ^)
        double z = context.getArgument("z", Double.class);
        try {
            double[] coordsToTp = Projection.to_geo(x, z);
            if (Double.isNaN(x)) {
                context.getSource().sendFeedback(Text.literal("An error occurred. You likely entered coordinates that are out of bounds."));
                return 0;
            }
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll "+String.format("%.7f", coordsToTp[0])+" "+String.format("%.7f", coordsToTp[1])+" "+y); //TODO disable /tp output or create own protocols
            return 1;
        } catch (CoordinateValueError e) {
            context.getSource().sendFeedback(Text.literal("An error occurred. You many have entered coordinates that are invalid or out of bounds."));
            return 0;
        }


    }

}
