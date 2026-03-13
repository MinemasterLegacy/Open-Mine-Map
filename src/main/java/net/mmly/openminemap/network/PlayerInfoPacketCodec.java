package net.mmly.openminemap.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.map.PlayersManager;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static double CONVERSION_FACTOR = 182.0444;
    // 0-360 > 0-65535

    public static double decodeDirection(byte[] encodedYaw) {
        return ByteBuffer.wrap(encodedYaw).getShort() / CONVERSION_FACTOR;
    }

    @Override
    public PlayerData decode(ByteBuf buf) {

        int packetVersion = buf.readByte();
        if (OpenMineMapClient.MAX_PACKET_VERSION < packetVersion) {
            buf.readBytes(buf.readableBytes());
            return null;
        }

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Float> latitude = new ArrayList<>();
        ArrayList<Float> longitude = new ArrayList<>();
        ArrayList<Short> encodedYaw = new ArrayList<>();

        while (buf.readableBytes() > 0) {
            StringBuilder name = new StringBuilder();
            char readingChar = 0x00;
            while (readingChar != '$') {
                readingChar = buf.readChar();
                name.append(readingChar);
            }

            names.add(name.toString());
            latitude.add(buf.readFloat());
            longitude.add(buf.readFloat());
            encodedYaw.add(buf.readShort());

        }

        PlayersManager.lastReceivedData = new PlayerData(packetVersion, names.stream().toList(), latitude, longitude, encodedYaw);
        return PlayersManager.lastReceivedData;
    }

    @Override
    public void encode(ByteBuf buf, PlayerData value) {
        //not needed client-side
    }
}