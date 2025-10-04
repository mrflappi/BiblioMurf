package net.murfgames.bibliomurf;

import net.minecraft.util.Identifier;

public interface BiblioModule {
    Identifier getID();
    String getVersion();

    default void registerModule() {
        BiblioMurf.registerModule(this);
    }
}
