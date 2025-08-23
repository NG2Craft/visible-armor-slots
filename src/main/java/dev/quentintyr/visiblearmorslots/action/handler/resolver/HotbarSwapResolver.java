package dev.quentintyr.visiblearmorslots.action.handler.resolver;

import dev.quentintyr.visiblearmorslots.action.SlotAction;
import net.minecraft.entity.EquipmentSlot;
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

        // Swap the items
        player.equipStack(targetSlot, hotbarStack.copy());
        player.getInventory().setStack(hotbarSlot, equipped.copy());
    }
}
