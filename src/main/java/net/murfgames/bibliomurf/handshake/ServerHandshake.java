package net.murfgames.bibliomurf.handshake;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.murfgames.bibliomurf.BiblioMurf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerHandshake {
    private static final Map<ServerPlayerEntity, List<ModuleIdentifier>> MODDED_PLAYERS = new HashMap<>();
    private static boolean registered = false;

    public static void register() {
        if (registered)
            return;

        HandshakeRegister.registerPayloadTypes();

        ServerPlayNetworking.registerGlobalReceiver(HandshakeC2SPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                MODDED_PLAYERS.put(context.player(), payload.modules());
                BiblioMurf.LOGGER.info("{} has BiblioMurf installed! {} modules found", context.player().getName().getString(), payload.modules().size());
                for (ModuleIdentifier moduleID: payload.modules())
                    BiblioMurf.LOGGER.info("- Found module: {}, version: {}", moduleID.identifier().toString(), moduleID.version());

                ServerPlayNetworking.send(context.player(), new HandshakeS2CPayload(BiblioMurf.getModuleIDs()));
            });
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            MODDED_PLAYERS.remove(handler.player);
        });

        registered = true;
    }

    public static boolean playerHasModule(ModuleIdentifier module, ServerPlayerEntity player) {
        if (!MODDED_PLAYERS.containsKey(player))
            return false;

        List<ModuleIdentifier> modules = MODDED_PLAYERS.get(player);
        return modules.contains(module);
    }

    public static boolean playerHasModule(Identifier module, ServerPlayerEntity player) {
        if (!MODDED_PLAYERS.containsKey(player))
            return false;

        List<ModuleIdentifier> modules = MODDED_PLAYERS.get(player);
        return modules.stream().anyMatch(moduleIdentifier -> moduleIdentifier.identifier() == module);
    }
}
