package dev.quentintyr.visiblearmorslots.client.gui;

import dev.quentintyr.visiblearmorslots.mixin.client.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public final class GuiExtraSlotsOverlay {
    private static final Identifier COLUMN_TEXTURE = new Identifier("visiblearmorslots",
            "textures/gui/extra-slots.png");

    // slot silhouettes
    private static final Identifier EMPTY_HELMET_SLOT_TEX = new Identifier("minecraft",
            "textures/item/empty_armor_slot_helmet.png");
    private static final Identifier EMPTY_CHEST_SLOT_TEX = new Identifier("minecraft",
            "textures/item/empty_armor_slot_chestplate.png");
    private static final Identifier EMPTY_LEGS_SLOT_TEX = new Identifier("minecraft",
            "textures/item/empty_armor_slot_leggings.png");
    private static final Identifier EMPTY_BOOTS_SLOT_TEX = new Identifier("minecraft",
            "textures/item/empty_armor_slot_boots.png");
    private static final Identifier EMPTY_OFFHAND_SLOT_TEX = new Identifier("minecraft",
            "textures/item/empty_armor_slot_shield.png");

    private GuiExtraSlotsOverlay() {
    }

    // renders once for any HandledScreen except the InventoryScreen
    public static void renderOnHandledScreen(DrawContext drawContext, HandledScreen<?> screen, int mouseX, int mouseY,
            float delta) {
        MinecraftClient mcInstance = MinecraftClient.getInstance();
        if (mcInstance.player == null)
            return;

        if (screen instanceof InventoryScreen)
            return;

        int left = ((HandledScreenAccessor) screen).getX();
        int top = ((HandledScreenAccessor) screen).getY();
        int backgroundHeight = ((HandledScreenAccessor) screen).getBackgroundHeight();

        // position column left of the container, bottom-aligned
        int baseX = left - 28;
        int baseY = top + backgroundHeight - 104;
        int itemX = baseX + 4;

        // column background (24x100)
        drawContext.drawTexture(COLUMN_TEXTURE, baseX, baseY, 0, 0, 24, 100, 24, 100);

        PlayerInventory inv = mcInstance.player.getInventory();

        // helmet
        ItemStack helmet = inv.getArmorStack(3);
        int helmetY = baseY + 4;
        drawSlot(drawContext, helmet, itemX, helmetY, mouseX, mouseY, EquipmentSlot.HEAD, 0);

        // chestplate
        ItemStack chestplate = inv.getArmorStack(2);
        int chestplateY = baseY + 20 + 2;
        drawSlot(drawContext, chestplate, itemX, chestplateY, mouseX, mouseY, EquipmentSlot.CHEST, 1);

        // leggings
        ItemStack leggings = inv.getArmorStack(1);
        int leggingsY = baseY + 40;
        drawSlot(drawContext, leggings, itemX, leggingsY, mouseX, mouseY, EquipmentSlot.LEGS, 2);

        // boots
        ItemStack boots = inv.getArmorStack(0);
        int bootsY = baseY + 60 - 2;
        drawSlot(drawContext, boots, itemX, bootsY, mouseX, mouseY, EquipmentSlot.FEET, 3);

        // offhand
        ItemStack off = mcInstance.player.getOffHandStack();
        int offY = baseY + 80;
        drawSlot(drawContext, off, itemX, offY, mouseX, mouseY, null, 4);
    }

    // draw overlay slot otherwise silhouette
    private static void drawSlot(DrawContext drawContext, ItemStack stack, int x, int y, int mouseX, int mouseY,
            EquipmentSlot equipmentSlot, int rowIndex) {
        MinecraftClient mcInstance = MinecraftClient.getInstance();

        if (stack.isEmpty()) {
            Identifier empty = emptyTextureForRow(rowIndex);
            drawContext.drawTexture(empty, x, y, 0, 0, 16, 16, 16, 16);
        } else {
            drawContext.drawItem(stack, x, y);
            drawContext.drawItemInSlot(mcInstance.textRenderer, stack, x, y);
        }
    }

    private static Identifier emptyTextureForRow(int row) {
        return switch (row) {
            case 0 -> EMPTY_HELMET_SLOT_TEX;
            case 1 -> EMPTY_CHEST_SLOT_TEX;
            case 2 -> EMPTY_LEGS_SLOT_TEX;
            case 3 -> EMPTY_BOOTS_SLOT_TEX;
            default -> EMPTY_OFFHAND_SLOT_TEX;
        };
    }

}
