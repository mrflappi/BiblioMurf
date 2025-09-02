package net.murfgames.bibliomurf;

import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import net.murfgames.bibliomurf.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiblioMurf implements ModInitializer {
	public static final String MOD_ID = "bibliomurf";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("BiblioMurf");

    private static final Map<Identifier, BiblioModule> loadedModules = new HashMap<>();

	@Override
	public void onInitialize() {
        ServerHandshake.register();
	}

    public static void registerModule(BiblioModule module) {
        loadedModules.put(module.getID(), module);
    }

    public static List<Identifier> getModuleIDs() {
        return loadedModules.keySet().stream().toList();
    }
}