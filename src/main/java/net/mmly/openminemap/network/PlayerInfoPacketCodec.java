package net.mmly.openminemap.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.map.PlayersManager;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerInfoPacketCodec implements PacketCodec<ByteBuf, NetworkPlayerData> {

    /*
    public static final Codec<NetworkPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("PacketVersions").forGetter(NetworkPlayerData::getPacketVersion),
            Codec.LONG.listOf().fieldOf("MostSigOfUuids").forGetter(NetworkPlayerData::getMostSignificant),
            Codec.LONG.listOf().fieldOf("LeastSigOfUuids").forGetter(NetworkPlayerData::getLeastSignificant),
            Codec.FLOAT.listOf().fieldOf("Latitudes").forGetter(NetworkPlayerData::getLatitudes),
            Codec.FLOAT.listOf().fieldOf("Longitudes").forGetter(NetworkPlayerData::getLongitudes),
            Codec.SHORT.listOf().fieldOf("EncodedYaws").forGetter(NetworkPlayerData::getEncodedYaws)
    ).apply(instance, NetworkPlayerData::new));
    */

    private static final int SINGLE_PLAYER_SECTION_LENGTH = 26;

    @Override
    public NetworkPlayerData decode(ByteBuf buf) {

        NetworkState.connectionEstablished();

        int packetVersion = buf.readByte();

        if (OpenMineMapClient.MAX_PACKET_VERSION < packetVersion || buf.readableBytes() % SINGLE_PLAYER_SECTION_LENGTH != 0) {
            NetworkState.connectionErrored();
            buf.readBytes(buf.readableBytes());
            return null;
        }

        ArrayList<UUID> uuids = new ArrayList<>();
        ArrayList<Float> latitude = new ArrayList<>();
        ArrayList<Float> longitude = new ArrayList<>();
        ArrayList<Short> encodedYaw = new ArrayList<>();

        while (buf.readableBytes() > 0) {
            long most = buf.readLong();
            long least = buf.readLong();

            uuids.add(new UUID(most, least));
            latitude.add(buf.readFloat());
            longitude.add(buf.readFloat());
            encodedYaw.add(buf.readShort());

        }

        PlayersManager.lastReceivedData = new NetworkPlayerData(packetVersion, uuids, latitude, longitude, encodedYaw);
        return PlayersManager.lastReceivedData;
    }

    @Override
    public void encode(ByteBuf buf, NetworkPlayerData value) {
        //not needed client-side
    }
}