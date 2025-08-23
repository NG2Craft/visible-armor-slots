package dev.quentintyr.visiblearmorslots.action.handler.resolver;

import dev.quentintyr.visiblearmorslots.action.SlotAction;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Handles F key swapping with off-hand
 */
public class OffhandSwapResolver {

    public static void resolve(SlotAction action, ServerPlayerEntity player) {
        // For offhand swap, we swap the mainhand with offhand
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        // Perform the swap
        player.setStackInHand(net.minecraft.util.Hand.MAIN_HAND, offHand);
        player.setStackInHand(net.minecraft.util.Hand.OFF_HAND, mainHand);
    }
}
