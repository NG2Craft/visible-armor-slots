package dev.quentintyr.visiblearmorslots.action.handler.resolver;

import dev.quentintyr.visiblearmorslots.action.SlotAction;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

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

        // If creative mode, handle differently
        if (action.isCreativeMode()) {
            handleCreativeSwap(player, targetSlot, currentEquipped, cursorStack);
        } else {
            handleSurvivalSwap(player, targetSlot, currentEquipped, cursorStack);
        }
    }

    private static void handleCreativeSwap(ServerPlayerEntity player, EquipmentSlot slot,
            ItemStack equipped, ItemStack cursor) {
        // In creative, just set the equipment directly
        if (!cursor.isEmpty()) {
            ItemStack copy = cursor.copy();
            copy.setCount(1); // Armor always has count of 1
            player.equipStack(slot, copy);
        } else {
            player.equipStack(slot, ItemStack.EMPTY);
        }
    }

    private static void handleSurvivalSwap(ServerPlayerEntity player, EquipmentSlot slot,
            ItemStack equipped, ItemStack cursor) {
        // Standard survival swap behavior
        player.equipStack(slot, cursor);
        player.currentScreenHandler.setCursorStack(equipped);
    }
}
