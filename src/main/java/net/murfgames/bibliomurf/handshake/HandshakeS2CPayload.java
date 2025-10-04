package net.murfgames.bibliomurf.handshake;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.murfgames.bibliomurf.BiblioMurf;

import java.util.ArrayList;
import java.util.List;

public record HandshakeS2CPayload(List<ModuleIdentifier> modules) implements CustomPayload {
    public static final Id<HandshakeS2CPayload> ID = new Id<>(Identifier.of(BiblioMurf.MOD_ID, "handshake_s2c"));

    public static final PacketCodec<RegistryByteBuf, HandshakeS2CPayload> CODEC =
            PacketCodec.of(
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
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
