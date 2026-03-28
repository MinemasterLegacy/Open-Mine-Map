package net.mmly.openminemap.map;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;
import net.mmly.openminemap.enums.OverlayVisibility;
import net.mmly.openminemap.network.NetworkPlayerData;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayersManager {

    public static HashMap<UUID, Identifier> playerSkinList;
    public static NetworkPlayerData lastReceivedData = NetworkPlayerData.empty();

    //MinecraftClient.getInstance().world.getPlayers()

    /// Returns a list of current players that should be displayed, taking into account overlay visibility rules and excluding the client player.
    public static List<MappablePlayer> getMappablePlayers() {
        //System.out.println("Stored data length: " + lastReceivedData.getMappablePlayers().length);
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
        //System.out.println("near list: " + list.size());
        list.remove(selfPlayer);

        HashMap<UUID, MappablePlayer> returnList = new HashMap<>();
        //ArrayList<MappablePlayer> returnList = new ArrayList<>();

        for (PlayerEntity player : list.toArray(new PlayerEntity[0])) {
            MappablePlayer mappablePlayer = new MappablePlayer(player, OverlayVisibility.LOCAL);
            if (!mappablePlayer.outOfBounds) returnList.put(mappablePlayer.uuid, mappablePlayer);
        }

        for (MappablePlayer player : lastReceivedData.getMappablePlayers()) {
            if (
                    (!player.outOfBounds) &&
                    (!returnList.containsKey(player.uuid)) &&
                    !player.uuid.equals(selfPlayer.getUuid())
            ) {
                returnList.put(player.uuid, player);
            }

        }

        //System.out.println("return list length: " + returnList.size());
        return returnList.values().stream().toList();
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

    public static Text getDisplayNameOf(UUID uuid) {
        PlayerListEntry entry = MinecraftClient.getInstance().player.networkHandler.getPlayerListEntry(uuid);
        if (entry != null) {}
        for (PlayerListEntry listEntry : MinecraftClient.getInstance().player.networkHandler.getPlayerList()) {
            GameProfile profile = listEntry.getProfile();
            if (profile.getId().equals(uuid)) return Text.of(profile.getName());
        }
        return null;
    }
}
