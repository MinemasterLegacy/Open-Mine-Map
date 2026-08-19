package net.mmly.openminemap.map;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
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
        LocalPlayer selfPlayer = Minecraft.getInstance().player;

        List<Player> list = Minecraft.getInstance().level.getEntities(EntityType.PLAYER, new AABB(
                selfPlayer.getBlockX() + 128,
                selfPlayer.getBlockY() + 128,
                selfPlayer.getBlockZ() + 128,
                selfPlayer.getBlockX() - 128,
                selfPlayer.getBlockY() - 128,
                selfPlayer.getBlockZ() - 128
        ), EntitySelector.ENTITY_STILL_ALIVE);
        //System.out.println("near list: " + list.size());
        list.remove(selfPlayer);

        HashMap<UUID, MappablePlayer> returnList = new HashMap<>();
        //ArrayList<MappablePlayer> returnList = new ArrayList<>();

        for (Player player : list.toArray(new Player[0])) {
            MappablePlayer mappablePlayer = new MappablePlayer(player, OverlayVisibility.LOCAL);
            if (!mappablePlayer.outOfBounds) returnList.put(mappablePlayer.uuid, mappablePlayer);
        }

        for (MappablePlayer player : lastReceivedData.getMappablePlayers()) {
            if (
                    (!player.outOfBounds) &&
                    (!returnList.containsKey(player.uuid)) &&
                    !player.uuid.equals(selfPlayer.getUUID())
            ) {
                returnList.put(player.uuid, player);
            }

        }

        //System.out.println("return list length: " + returnList.size());
        return returnList.values().stream().toList();
    }

    public static void updatePlayerSkinList() {
        Collection<PlayerInfo> pCollection = Minecraft.getInstance().player.connection.getOnlinePlayers(); //im pretty sure this doesn't send network requests :pray:
        List<PlayerInfo> pList = pCollection.stream().toList();
        HashMap<UUID, Identifier> map = new HashMap<>();
        for (int i = 0; i < pList.size(); i++) {
            map.put(pList.get(i).getProfile().id(), pList.get(i).getSkin().body().texturePath());
        }
        playerSkinList = map;
    }

    public static double getHighestPoint(double x, double z) { //returns the highest applicable point for use in tpll commands
        double altitude = Minecraft.getInstance().level.getHeight(Heightmap.Types.WORLD_SURFACE, (int) Math.floor(x), (int) Math.floor(z)); //get the highest point from cm heightmap
        if (altitude == Minecraft.getInstance().level.getMinY()) altitude = Minecraft.getInstance().player.getY(); //if the calculated altitude is the world bottom, then the area is likely unrendered, so use the player's current y-value instead
        return altitude;
    }

    public static Component getDisplayNameOf(UUID uuid) {
        PlayerInfo entry = Minecraft.getInstance().player.connection.getPlayerInfo(uuid);
        if (entry != null) {}
        for (PlayerInfo listEntry : Minecraft.getInstance().player.connection.getOnlinePlayers()) {
            GameProfile profile = listEntry.getProfile();
            if (profile.id().equals(uuid)) return Component.nullToEmpty(profile.name());
        }
        return null;
    }
}
