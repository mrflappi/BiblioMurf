package net.murfgames.bibliomurf.handshake;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import net.murfgames.bibliomurf.BiblioMurf;

import java.util.ArrayList;
import java.util.List;

public class ClientHandshake {
    private static boolean registered = false;

    private static List<Identifier> serverModules = new ArrayList<>();

    public static void register() {
        if (registered)
            return;

        HandshakeRegister.registerPayloadTypes();

        // Send hello on join
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            serverModules.clear();
            client.execute(() -> {
                ClientPlayNetworking.send(new HandshakeC2SPayload(BiblioMurf.getModuleIDs()));
            });
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            serverModules.clear();
        });

        // Listen for server reply
        ClientPlayNetworking.registerGlobalReceiver(HandshakeS2CPayload.ID, (payload, context) -> {
            serverModules = payload.modules();
            BiblioMurf.LOGGER.info("Server has BiblioMurf installed! {} modules found", serverModules.size());
            for (Identifier moduleID: serverModules)
                BiblioMurf.LOGGER.info("- Found module: {}", moduleID.toString());
        });

        registered = true;
    }

    public static boolean serverHasModule(Identifier module) {
        return serverModules.contains(module);
    }
}
