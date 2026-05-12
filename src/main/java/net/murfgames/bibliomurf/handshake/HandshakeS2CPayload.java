package net.murfgames.bibliomurf.handshake;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.murfgames.bibliomurf.BiblioMurf;

import java.util.ArrayList;
import java.util.List;

public record HandshakeS2CPayload(List<ModuleIdentifier> modules) implements CustomPacketPayload {
    public static final Type<HandshakeS2CPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(BiblioMurf.MOD_ID, "handshake_s2c"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HandshakeS2CPayload> CODEC =
            StreamCodec.ofMember(
                    // encoder
                    (payload, buf) -> {
                        buf.writeVarInt(payload.modules().size());
                        for (ModuleIdentifier module : payload.modules()) {
                            ModuleIdentifier.CODEC.encode(buf, module);
                        }
                    },
                    // decoder
                    buf -> {
                        int size = buf.readVarInt();
                        List<ModuleIdentifier> modules = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            modules.add(ModuleIdentifier.CODEC.decode(buf));
                        }
                        return new HandshakeS2CPayload(modules);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
