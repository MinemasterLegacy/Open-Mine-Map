package net.mmly.openminemap.event;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.map.DrawableClaim;
import net.mmly.openminemap.map.MappablePlayer;
import net.mmly.openminemap.map.PlayerAttributes;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.UnitConvert;
import net.mmly.openminemap.util.Waypoint;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CommandHander {

    public static final ChatFormatting FEEDBACK_COLOR = ChatFormatting.BLUE;
    public static final ChatFormatting ERROR_COLOR = ChatFormatting.RED;
    public static boolean forceNoIntercept = false;

    public static void register() { //this chaining is f***ing horrible
        ArgumentTypeRegistry.registerArgumentType(Identifier.fromNamespaceAndPath("openminemap", "coordinateargument"), CoordinateArgumentType.class, SingletonArgumentInfo.contextFree(CoordinateArgumentType::coordinateArgumentType));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal("omm")

                    .then(ClientCommands.literal("tpllwtp")
                            .then(ClientCommands.argument("latitude longitude [altitude]", CoordinateArgumentType.coordinateArgumentType())
                            .executes(CommandHander::tpllwtp)))

                    .then(ClientCommands.literal("tpwtpll")
                            .then(ClientCommands.argument("x y z", CoordinateArgumentType.coordinateArgumentType())
                            .executes(CommandHander::tpwtpll)))

                    .then(ClientCommands.literal("tpllto")
                            .then(ClientCommands.argument("player name", CoordinateArgumentType.coordinateArgumentType())
                            .suggests(new TplltoSuggestionProvider())
                            .executes(CommandHander::tpllto)))

                    .then(ClientCommands.literal("warp")
                            .then(ClientCommands.argument("warp", CoordinateArgumentType.coordinateArgumentType())
                            .suggests(new WarpSuggestionProvider())
                            .executes(CommandHander::warp)))

                    .then(ClientCommands.literal("distortion")
                            .executes(CommandHander::distortionAtPlayer)
                                .then(ClientCommands.argument("[latitude longitude]", CoordinateArgumentType.coordinateArgumentType())
                                .executes(CommandHander::distortionAtLocation)))

                    .then(ClientCommands.literal("reloadclaims")
                            .executes(CommandHander::reloadclaims))
        );});
        //registerCommands();
    }

    private static int reloadclaims(CommandContext<FabricClientCommandSource> context) {
        if (!ConfigOptions.CLAIMS_RENDERING.getAsBooleanFromValues(ConfigOptions.Values.ON_OFF)) {
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable("omm.claims.not-enabled").withStyle(ERROR_COLOR));
            return 0;
        }
        DrawableClaim.reloadClaimData(false, true, true);
        return 0;
    }

    private static int distortionTeleport(double lat, double lon) {
        try {
            double[] distortion = Projection.getDistortion(lon, lat);
            String distString = UnitConvert.floorToPlace(Math.sqrt(Math.abs(distortion[0])), 10);
            String errString = UnitConvert.floorToPlace(Math.toDegrees(distortion[1]), 10);

            MutableComponent distText = Component.literal(distString).withStyle((style) -> style
                    .withColor(FEEDBACK_COLOR)
                    .applyFormat(ChatFormatting.ITALIC)
                    .withClickEvent(new ClickEvent.CopyToClipboard(distString))
                    .withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.copy.click")))
            );
            MutableComponent errText = Component.literal(errString).withStyle((style) -> style
                    .withColor(FEEDBACK_COLOR)
                    .applyFormat(ChatFormatting.ITALIC)
                    .withClickEvent(new ClickEvent.CopyToClipboard(errString))
                    .withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.copy.click")))
            );

            Minecraft.getInstance().player.sendSystemMessage(
                Component.translatable("omm.text.distortion")
                        .append(" \n")
                        .append(distText)
                        .append(" ± ")
                        .append(errText)
                        .append("°")
                .withStyle(ChatFormatting.ITALIC).withStyle(FEEDBACK_COLOR));
        } catch (CoordinateValueError e) {
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable("omm.error.distortion").withStyle(ERROR_COLOR).withStyle(ChatFormatting.ITALIC));
        }

        return 0;
    }

    private static int distortionAtPlayer(CommandContext<FabricClientCommandSource> context) {
        PlayerAttributes.updatePlayerAttributes(Minecraft.getInstance());
        return distortionTeleport(PlayerAttributes.getLatitude(), PlayerAttributes.getLongitude());
    }

    private static int distortionAtLocation(CommandContext<FabricClientCommandSource> context) {
        double latitide;
        double longitude;

        String[] coords = context.getArgument("[latitude longitude]", CoordinateValue.class).value.split(" ");
        if (coords.length < 2) {
            context.getSource().sendFeedback(Component.translatable("omm.error.incomplete-coordinates").withStyle(ERROR_COLOR).withStyle(ChatFormatting.ITALIC));
            return 0;
        }

        try {
            latitide = Double.parseDouble(coords[0]);
            longitude = Double.parseDouble(coords[1]);
        } catch (NumberFormatException e) {
            context.getSource().sendFeedback(Component.translatable("omm.error.formatted-coordinates").withStyle(ERROR_COLOR).withStyle(ChatFormatting.ITALIC));
            return 0;
        }

        return distortionTeleport(latitide, longitude);
    }

    /*
    private static int distortion(CommandContext<FabricClientCommandSource> context) {
        PlayerAttributes.updatePlayerAttributes(MinecraftClient.getInstance());
        try {
            double latitide;
            double longitude;
            if (!context.getArgument("[latitude longitude]", CoordinateValue.class).value.isBlank()) {

                String[] coords = context.getArgument("[latitude longitude]", CoordinateValue.class).value.split(" ");
                if (coords.length < 2) {
                    context.getSource().sendFeedback(Text.translatable("omm.error.incomplete-coordinates").formatted(ERROR_COLOR).formatted(Formatting.ITALIC));
                    return 0;
                }

                try {
                    latitide = Double.parseDouble(coords[0]);
                    longitude = Double.parseDouble(coords[1]);
                } catch (NumberFormatException e) {
                    context.getSource().sendFeedback(Text.translatable("omm.error.formatted-coordinates").formatted(ERROR_COLOR).formatted(Formatting.ITALIC));
                    return 0;
                }

            } else {
                latitide = PlayerAttributes.getLatitude();
                longitude = PlayerAttributes.getLongitude();
            }
            double[] distortion = Projection.getDistortion(latitide, longitude);
            MinecraftClient.getInstance().player.sendMessage(Text.literal(
                    Text.translatable("omm.text.distortion").getString() + " \n" +
                        UnitConvert.floorToPlace(Math.sqrt(Math.abs(distortion[0])), 10) +
                        " ± " +
                        UnitConvert.floorToPlace(Math.toDegrees(distortion[1]), 10) +
                        "°"
            ).formatted(Formatting.ITALIC).formatted(FEEDBACK_COLOR), false);
        } catch (CoordinateValueError e) {
            MinecraftClient.getInstance().player.sendMessage(Text.translatable("omm.error.distortion").formatted(ERROR_COLOR).formatted(Formatting.ITALIC), false);
        }

        return 0;
    }
     */

    private static int warp(CommandContext<FabricClientCommandSource> context) {

        String warp = context.getArgument("warp", CoordinateValue.class).value;

        for (Waypoint waypoint : OmmMap.getWaypoints()) {
            if (waypoint.name.equals(warp)) {
                Minecraft.getInstance().player.connection.sendCommand("tpll "+waypoint.latitude+" "+waypoint.longitude);
                return 1;
            }
        }
        Minecraft.getInstance().player.sendSystemMessage(Component.translatable("omm.key.execute.error.snap-angle").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
        return 0;
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
            context.getSource().sendFeedback(Component.translatable("omm.error.incomplete-coordinates").withStyle(ERROR_COLOR).withStyle(ChatFormatting.ITALIC));
            return 0;
        }
        String lat = coords[0];
        String lon = coords[1];
        String altitude;
        if (coords.length < 3) altitude = null;
        else altitude = coords[2];

        double[] convertedCoords = UnitConvert.toDecimalDegrees(lat, lon);
        if (convertedCoords == null) {
            context.getSource().sendFeedback(Component.translatable("omm.error.formatted-coordinates").withStyle(ERROR_COLOR).withStyle(ChatFormatting.ITALIC));
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
            forceNoIntercept = true;
            Minecraft.getInstance().player.connection.sendCommand("tp "+String.format("%.7f", coordsToTp[0])+" "+altitude+" "+String.format("%.7f", coordsToTp[1]));
            forceNoIntercept = false;
            return 1;
        } catch (CoordinateValueError e) {
            context.getSource().sendFeedback(Component.translatable("omm.error.invalid-or-out-of-bounds").withStyle(ChatFormatting.RED).withStyle(ChatFormatting.ITALIC));
            return 0;
        }


    }

    private static int tpwtpll(CommandContext<FabricClientCommandSource> context) {
        String[] xyzStrings = context.getArgument("x y z", CoordinateValue.class).value.split(" ");

        LocalPlayer player = Minecraft.getInstance().player;
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
            context.getSource().sendFeedback(Component.translatable("omm.error.formatted-coordinates").withStyle(ERROR_COLOR).withStyle(ChatFormatting.ITALIC));
            return 0;
        }

        try {
            double[] coordsToTp = Projection.to_geo(xyz[0], xyz[2]);
            if (Double.isNaN(coordsToTp[0])) {
                context.getSource().sendFeedback(Component.translatable("omm.error.out-of-bounds").withStyle(ERROR_COLOR).withStyle(ChatFormatting.ITALIC));
                return 0;
            }
            player.connection.sendCommand("tpll "+String.format("%.7f", coordsToTp[0])+" "+String.format("%.7f", coordsToTp[1])+" "+xyz[1]);
            return 1;
        } catch (CoordinateValueError e) {
            context.getSource().sendFeedback(Component.translatable("omm.error.invalid-or-out-of-bounds").withStyle(ERROR_COLOR).withStyle(ChatFormatting.ITALIC));
            return 0;
        }
    }

    private static int tpllto(CommandContext<FabricClientCommandSource> context) {
        String desiredPlayer = context.getArgument("player name", CoordinateValue.class).value.trim();

        for (MappablePlayer knownPlayer : PlayersManager.getMappablePlayers()) {
            if (knownPlayer.outOfBounds) continue;
            try {
                if (Objects.equals(Objects.requireNonNull(knownPlayer.name).getString(), desiredPlayer)) {
                    double desiredY = knownPlayer.altitude;
                    Minecraft.getInstance().player.connection.sendCommand("tpll "+String.format("%.7f", knownPlayer.latitude)+" "+String.format("%.7f", knownPlayer.longitude)+" "+desiredY);
                    return 1;
                }
            } catch (NullPointerException e) {
                OpenMineMapClient.debugMessages.add(Component.translatable("omm.notification.something-wrong").getString());
                return 0;
            }
        }

        context.getSource().sendFeedback(Component.literal(
                Component.translatable("omm.error.cannot-find-player-start").getString()
                    +desiredPlayer+
                    Component.translatable("omm.error.cannot-find-player-end").getString()
        ).withStyle(ERROR_COLOR).withStyle(ChatFormatting.ITALIC));

        return 1;
    }

}

class TplltoSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
        for (MappablePlayer knownPlayer : PlayersManager.getMappablePlayers()) {
            if (knownPlayer.outOfBounds) continue;
            String name = knownPlayer.name.getString();
            if (name.equals(Minecraft.getInstance().player.getName().getString())) continue;
            builder.suggest(name);
        }
        return builder.buildFuture();
    }
}

class WarpSuggestionProvider implements SuggestionProvider<FabricClientCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<FabricClientCommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {

        String existing = context.getInput().substring(10);

        for (Waypoint waypoint : OmmMap.getWaypoints()) {
            if (!(waypoint.name.toLowerCase().startsWith(existing.toLowerCase()))) continue;
            builder.suggest(waypoint.name);
        }
        return builder.buildFuture();
    }
}
