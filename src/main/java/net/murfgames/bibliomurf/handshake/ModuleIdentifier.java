package net.murfgames.bibliomurf.handshake;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ModuleIdentifier(Identifier identifier, String version) {
    public static final StreamCodec<RegistryFriendlyByteBuf, ModuleIdentifier> CODEC =
            StreamCodec.ofMember(
                    // encoder
                    (module, buf) -> {
                        buf.writeIdentifier(module.identifier());
                        buf.writeUtf(module.version());
                    },
                    // decoder
                    buf -> new ModuleIdentifier(buf.readIdentifier(), buf.readUtf())
            );

    @Override
    public @NotNull String toString() {
        return "ModuleIdentifier{" +
                "identifier=" + identifier +
                ", version='" + version + '\'' +
                '}';
    }
}
