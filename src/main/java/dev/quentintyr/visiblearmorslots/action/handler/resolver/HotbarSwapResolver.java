package dev.quentintyr.visiblearmorslots.action.handler.resolver;

import dev.quentintyr.visiblearmorslots.action.SlotAction;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Handles number key swapping with hotbar slots
 */
public class HotbarSwapResolver {

    public static void resolve(SlotAction action, ServerPlayerEntity player) {
        EquipmentSlot targetSlot = action.getTargetSlot();
        if (targetSlot == null)
            return;

        int hotbarSlot = action.getHotbarSlot();
        if (hotbarSlot < 0 || hotbarSlot > 8)
            return;

        ItemStack equipped = player.getEquippedStack(targetSlot);
        ItemStack hotbarStack = player.getInventory().getStack(hotbarSlot);

        // Validate that the hotbar item can be equipped in this slot
        if (!canEquipInSlot(hotbarStack, targetSlot)) {
            return; // Invalid item for this slot - do nothing
        }

        // Swap the items
        player.equipStack(targetSlot, hotbarStack.copy());
        player.getInventory().setStack(hotbarSlot, equipped.copy());

        // Force inventory sync to client - same pattern as other resolvers
        player.currentScreenHandler.syncState();
        player.playerScreenHandler.syncState();

        // Force a screen handler refresh to ensure changes are sent to client
        player.currentScreenHandler.sendContentUpdates();
    }

    private static boolean canEquipInSlot(ItemStack stack, EquipmentSlot slot) {
        if (stack.isEmpty()) {
            return true; // Can always remove items
        }

        // Check if it's armor and matches the slot
        if (stack.getItem() instanceof ArmorItem armorItem) {
            return armorItem.getSlotType() == slot;
        }

        // Allow non-armor items only in offhand
        return slot == EquipmentSlot.OFFHAND;
    }
}
