package net.mmly.openminemap.event;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.UnitConvert;

public class CommandHander {

    public static void register() { //this chaining is f***ing horrible
        ArgumentTypeRegistry.registerArgumentType(Identifier.of("openminemap", "coordinateargument"), CoordinateArgumentType.class, ConstantArgumentSerializer.of(CoordinateArgumentType::coordinateArgumentType));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("omm")
                    .then(ClientCommandManager.literal("tpllwtp")
                            .then(ClientCommandManager.argument("latitude", CoordinateArgumentType.coordinateArgumentType())
                            .then(ClientCommandManager.argument("longitude", CoordinateArgumentType.coordinateArgumentType())
                            .executes(CommandHander::tpllwtp)
                            .then(ClientCommandManager.argument("altitude", StringArgumentType.string())
                            .executes(CommandHander::tpllwtp)))))
                    .then(ClientCommandManager.literal("tpwtpll")
                            .then(ClientCommandManager.argument("x", CoordinateArgumentType.coordinateArgumentType())
                            .then(ClientCommandManager.argument("y", CoordinateArgumentType.coordinateArgumentType())
                            .then(ClientCommandManager.argument("z", CoordinateArgumentType.coordinateArgumentType())
                            .executes(CommandHander::tpwtpll)))))
        );});
        //registerCommands();
    }

    private static int tpllwtp(CommandContext<FabricClientCommandSource> context) {
        String altitude;
        try { altitude = context.getArgument("altitude", String.class); }
        catch (IllegalArgumentException e) { altitude = null; }
        String lat = context.getArgument("latitude", CoordinateValue.class).value;
        String lon = context.getArgument("longitude", CoordinateValue.class).value;

        double[] convertedCoords = UnitConvert.toDecimalDegrees(lat, lon);
        if (convertedCoords == null) {
            context.getSource().sendFeedback(Text.literal("An error occurred. You likely entered coordinates with invalid formatting.").formatted(Formatting.RED).formatted(Formatting.ITALIC));
            return 0;
        }
        /*
        context.getSource().sendFeedback(Text.literal("Called /omm sub1. Args:"));
        context.getSource().sendFeedback(Text.literal(String.valueOf(lat)));
        context.getSource().sendFeedback(Text.literal(String.valueOf(lon)));
        if (Double.isNaN(altitude)) context.getSource().sendFeedback(Text.literal("No altitude argument"));
        else context.getSource().sendFeedback(Text.literal(String.valueOf(context.getArgument("altitude", Double.class))));
         */

        try {
            double[] coordsToTp = Projection.from_geo(convertedCoords[0], convertedCoords[1]);
            if (altitude == null) altitude = Double.toString(PlayersManager.getHighestPoint(coordsToTp[0], coordsToTp[1]));
            MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tp "+String.format("%.7f", coordsToTp[0])+" "+altitude+" "+String.format("%.7f", coordsToTp[1]));
            return 1;
        } catch (CoordinateValueError e) {
            context.getSource().sendFeedback(Text.literal("An error occurred. You many have entered coordinates that are invalid or out of bounds.").formatted(Formatting.RED).formatted(Formatting.ITALIC));
            return 0;
        }


    }

    private static int tpwtpll(CommandContext<FabricClientCommandSource> context) {
        String[] xyzStrings = new String[] {
            context.getArgument("x", CoordinateValue.class).value,
            context.getArgument("y", CoordinateValue.class).value,
            context.getArgument("z", CoordinateValue.class).value
        };

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        double[] xyz = new double[3];
        double[] xyzPlayer = new double[] {player.getX(), player.getY(), player.getZ()};

        try {
            for (int i = 0; i < 3; i++) {
                if (xyzStrings[i].startsWith("~")) {
                    if (xyzStrings[i].length() == 1) xyz[i] = xyzPlayer[i];
                    xyz[i] = xyzPlayer[i] + Double.parseDouble(xyzStrings[i].substring(1));
                } else {
                    xyz[i] = Double.parseDouble(xyzStrings[i]);
                }
            }
        } catch (NumberFormatException error) {
            context.getSource().sendFeedback(Text.literal("An error occurred. You likely entered coordinates with invalid formatting.").formatted(Formatting.RED).formatted(Formatting.ITALIC));
            return 0;
        }

        try {
            double[] coordsToTp = Projection.to_geo(xyz[0], xyz[2]);
            if (Double.isNaN(xyz[0])) {
                context.getSource().sendFeedback(Text.literal("An error occurred. You likely entered coordinates that are out of bounds.").formatted(Formatting.RED).formatted(Formatting.ITALIC));
                return 0;
            }
            player.networkHandler.sendChatCommand("tpll "+String.format("%.7f", coordsToTp[0])+" "+String.format("%.7f", coordsToTp[1])+" "+xyz[1]);
            return 1;
        } catch (CoordinateValueError e) {
            context.getSource().sendFeedback(Text.literal("An error occurred. You many have entered coordinates that are invalid or out of bounds.").formatted(Formatting.RED).formatted(Formatting.ITALIC));
            return 0;
        }


    }

}
