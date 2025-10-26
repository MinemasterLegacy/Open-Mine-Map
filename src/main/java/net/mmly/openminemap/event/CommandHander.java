package net.mmly.openminemap.event;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.world.Heightmap;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.UnitConvert;

public class CommandHander {

    /*
    //seems unused and unnecessary
    public static void registerCommands() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

        });
    }

     */

    public static void register() { //this chaining is f***ing horrible
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("omm")
                    .then(ClientCommandManager.literal("tpllwtp")
                            .then(ClientCommandManager.argument("latitude", StringArgumentType.string())
                            .then(ClientCommandManager.argument("longitude", StringArgumentType.string())
                            .executes(CommandHander::tpllwtp)
                            .then(ClientCommandManager.argument("altitude", StringArgumentType.string())
                            .executes(CommandHander::tpllwtp)))))
                    .then(ClientCommandManager.literal("tpwtpll")
                            .then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
                            .then(ClientCommandManager.argument("y", DoubleArgumentType.doubleArg())
                            .then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
                            .executes(CommandHander::tpwtpll)))))
        );});
        //registerCommands();
    }

    private static int tpllwtp(CommandContext<FabricClientCommandSource> context) {
        String altitude;
        try { altitude = context.getArgument("altitude", String.class); }
        catch (IllegalArgumentException e) { altitude = null; }
        //TODO without an altitude argument, find the highest block of the column if area is loaded, otherwise use current
        String lat = context.getArgument("latitude", String.class);
        String lon = context.getArgument("longitude", String.class);

        double[] convertedCoords = UnitConvert.toDecimalDegrees(lat, lon);
        if (convertedCoords == null) {
            context.getSource().sendFeedback(Text.literal("An error occurred. You likely entered coordinates with invalid formatting."));
            return 0;
        }
        /*
        context.getSource().sendFeedback(Text.literal("Called /omm sub1. Args:"));
        context.getSource().sendFeedback(Text.literal(String.valueOf(lat)));
        context.getSource().sendFeedback(Text.literal(String.valueOf(lon)));
        if (Double.isNaN(altitude)) context.getSource().sendFeedback(Text.literal("No altitude argument"));
        else context.getSource().sendFeedback(Text.literal(String.valueOf(context.getArgument("altitude", Double.class))));
         */

        String yTp = "~";
        try {
            double[] coordsToTp = Projection.from_geo(convertedCoords[0], convertedCoords[1]);
            if (altitude == null) altitude = Double.toString(PlayersManager.getHighestPoint(coordsToTp[0], coordsToTp[1]));
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tp "+String.format("%.7f", coordsToTp[0])+" "+altitude+" "+String.format("%.7f", coordsToTp[1]));
            return 1;
        } catch (CoordinateValueError e) {
            context.getSource().sendFeedback(Text.literal("An error occurred. You many have entered coordinates that are invalid or out of bounds."));
            return 0;
        }


    }

    private static int tpwtpll(CommandContext<FabricClientCommandSource> context) {
        double x = context.getArgument("x", Double.class);
        double y = context.getArgument("y", Double.class);
        double z = context.getArgument("z", Double.class);
        try {
            double[] coordsToTp = Projection.to_geo(x, z);
            if (Double.isNaN(x)) {
                context.getSource().sendFeedback(Text.literal("An error occurred. You likely entered coordinates that are out of bounds."));
                return 0;
            }
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll "+String.format("%.7f", coordsToTp[0])+" "+String.format("%.7f", coordsToTp[1])+" "+y);
            return 1;
        } catch (CoordinateValueError e) {
            context.getSource().sendFeedback(Text.literal("An error occurred. You many have entered coordinates that are invalid or out of bounds."));
            return 0;
        }


    }

}
