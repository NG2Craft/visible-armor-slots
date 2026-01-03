package dev.quentintyr.visiblearmorslots.action.handler.resolver;

import dev.quentintyr.visiblearmorslots.network.SlotActionPayload;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;

/**
 * Handles mouse click actions (left/right click swapping)
 */
public class MouseSwapResolver {

    public static void resolve(SlotActionPayload action, ServerPlayerEntity player) {
        EquipmentSlot targetSlot = action.targetSlot();
        if (targetSlot == null)
            return;

        // Get current item in equipment slot (supports OFFHAND too)
        ItemStack currentEquipped = player.getEquippedStack(targetSlot);

        // Get cursor item
        ItemStack cursorStack = player.currentScreenHandler.getCursorStack();

        // Validate that the cursor item can be equipped in this slot
        if (!canEquipInSlot(cursorStack, targetSlot)) {
            return; // Invalid item for this slot - do nothing
        }

        // Perform the swap (same logic for both creative and survival)
        performSwap(player, targetSlot, currentEquipped, cursorStack);
    }

    private static boolean canEquipInSlot(ItemStack stack, EquipmentSlot slot) {
        if (stack.isEmpty()) {
            return true; // Can always remove items
        }

        // Check if it's armor and matches the slot
        if (stack.getItem() instanceof ArmorItem armorItem) {
            return armorItem.getSlotType() == slot;
        }

        // Allow any item in offhand
        return slot == EquipmentSlot.OFFHAND;
    }

    private static void performSwap(ServerPlayerEntity player, EquipmentSlot slot,
            ItemStack equipped, ItemStack cursor) {
        // Equip the new item (or clear the slot if cursor is empty)
        player.equipStack(slot, cursor.copy());

        // Put the previously equipped item on the cursor
        player.currentScreenHandler.setCursorStack(equipped.copy());

        // Force inventory sync to client
        player.currentScreenHandler.syncState();
        player.playerScreenHandler.syncState();
        player.currentScreenHandler.sendContentUpdates();
        ((PlayerScreenHandler) player.playerScreenHandler).onContentChanged(player.getInventory());
    }
}
