package dev.quentintyr.visiblearmorslots.gui.widget;

import dev.quentintyr.visiblearmorslots.gui.SlotInfo;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * Custom slot widget for armor pieces with validation
 */
public class ArmorSlotWidget {
    private final SlotInfo.SlotType slotType;
    private final int x;
    private final int y;

    private static final Identifier EMPTY_HELMET_SLOT = Identifier.of(
            "minecraft:textures/item/empty_armor_slot_helmet.png");
    private static final Identifier EMPTY_CHEST_SLOT = Identifier.of(
            "minecraft:textures/item/empty_armor_slot_chestplate.png");
    private static final Identifier EMPTY_LEGS_SLOT = Identifier.of(
            "minecraft:textures/item/empty_armor_slot_leggings.png");
    private static final Identifier EMPTY_BOOTS_SLOT = Identifier.of(
            "minecraft:textures/item/empty_armor_slot_boots.png");

    public ArmorSlotWidget(SlotInfo.SlotType slotType, int x, int y) {
        this.slotType = slotType;
        this.x = x;
        this.y = y;
    }

    public void render(DrawContext context, ItemStack stack, int mouseX, int mouseY) {
        if (stack.isEmpty()) {
            // Draw empty slot texture
            Identifier emptyTexture = getEmptySlotTexture();
            context.drawTexture(emptyTexture, x, y, 0, 0, 16, 16, 16, 16);
        } else {
            // Draw item with count (always 1 for armor)
            context.drawItem(stack, x, y);
            context.drawItemInSlot(net.minecraft.client.MinecraftClient.getInstance().textRenderer, stack, x, y);
        }
    }

    public boolean canAcceptItem(ItemStack stack) {
        if (stack.isEmpty())
            return true;

        Item item = stack.getItem();
        if (!(item instanceof ArmorItem armorItem)) {
            return false;
        }

        EquipmentSlot itemSlot = armorItem.getSlotType();
        return itemSlot == slotType.getEquipmentSlot();
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16;
    }

    private Identifier getEmptySlotTexture() {
        return switch (slotType) {
            case HELMET -> EMPTY_HELMET_SLOT;
            case CHESTPLATE -> EMPTY_CHEST_SLOT;
            case LEGGINGS -> EMPTY_LEGS_SLOT;
            case BOOTS -> EMPTY_BOOTS_SLOT;
            default -> EMPTY_HELMET_SLOT;
        };
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public SlotInfo.SlotType getSlotType() {
        return slotType;
    }
}
