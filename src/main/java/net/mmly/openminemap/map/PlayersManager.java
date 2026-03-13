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
import net.mmly.openminemap.network.PlayerData;

import java.util.*;

public class PlayersManager {

    public static HashMap<UUID, Identifier> playerSkinList;
    public static PlayerData lastReceivedData;

    //MinecraftClient.getInstance().world.getPlayers()
    public static List<MappablePlayer> getNearPlayers() {
        updatePlayerSkinList();
        ClientPlayerEntity selfPlayer = MinecraftClient.getInstance().player;

        List<PlayerEntity> list = MinecraftClient.getInstance().world.getEntitiesByType(EntityType.PLAYER, new Box(
                selfPlayer.getBlockX() + 128,
                selfPlayer.getBlockY() + 128,
                selfPlayer.getBlockZ() + 128,
                selfPlayer.getBlockX() - 128,
                selfPlayer.getBlockY() - 128,
                selfPlayer.getBlockZ() - 128
        ), EntityPredicates.VALID_ENTITY);

        ArrayList<MappablePlayer> returnList = new ArrayList<>();
        for (PlayerEntity player : list.toArray(new PlayerEntity[0])) {
            returnList.add(new MappablePlayer(player));
        }
        return returnList.stream().toList();
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
