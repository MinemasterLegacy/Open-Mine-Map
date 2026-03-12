package net.mmly.openminemap.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record PlayerDataS2CPayload(PlayerData playerData) implements CustomPayload {
    public static final Identifier PLAYER_DATA_PAYLOAD_ID = Identifier.of("openservermap", "channel");
    public static final CustomPayload.Id<PlayerDataS2CPayload> ID = new CustomPayload.Id<>(PLAYER_DATA_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, PlayerDataS2CPayload> CODEC = PacketCodec.tuple(
            null,
            PlayerDataS2CPayload::playerData,
            PlayerDataS2CPayload::new
    ); //TODO fillout nulls

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
