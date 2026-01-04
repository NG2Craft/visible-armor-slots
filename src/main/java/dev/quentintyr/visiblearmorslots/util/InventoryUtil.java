package dev.quentintyr.visiblearmorslots.util;

import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Utility methods for inventory operations
 */
public class InventoryUtil {

    /**
     * Forces a full inventory sync from server to client.
     * This ensures that all inventory changes are immediately visible to the player.
     * 
     * @param player The player whose inventory should be synced
     */
    public static void syncInventory(ServerPlayerEntity player) {
        player.currentScreenHandler.syncState();
        player.playerScreenHandler.syncState();
        player.currentScreenHandler.sendContentUpdates();
    }

    /**
     * Forces a full inventory sync with additional player screen handler notification.
     * Use this when modifying equipment slots or cursor items that may need special handling.
     * 
     * @param player The player whose inventory should be synced
     */
    public static void syncInventoryFull(ServerPlayerEntity player) {
        syncInventory(player);
        ((PlayerScreenHandler) player.playerScreenHandler).onContentChanged(player.getInventory());
    }
}
