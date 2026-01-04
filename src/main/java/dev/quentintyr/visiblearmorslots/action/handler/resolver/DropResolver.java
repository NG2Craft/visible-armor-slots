package dev.quentintyr.visiblearmorslots.action.handler.resolver;

import dev.quentintyr.visiblearmorslots.network.SlotActionPayload;
import dev.quentintyr.visiblearmorslots.util.InventoryUtil;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Handles dropping equipped armor items (Q key)
 */
public class DropResolver {

    public static void resolve(SlotActionPayload action, ServerPlayerEntity player) {
        if (player == null) {
            return;
        }
        
        EquipmentSlot targetSlot = action.targetSlot();
        if (targetSlot == null)
            return;

        ItemStack equipped = player.getEquippedStack(targetSlot);
        if (equipped.isEmpty())
            return;

        // Drop the equipped item into the world
        player.dropItem(equipped, false);

        // Clear the equipment slot
        player.equipStack(targetSlot, ItemStack.EMPTY);

        // Force inventory sync to client
        InventoryUtil.syncInventory(player);
    }
}
