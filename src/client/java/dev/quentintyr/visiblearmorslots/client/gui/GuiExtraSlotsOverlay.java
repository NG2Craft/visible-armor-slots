package dev.quentintyr.visiblearmorslots.client.gui;

import dev.quentintyr.visiblearmorslots.mixin.client.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class GuiExtraSlotsOverlay {
    private static final Identifier TEXTURE = new Identifier("visiblearmorslots", "textures/gui/extra-slots.png");

    private GuiExtraSlotsOverlay() {
    }

    // renders once for any HandledScreen
    public static void renderOnHandledScreen(DrawContext ctx, HandledScreen<?> screen, int mouseX, int mouseY, float delta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;

        // anchor to the container
        int left = ((HandledScreenAccessor) screen).getX();
        int top  = ((HandledScreenAccessor) screen).getY();
        int backgroundHeight = ((HandledScreenAccessor) screen).getBackgroundHeight();

        // position column to be on the left, but locked to the bottom
        int baseX = left - 28;
        int baseY = top + backgroundHeight - 104;

        PlayerInventory inv = mc.player.getInventory();

        // draw the background for all slots with extra-slots.png -> res 24x100
        ctx.drawTexture(TEXTURE, baseX, baseY, 0, 0, 24, 100, 24, 100);

        int itemX = baseX + 4; // 4 pixels to the first slot

        // helmet
        ItemStack helmet = inv.getArmorStack(3);
        if (!helmet.isEmpty()) {
            int itemY = baseY + 4;
            ctx.drawItem(helmet, itemX, itemY);
            ctx.drawItemInSlot(mc.textRenderer, helmet, itemX, itemY);
        }

        // chestplate
        ItemStack chestplate = inv.getArmorStack(2);
        if (!chestplate.isEmpty()) {
            int itemY = baseY + 20 + 2;
            ctx.drawItem(chestplate, itemX, itemY);
            ctx.drawItemInSlot(mc.textRenderer, chestplate, itemX, itemY);
        }

        // leggings
        ItemStack leggings = inv.getArmorStack(1);
        if (!leggings.isEmpty()) {
            int itemY = baseY + 40;
            ctx.drawItem(leggings, itemX, itemY);
            ctx.drawItemInSlot(mc.textRenderer, leggings, itemX, itemY);
        }

        // boots
        ItemStack boots = inv.getArmorStack(0);
        if (!boots.isEmpty()) {
            int itemY = baseY + 60 - 2; 
            ctx.drawItem(boots, itemX, itemY);
            ctx.drawItemInSlot(mc.textRenderer, boots, itemX, itemY);
        }

        // offhand
        ItemStack off = mc.player.getOffHandStack();
        if (!off.isEmpty()) {
            int itemY = baseY + 80;
            ctx.drawItem(off, itemX, itemY);
            ctx.drawItemInSlot(mc.textRenderer, off, itemX, itemY);
        }
    }
}
