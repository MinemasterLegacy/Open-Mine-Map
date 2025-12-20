package net.mmly.openminemap.map;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayersManager {

    public static HashMap<UUID, Identifier> playerSkinList;

    //MinecraftClient.getInstance().world.getPlayers()
    public static List<PlayerEntity> getNearPlayers() {
        updatePlayerSkinList();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        List<PlayerEntity> l = MinecraftClient.getInstance().world.getEntitiesByType(EntityType.PLAYER, new Box(
                player.getBlockX() + 128,
                player.getBlockY() + 128,
                player.getBlockZ() + 128,
                player.getBlockX() - 128,
                player.getBlockY() - 128,
                player.getBlockZ() - 128
        ), EntityPredicates.VALID_ENTITY);
        return l;
    }

    private static void updatePlayerSkinList() {
        Collection<PlayerListEntry> pCollection = MinecraftClient.getInstance().player.networkHandler.getPlayerList(); //im pretty sure this doesn't send network requests :pray:
        List<PlayerListEntry> pList = pCollection.stream().toList();
        HashMap<UUID, Identifier> map = new HashMap<>();
        for (int i = 0; i < pList.size(); i++) {
            map.put(pList.get(i).getProfile().getId(), pList.get(i).getSkinTextures().texture());
        }
        playerSkinList = map;
    }

    public static double getHighestPoint(double x, double z) { //returns the highest applicable point for use in tpll commands
        double altitude = MinecraftClient.getInstance().world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) Math.floor(x), (int) Math.floor(z)); //get the highest point from cm heightmap
        if (altitude == MinecraftClient.getInstance().world.getBottomY()) altitude = MinecraftClient.getInstance().player.getY(); //if the calculated altitude is the world bottom, then the area is likely unrendered, so use the player's current y-value instead
        return altitude;
    }
}
