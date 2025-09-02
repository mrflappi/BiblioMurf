package net.murfgames.bibliomurf.handshake;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.murfgames.bibliomurf.BiblioMurf;

import java.util.ArrayList;
import java.util.List;

public record HandshakeC2SPayload(List<Identifier> modules) implements CustomPayload {
    public static final CustomPayload.Id<HandshakeC2SPayload> ID = new Id<>(Identifier.of(BiblioMurf.MOD_ID, "handshake_c2s"));

    public static final PacketCodec<RegistryByteBuf, HandshakeC2SPayload> CODEC =
            PacketCodec.of(
                    // encoder
                    (payload, buf) -> {
                        buf.writeVarInt(payload.modules().size());
                        for (Identifier id : payload.modules()) {
                            buf.writeIdentifier(id);
                        }
                    },
                    // decoder
                    buf -> {
                        int size = buf.readVarInt();
                        List<Identifier> modules = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            modules.add(buf.readIdentifier());
                        }
                        return new HandshakeC2SPayload(modules);
                    }
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
