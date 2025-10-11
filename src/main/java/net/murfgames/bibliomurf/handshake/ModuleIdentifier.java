package net.murfgames.bibliomurf.handshake;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record ModuleIdentifier(Identifier identifier, String version) {
    public static final PacketCodec<RegistryByteBuf, ModuleIdentifier> CODEC =
            PacketCodec.of(
                    // encoder
                    (module, buf) -> {
                        buf.writeIdentifier(module.identifier());
                        buf.writeString(module.version());
                    },
                    // decoder
                    buf -> new ModuleIdentifier(buf.readIdentifier(), buf.readString())
            );

    @Override
    public @NotNull String toString() {
        return "ModuleIdentifier{" +
                "identifier=" + identifier +
                ", version='" + version + '\'' +
                '}';
    }
}
