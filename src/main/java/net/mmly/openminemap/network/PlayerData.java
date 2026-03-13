package net.mmly.openminemap.network;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    public int packetVersion;
    public List<UUID> uuids;
    public List<Float> latitudes;
    public List<Float> longitudes;
    public List<Short> yaws;

    private static List<UUID> toUUIDs(List<Long> mostSignificant, List<Long> leastSignificant) {
        ArrayList<UUID> uuids = new ArrayList<>();
        while (!mostSignificant.isEmpty()) {
            uuids.add(new UUID(mostSignificant.getFirst(), leastSignificant.getFirst()));
        }
        return uuids.stream().toList();
    }

    public PlayerData(int packetVersion, List<Long> mostSignificant, List<Long> leastSignificant, List<Float> latitudes, List<Float> longitudes, List<Short> encodedYaws) {
        this(packetVersion, toUUIDs(mostSignificant, leastSignificant), latitudes, longitudes, encodedYaws);
    }

    public PlayerData(int packetVersion, List<UUID> uuids, List<Float> latitudes, List<Float> longitudes, List<Short> encodedYaws) {
        this.packetVersion = packetVersion;
        this.uuids = uuids;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        this.yaws = encodedYaws;
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

    public ImmutableList<Short> getYaws() {
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

}
