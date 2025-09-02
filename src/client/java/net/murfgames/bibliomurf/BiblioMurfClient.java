package net.murfgames.bibliomurf;

import net.fabricmc.api.ClientModInitializer;
import net.murfgames.bibliomurf.customassets.CustomFontLoader;
import net.murfgames.bibliomurf.handshake.ClientHandshake;

public class BiblioMurfClient implements ClientModInitializer {

	public static final CustomFontLoader CUSTOM_FONT_LOADER = new CustomFontLoader();

	@Override
	public void onInitializeClient() {
		CUSTOM_FONT_LOADER.onInitialise();

        ClientHandshake.register();

		BiblioMurf.LOGGER.info("BiblioMurf has been initialized");
	}
}