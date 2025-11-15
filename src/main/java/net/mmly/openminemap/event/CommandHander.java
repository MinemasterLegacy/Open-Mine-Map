package net.mmly.openminemap.event;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.UnitConvert;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CommandHander {

    public static void register() { //this chaining is f***ing horrible
        ArgumentTypeRegistry.registerArgumentType(Identifier.of("openminemap", "coordinateargument"), CoordinateArgumentType.class, ConstantArgumentSerializer.of(CoordinateArgumentType::coordinateArgumentType));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("omm")
                    .then(ClientCommandManager.literal("tpllwtp")
                            .then(ClientCommandManager.argument("latitude longitude [altitude]", CoordinateArgumentType.coordinateArgumentType())
                            .executes(CommandHander::tpllwtp)))
                    .then(ClientCommandManager.literal("tpwtpll")
                            .then(ClientCommandManager.argument("x y z", CoordinateArgumentType.coordinateArgumentType())
                            .executes(CommandHander::tpwtpll)))
                    .then(ClientCommandManager.literal("tpllto")
                            .then(ClientCommandManager.argument("player name", CoordinateArgumentType.coordinateArgumentType())
                            .suggests(new TplltoSuggestionProvider())
                            .executes(CommandHander::tpllto)))
        );});
        //registerCommands();
    }

    private static int tpllwtp(CommandContext<FabricClientCommandSource> context) {
        /*
        String altitude;
        try { altitude = context.getArgument("altitude", String.class); }
        catch (IllegalArgumentException e) { altitude = null; }
        String lat = context.getArgument("latitude", CoordinateValue.class).value;
        String lon = context.getArgument("longitude", CoordinateValue.class).value;
         */

        String[] coords = context.getArgument("latitude longitude [altitude]", CoordinateValue.class).value.split(" ");
        if (coords.length < 2) {
            context.getSource().sendFeedback(Text.literal("An error occurred. You likely entered incomplete coordinates.").formatted(Formatting.RED).formatted(Formatting.ITALIC));
            return 0;
        }
        String lat = coords[0];
        String lon = coords[1];
        String altitude;
        if (coords.length < 3) altitude = null;
        else altitude = coords[2];

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
        String[] xyzStrings = context.getArgument("x y z", CoordinateValue.class).value.split(" ");

        System.out.println(Arrays.toString(xyzStrings));

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        double[] xyz = new double[3];
        double[] xyzPlayer = new double[] {player.getX(), player.getY(), player.getZ()};

        try {
            for (int i = 0; i < 3; i++) {
                if (xyzStrings[i].startsWith("~")) {
                    if (xyzStrings[i].length() == 1) xyz[i] = xyzPlayer[i];
                    else xyz[i] = xyzPlayer[i] + Double.parseDouble(xyzStrings[i].substring(1));
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
            if (Double.isNaN(coordsToTp[0])) {
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

    private static int tpllto(CommandContext<FabricClientCommandSource> context) {
        String desiredPlayer = context.getArgument("player name", CoordinateValue.class).value.trim();

        for (PlayerEntity knownPlayer : PlayersManager.getNearPlayers()) {
            try {
                if (Objects.equals(Objects.requireNonNull(knownPlayer.getName()).getString(), desiredPlayer)) {
                    double desiredY = knownPlayer.getY();
                    double[] longLat = Projection.to_geo(knownPlayer.getX(), knownPlayer.getZ());
                    MinecraftClient.getInstance().player.networkHandler.sendChatCommand("tpll "+String.format("%.7f", longLat[0])+" "+String.format("%.7f", longLat[1])+" "+desiredY);
                    return 1;
                }
            } catch (NullPointerException e) {
                System.out.println("NullPointerException thrown for /tpllto");
                return 0;
            } catch (CoordinateValueError e) {
                context.getSource().sendFeedback(Text.of("Error parsing coordinates; The player you are trying to teleport to may be out of bounds of the projection."));
                return 0;
            }
        }

        context.getSource().sendFeedback(Text.literal("Could not find player \""+desiredPlayer+"\" within rendered area.").formatted(Formatting.RED).formatted(Formatting.ITALIC));

        return 1;
    }

}

class TplltoSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        for (PlayerEntity knownPlayer : PlayersManager.getNearPlayers()) {
            String name = knownPlayer.getName().getString();
            if (name.equals(MinecraftClient.getInstance().player.getName().getString())) continue;
            builder.suggest(name);
        }
        return builder.buildFuture();
    }
}
