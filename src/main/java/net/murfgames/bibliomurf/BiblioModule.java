package net.murfgames.bibliomurf;

import net.minecraft.util.Identifier;

public interface BiblioModule {
    Identifier getID();

    default void registerModule() {
        BiblioMurf.registerModule(this);
    }
}
