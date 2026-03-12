package net.mmly.openminemap.network;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;

import java.util.List;

public class PlayerData {

    public int packetVersion;
    public List<String> names;
    public List<Float> latitudes;
    public List<Float> longitudes;
    public List<Short> yaws;

    public PlayerData(int packetVersion, String[] names, Float[] latitudes, Float[] longitudes, Short[] encodedYaws) {
        this.packetVersion = packetVersion;
        this.names = List.of(names);
        this.latitudes = List.of(latitudes);
        this.longitudes = List.of(longitudes);
        this.yaws = List.of(encodedYaws);
    }

    public PlayerData(int packetVersion, List<String> names, List<Float> latitudes, List<Float> longitudes, List<Short> encodedYaws) {
        this.packetVersion = packetVersion;
        this.names = names;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        this.yaws = encodedYaws;
    }

    public int getPacketVersion() {
        return packetVersion;
    }

    public ImmutableList<String> getNames() {
        return ImmutableList.<String>builder().build();
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
}
