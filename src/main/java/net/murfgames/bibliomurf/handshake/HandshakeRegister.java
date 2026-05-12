package net.murfgames.bibliomurf.handshake;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class HandshakeRegister {
    private static boolean registered = false;

    public static void registerPayloadTypes() {
        if (registered) return;

        // Both sides must know both payloads
        PayloadTypeRegistry.serverboundPlay().register(HandshakeC2SPayload.ID, HandshakeC2SPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(HandshakeS2CPayload.ID, HandshakeS2CPayload.CODEC);

        registered = true;
    }
}
