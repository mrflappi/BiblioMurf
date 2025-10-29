package net.murfgames.bibliomurf;

import net.fabricmc.api.ClientModInitializer;
import net.murfgames.bibliomurf.handshake.ClientHandshake;
import net.murfgames.bibliomurf.tags.ClientTagLoader;

public class BiblioMurfClient implements ClientModInitializer {

    public static final ClientTagLoader CLIENT_TAG_LOADER = new ClientTagLoader();

	@Override
	public void onInitializeClient() {
        ClientHandshake.register();

        CLIENT_TAG_LOADER.onInitialise();

		BiblioMurf.LOGGER.info("BiblioMurf has been initialized");
	}
}