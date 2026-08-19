package net.mmly.openminemap.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PlayerDataS2CPayload(NetworkPlayerData networkPlayerData) implements CustomPacketPayload {
    public static final Identifier PLAYER_DATA_PAYLOAD_ID = Identifier.fromNamespaceAndPath("openservermap", "channel");
    public static final CustomPacketPayload.Type<PlayerDataS2CPayload> ID = new CustomPacketPayload.Type<>(PLAYER_DATA_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerDataS2CPayload> CODEC = StreamCodec.composite(
            new PlayerInfoPacketCodec(),
            PlayerDataS2CPayload::networkPlayerData,
            PlayerDataS2CPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
