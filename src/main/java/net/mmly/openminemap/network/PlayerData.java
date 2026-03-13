package net.mmly.openminemap.network;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

public class PlayerData {

    public int packetVersion;
    public List<String> names;
    public List<Float> latitudes;
    public List<Float> longitudes;
    public List<Short> yaws;

    public PlayerData(int packetVersion, List<String> names, List<Float> latitudes, List<Float> longitudes, List<Short> encodedYaws) {
        this.packetVersion = packetVersion;
        this.names = names;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        this.yaws = encodedYaws;

        System.out.println(names.size());
        try {
            System.out.println(names.getFirst());
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    //public PlayerEntity

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
