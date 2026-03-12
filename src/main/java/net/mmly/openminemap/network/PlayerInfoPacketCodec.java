package net.mmly.openminemap.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.mmly.openminemap.OpenMineMapClient;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayerInfoPacketCodec implements PacketCodec<ByteBuf, PlayerData> {

    public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("P").forGetter(PlayerData::getPacketVersion),
            Codec.STRING.listOf().fieldOf("N").forGetter(PlayerData::getNames),
            Codec.FLOAT.listOf().fieldOf("A").forGetter(PlayerData::getLatitudes),
            Codec.FLOAT.listOf().fieldOf("O").forGetter(PlayerData::getLongitudes),
            Codec.SHORT.listOf().fieldOf("Y").forGetter(PlayerData::getYaws)
    ).apply(instance, PlayerData::new));

    //TODO move to packet handler class
    public static NetworkState currentNetworkState = NetworkState.NOT_CONNECTED;

    public static FarPlayer[] decode(byte[] data) {
        ArrayList<FarPlayer> players = new ArrayList<>();

        StringBuilder name = new StringBuilder();
        float latitude;
        float longitude;
        byte[] encodedYaw;

        byte packetVersion = data[0];
        if (packetVersion > OpenMineMapClient.MAX_PACKET_VERSION) {
            //Throw an error or something idk yet
        }
        System.out.println(packetVersion);

        int i = 1;

        while (data[i] != 0) {
            name.append(Character.toString(data[i]));
            i++;
        }

        i++;
        //System.out.println(Arrays.toString(ByteBuffer.wrap(Arrays.copyOfRange(data, i, i+4)).array()));
        latitude = ByteBuffer.wrap(Arrays.copyOfRange(data, i, i+4)).getFloat();
        i += 4;
        //System.out.println(Arrays.toString(ByteBuffer.wrap(Arrays.copyOfRange(data, i, i+4)).array()));
        longitude = ByteBuffer.wrap(Arrays.copyOfRange(data, i, i+4)).getFloat();
        i += 4;
        encodedYaw = new byte[] {data[i], data[i+1]};

        players.add(new FarPlayer(name.toString(), latitude, longitude, encodedYaw));

        return players.toArray(new FarPlayer[0]);
    }

    private static double CONVERSION_FACTOR = 182.0444;
    // 0-360 > 0-65535

    private static byte[] encodeDiretion(float mcYaw) {
        short encodedYaw = (short) Math.round((mcYaw % 360) * CONVERSION_FACTOR);
        return ByteBuffer.allocate(2).putShort(encodedYaw).array();
    }

    public static double decodeDirection(byte[] encodedYaw) {
        return ByteBuffer.wrap(encodedYaw).getShort() / CONVERSION_FACTOR;
    }

    public static byte[] encodePlayer(FarPlayer player) {
        // Will use farplayer here for now, but should be switched to mc player, with value conversions for xy and mcyaw
        byte[] name = player.name.getBytes(StandardCharsets.UTF_8);
        return ByteBuffer.allocate(name.length + 11) // 11 = 0byte{1} + lat{4} + lon{4} + yaw{2}
                .put(name)
                .put((byte) 0x00)
                .putFloat((float) player.latitude)
                .putFloat((float) player.longitude)
                .put(encodeDiretion((float) player.mcYaw))
                .array();
    }

    @Override
    public PlayerData decode(ByteBuf buf) {
        System.out.println(buf.toString(Charset.defaultCharset()));
        return null;
    }

    @Override
    public void encode(ByteBuf buf, PlayerData value) {

    }
}

class FarPlayer {
    public final String name;
    public final double latitude;
    public final double longitude;
    public final double mcYaw;

    FarPlayer(String name, float latitude, float longitude, byte[] encodedYaw) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.mcYaw = PlayerInfoPacketCodec.decodeDirection(encodedYaw);
    }
}