package dev.quentintyr.visiblearmorslots.action.handler.resolver;

import dev.quentintyr.visiblearmorslots.action.SlotAction;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;

/**
 * Handles mouse click actions (left/right click swapping)
 */
public class MouseSwapResolver {

    public static void resolve(SlotAction action, ServerPlayerEntity player) {
        EquipmentSlot targetSlot = action.getTargetSlot();
        if (targetSlot == null)
            return;

        // Get current item in equipment slot
        ItemStack currentEquipped = player.getEquippedStack(targetSlot);

        // Get cursor item
        ItemStack cursorStack = player.currentScreenHandler.getCursorStack();

        // Validate that the cursor item can be equipped in this slot
        if (!canEquipInSlot(cursorStack, targetSlot)) {
            return; // Invalid item for this slot - do nothing
        }

        // If creative mode, handle differently
        if (action.isCreativeMode()) {
            handleCreativeSwap(player, targetSlot, currentEquipped, cursorStack);
        } else {
            handleSurvivalSwap(player, targetSlot, currentEquipped, cursorStack);
        }
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

    private static void handleCreativeSwap(ServerPlayerEntity player, EquipmentSlot slot,
            ItemStack equipped, ItemStack cursor) {
        // In creative mode, we need to handle this like survival mode for proper cursor
        // behavior
        // Don't use creative-specific logic that might cause issues

        // Equip the new item (or clear the slot if cursor is empty)
        player.equipStack(slot, cursor.copy());

        // Put the previously equipped item on the cursor
        player.currentScreenHandler.setCursorStack(equipped.copy());

        // Mark the cursor stack as changed to ensure client sync
        player.currentScreenHandler.syncState();
        player.playerScreenHandler.syncState();

        // Force a screen handler refresh to ensure changes are sent to client
        player.currentScreenHandler.sendContentUpdates();

        // Try marking the player inventory as dirty
        ((PlayerScreenHandler) player.playerScreenHandler).onContentChanged(player.getInventory());
    }

    private static void handleSurvivalSwap(ServerPlayerEntity player, EquipmentSlot slot,
            ItemStack equipped, ItemStack cursor) {
        // Standard survival swap behavior
        player.equipStack(slot, cursor.copy());
        player.currentScreenHandler.setCursorStack(equipped.copy());

        // Force inventory sync to client - try multiple approaches
        player.currentScreenHandler.syncState();
        player.playerScreenHandler.syncState();

        // Force a screen handler refresh to ensure changes are sent to client
        player.currentScreenHandler.sendContentUpdates();

        // Try marking the player inventory as dirty
        ((PlayerScreenHandler) player.playerScreenHandler).onContentChanged(player.getInventory());
    }
}
