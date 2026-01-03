package dev.quentintyr.visiblearmorslots.action.handler.resolver;

import dev.quentintyr.visiblearmorslots.network.SlotActionPayload;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Handles shift-click actions to move items to inventory
 */
public class QuickTransferResolver {

    public static void resolve(SlotActionPayload action, ServerPlayerEntity player) {
        EquipmentSlot targetSlot = action.targetSlot();
        if (targetSlot == null)
            return;

        ItemStack equipped = player.getEquippedStack(targetSlot);
        if (equipped.isEmpty())
            return;

        // Try to move equipped item to main inventory
        if (insertIntoMainInventory(player, equipped)) {
            player.equipStack(targetSlot, ItemStack.EMPTY);

            // Force inventory sync to client
            player.currentScreenHandler.syncState();
            player.playerScreenHandler.syncState();
            player.currentScreenHandler.sendContentUpdates();
        }
    }

    private static boolean insertIntoMainInventory(ServerPlayerEntity player, ItemStack stack) {
        // Try to insert into player's main inventory (slots 0-35)
        for (int i = 9; i < 36; i++) { // Skip hotbar, start with main inventory
            ItemStack slotStack = player.getInventory().getStack(i);
            if (slotStack.isEmpty()) {
                player.getInventory().setStack(i, stack.copy());
                return true;
            } else if (slotStack.getItem() == stack.getItem()
                    && slotStack.getCount() < slotStack.getMaxCount()) {
                int remaining = slotStack.getMaxCount() - slotStack.getCount();
                if (remaining >= stack.getCount()) {
                    slotStack.increment(stack.getCount());
                    return true;
                }
            }
        }

        // Try hotbar if main inventory is full
        for (int i = 0; i < 9; i++) {
            ItemStack slotStack = player.getInventory().getStack(i);
            if (slotStack.isEmpty()) {
                player.getInventory().setStack(i, stack.copy());
                return true;
            }
        }

        return false; // No space available
    }
}
