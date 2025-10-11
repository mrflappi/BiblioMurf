package net.murfgames.bibliomurf.handshake;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import net.murfgames.bibliomurf.BiblioMurf;

import java.util.ArrayList;
import java.util.List;

public class ClientHandshake {
    private static boolean registered = false;

    private static List<ModuleIdentifier> serverModules = new ArrayList<>();

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
            for (ModuleIdentifier moduleID: serverModules)
                BiblioMurf.LOGGER.info("- Found module: {}", moduleID.toString());
        });

        registered = true;
    }

    public static boolean serverHasModule(ModuleIdentifier module) {
        return serverModules.contains(module);
    }

    public static boolean serverHasModule(Identifier module) {
        return serverModules.stream().anyMatch(moduleIdentifier -> moduleIdentifier.identifier().equals(module));
    }
}
