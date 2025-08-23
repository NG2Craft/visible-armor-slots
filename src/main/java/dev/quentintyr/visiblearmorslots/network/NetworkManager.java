package dev.quentintyr.visiblearmorslots.network;

import dev.quentintyr.visiblearmorslots.action.ActionType;
import dev.quentintyr.visiblearmorslots.action.SlotAction;
import dev.quentintyr.visiblearmorslots.action.handler.SlotActionHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;

/**
 * Manages network communication between client and server
 */
public class NetworkManager {
    public static final Identifier SLOT_ACTION_PACKET_ID = new Identifier("visiblearmorslots", "slot_action");

    public static void init() {
        // Register server-side packet handlers
        registerServerReceivers();
    }

    private static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(SLOT_ACTION_PACKET_ID,
                (server, player, handler, buf, responseSender) -> {
                    // Read packet data
                    ActionType actionType = buf.readEnumConstant(ActionType.class);
                    EquipmentSlot targetSlot = buf.readBoolean() ? buf.readEnumConstant(EquipmentSlot.class) : null;
                    int hotbarSlot = buf.readVarInt();
                    boolean isShiftPressed = buf.readBoolean();
                    boolean isCtrlPressed = buf.readBoolean();
                    boolean isCreativeMode = buf.readBoolean();

                    SlotAction slotAction = new SlotAction(actionType, targetSlot, hotbarSlot,
                            isShiftPressed, isCtrlPressed, isCreativeMode);

                    // Execute on server thread
                    server.execute(() -> {
                        SlotActionHandler.handleAction(slotAction, player);
                    });
                });
    }
}
