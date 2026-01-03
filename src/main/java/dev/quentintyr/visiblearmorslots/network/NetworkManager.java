package dev.quentintyr.visiblearmorslots.network;

import dev.quentintyr.visiblearmorslots.action.handler.SlotActionHandler;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Manages network communication between client and server using the typed
 * payload registry API.
 */
public class NetworkManager {

    public static void initialize() {
        // Register the payload type (server-side C2S)
        PayloadTypeRegistry.playC2S().register(SlotActionPayload.ID, SlotActionPayload.CODEC);

        // Register receiver using the typed payload handler
        ServerPlayNetworking.registerGlobalReceiver(SlotActionPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                SlotActionHandler.handleAction(payload, context.player());
            });
        });
    }
}
