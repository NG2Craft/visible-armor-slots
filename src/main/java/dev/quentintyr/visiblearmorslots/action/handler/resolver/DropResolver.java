package dev.quentintyr.visiblearmorslots.action.handler.resolver;

import dev.quentintyr.visiblearmorslots.action.SlotAction;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Handles dropping equipped armor items (Q key)
 */
public class DropResolver {

    public static void resolve(SlotAction action, ServerPlayerEntity player) {
        EquipmentSlot targetSlot = action.getTargetSlot();
        if (targetSlot == null)
            return;

        ItemStack equipped = player.getEquippedStack(targetSlot);
        if (equipped.isEmpty())
            return;

        System.out.println("SERVER: Drop requested for " + targetSlot + " with item " + equipped.getName().getString());

        // Drop the equipped item into the world
        player.dropItem(equipped, false);

        // Clear the equipment slot
        player.equipStack(targetSlot, ItemStack.EMPTY);

        // Force inventory sync to client - same pattern as other resolvers
        player.currentScreenHandler.syncState();
        player.playerScreenHandler.syncState();

        // Force a screen handler refresh to ensure changes are sent to client
        player.currentScreenHandler.sendContentUpdates();

        System.out.println("SERVER: Successfully dropped item from " + targetSlot);
    }
}
