package net.murfgames.bibliomurf;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Identifier;
import net.murfgames.bibliomurf.handshake.ModuleIdentifier;
import net.murfgames.bibliomurf.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BiblioMurf implements ModInitializer {
	public static final String MOD_ID = "bibliomurf";
    public static final String MOD_VERSION = getModVersion(MOD_ID);

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

    public static List<ModuleIdentifier> getModuleIDs() {
        return loadedModules.keySet().stream().map(identifier ->
                new ModuleIdentifier(identifier, loadedModules.get(identifier).getVersion())
        ).toList();
    }

    public static String getModVersion(String modId) {
        return FabricLoader.getInstance()
                .getModContainer(modId)
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getVersion)
                .map(Version::getFriendlyString)
                .orElse("unknown");
    }
}