package net.murfgames.bibliomurf;

import net.fabricmc.api.ClientModInitializer;
import net.murfgames.bibliomurf.handshake.ClientHandshake;

public class BiblioMurfClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
        ClientHandshake.register();

		BiblioMurf.LOGGER.info("BiblioMurf has been initialized");
	}
}