package net.murfgames.bibliomurf;

import net.minecraft.util.Identifier;

public interface BiblioModule {
    default Identifier getID() {
        return Identifier.of("bibliomurf", "default");
    }

    default void registerModule() {
        BiblioMurf.registerModule(this);
    }
}
