package net.mmly.openminemap.network;

import com.google.common.collect.ImmutableList;
import net.mmly.openminemap.enums.OverlayVisibility;
import net.mmly.openminemap.map.MappablePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NetworkPlayerData {

    public int packetVersion;
    public List<UUID> uuids;
    public List<Float> latitudes;
    public List<Float> longitudes;
    public List<Short> encodedYaws;

    private static List<UUID> toUUIDs(List<Long> mostSignificant, List<Long> leastSignificant) {
        ArrayList<UUID> uuids = new ArrayList<>();
        while (!mostSignificant.isEmpty()) {
            uuids.add(new UUID(mostSignificant.getFirst(), leastSignificant.getFirst()));
        }
        return uuids.stream().toList();
    }

    public static NetworkPlayerData empty() {
        return new NetworkPlayerData(-1, List.of(), List.of(), List.of(), List.of());
    }

    public NetworkPlayerData(int packetVersion, List<Long> mostSignificant, List<Long> leastSignificant, List<Float> latitudes, List<Float> longitudes, List<Short> encodedYaws) {
        this(packetVersion, toUUIDs(mostSignificant, leastSignificant), latitudes, longitudes, encodedYaws);
    }

    public NetworkPlayerData(int packetVersion, List<UUID> uuids, List<Float> latitudes, List<Float> longitudes, List<Short> encodedYaws) {
        this.packetVersion = packetVersion;
        this.uuids = uuids;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        this.encodedYaws = encodedYaws;
    }

    //public PlayerEntity

    public int getPacketVersion() {
        return packetVersion;
    }

    public ImmutableList<UUID> getUUIDs() {
        return ImmutableList.<UUID>builder().build();
    }

    public ImmutableList<Float> getLatitudes() {
        return ImmutableList.<Float>builder().build();
    }

    public ImmutableList<Float> getLongitudes() {
        return ImmutableList.<Float>builder().build();
    }

    public ImmutableList<Short> getEncodedYaws() {
        return ImmutableList.<Short>builder().build();
    }

    public ImmutableList<Long> getLeastSignificant() {
        ArrayList<Long> longs = new ArrayList<>();
        uuids.forEach(uuid -> longs.add(uuid.getLeastSignificantBits()));
        return (ImmutableList<Long>) longs.stream().toList();
    }

    public ImmutableList<Long> getMostSignificant() {
        ArrayList<Long> longs = new ArrayList<>();
        uuids.forEach(uuid -> longs.add(uuid.getLeastSignificantBits()));
        return (ImmutableList<Long>) longs.stream().toList();
    }

    private static double CONVERSION_FACTOR = 182.0444;
    // 0-360 -> 0-65535
    public static double decodeDirection(short encodedYaw) {
        return encodedYaw / CONVERSION_FACTOR;
    }

    public MappablePlayer[] getMappablePlayers() {
        MappablePlayer[] players = new MappablePlayer[uuids.size()];
        for (int i = 0; i < uuids.size(); i++) {
            players[i] = new MappablePlayer(latitudes.get(i), longitudes.get(i), decodeDirection(encodedYaws.get(i)), uuids.get(i), OverlayVisibility.ALL);
        }
        return players;
    }

}
