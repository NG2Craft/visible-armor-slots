package dev.quentintyr.visiblearmorslots.network;

import dev.quentintyr.visiblearmorslots.action.ActionType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Typed payload for slot actions used with the new payload registry API.
 */
public record SlotActionPayload(ActionType actionType,
        EquipmentSlot targetSlot,
        int hotbarSlot,
        boolean isShiftPressed,
        boolean isCtrlPressed,
        boolean isCreativeMode) implements CustomPayload {

    public static final CustomPayload.Id<SlotActionPayload> ID = new CustomPayload.Id<>(
            Identifier.of("visiblearmorslots", "slot_action"));

    // Encode/decode all fields so client and server agree on the payload shape.
    public static final PacketCodec<PacketByteBuf, SlotActionPayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeEnumConstant(payload.actionType());
                buf.writeBoolean(payload.targetSlot() != null);
                if (payload.targetSlot() != null) {
                    buf.writeEnumConstant(payload.targetSlot());
                }
                buf.writeVarInt(payload.hotbarSlot());
                buf.writeBoolean(payload.isShiftPressed());
                buf.writeBoolean(payload.isCtrlPressed());
                buf.writeBoolean(payload.isCreativeMode());
            },
            buf -> {
                ActionType actionType = buf.readEnumConstant(ActionType.class);
                EquipmentSlot targetSlot = null;
                if (buf.readBoolean()) {
                    targetSlot = buf.readEnumConstant(EquipmentSlot.class);
                }
                int hotbarSlot = buf.readVarInt();
                boolean isShiftPressed = buf.readBoolean();
                boolean isCtrlPressed = buf.readBoolean();
                boolean isCreativeMode = buf.readBoolean();
                return new SlotActionPayload(actionType, targetSlot, hotbarSlot, isShiftPressed, isCtrlPressed,
                        isCreativeMode);
            });

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
