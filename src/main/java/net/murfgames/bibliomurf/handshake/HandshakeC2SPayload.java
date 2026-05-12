package net.murfgames.bibliomurf.handshake;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.murfgames.bibliomurf.BiblioMurf;

import java.util.ArrayList;
import java.util.List;

public record HandshakeC2SPayload(List<ModuleIdentifier> modules) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<HandshakeC2SPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(BiblioMurf.MOD_ID, "handshake_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, HandshakeC2SPayload> CODEC =
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
                        return new HandshakeC2SPayload(modules);
                    }
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
