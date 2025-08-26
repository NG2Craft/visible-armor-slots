package dev.quentintyr.visiblearmorslots.gui;

import dev.quentintyr.visiblearmorslots.action.ActionType;
import dev.quentintyr.visiblearmorslots.config.ModConfig;
import dev.quentintyr.visiblearmorslots.gui.widget.ArmorSlotWidget;
import dev.quentintyr.visiblearmorslots.gui.widget.OffhandSlotWidget;
import dev.quentintyr.visiblearmorslots.mixin.client.HandledScreenAccessor;
import dev.quentintyr.visiblearmorslots.network.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced armor slots overlay system
 */
public class ArmorSlotsOverlay {
    private static final Identifier COLUMN_TEXTURE = new Identifier("visiblearmorslots",
            "textures/gui/extra-slots.png");

    private final List<ArmorSlotWidget> armorSlots = new ArrayList<>();
    private OffhandSlotWidget offhandSlot;
    private int baseX, baseY;

    public int getBaseX() {
        return baseX;
    }

    public int getBaseY() {
        return baseY;
    }

    private boolean visible = false;

    // Cache the last known equipment state to detect changes
    private ItemStack lastHelmet = ItemStack.EMPTY;
    private ItemStack lastChestplate = ItemStack.EMPTY;
    private ItemStack lastLeggings = ItemStack.EMPTY;
    private ItemStack lastBoots = ItemStack.EMPTY;
    private ItemStack lastOffhand = ItemStack.EMPTY;

    public void initialize(HandledScreen<?> screen) {
        if (!ModConfig.getInstance().isEnabled()) {
            visible = false;
            return;
        }

        // Don't show on inventory screen
        if (screen instanceof InventoryScreen) {
            visible = false;
            return;
        }

        visible = true;

        calculatePosition(screen);
        createSlots();
    }

    private void calculatePosition(HandledScreen<?> screen) {
        HandledScreenAccessor accessor = (HandledScreenAccessor) screen;
        int screenLeft = accessor.getX();
        int screenTop = accessor.getY();
        int screenHeight = accessor.getBackgroundHeight();

        ModConfig config = ModConfig.getInstance();

        // Handle potion effects auto-positioning
        if (config.isAutoPositioning()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            PlayerEntity player = mc.player;
            if (player != null && player.hasStatusEffect(StatusEffects.REGENERATION)) {
                // Shift further left when potion effects are visible
                baseX = screenLeft - 28 - 24;
            } else {
                baseX = screenLeft - 28;
            }
        } else {
            baseX = screenLeft - 28;
        }

        // Apply configuration margins and positioning
        if (config.getPositioning() == ModConfig.Side.RIGHT) {
            baseX = screenLeft + accessor.getBackgroundWidth() + config.getMarginX();
        } else {
            baseX = screenLeft - 28 - config.getMarginX();
        }

        baseY = screenTop + screenHeight - 104 + config.getMarginY();
    }

    private void createSlots() {
        armorSlots.clear();

        int itemX = baseX + 4;

        // Create armor slots from top to bottom
        armorSlots.add(new ArmorSlotWidget(SlotInfo.SlotType.HELMET, itemX, baseY + 4));
        armorSlots.add(new ArmorSlotWidget(SlotInfo.SlotType.CHESTPLATE, itemX, baseY + 22));
        armorSlots.add(new ArmorSlotWidget(SlotInfo.SlotType.LEGGINGS, itemX, baseY + 40));
        armorSlots.add(new ArmorSlotWidget(SlotInfo.SlotType.BOOTS, itemX, baseY + 58));

        // Create offhand slot
        offhandSlot = new OffhandSlotWidget(itemX, baseY + 80);
    }

    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (!visible)
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null)
            return;

        // Get fresh inventory reference each frame to ensure real-time updates
        PlayerInventory inventory = player.getInventory();

        // Check for equipment changes and log them
        ItemStack currentHelmet = inventory.getArmorStack(3);
        ItemStack currentChestplate = inventory.getArmorStack(2);
        ItemStack currentLeggings = inventory.getArmorStack(1);
        ItemStack currentBoots = inventory.getArmorStack(0);
        ItemStack currentOffhand = player.getOffHandStack();

        // Update cached states for change detection
        if (!ItemStack.areEqual(lastHelmet, currentHelmet)) {
            lastHelmet = currentHelmet.copy();
        }
        if (!ItemStack.areEqual(lastChestplate, currentChestplate)) {
            lastChestplate = currentChestplate.copy();
        }
        if (!ItemStack.areEqual(lastLeggings, currentLeggings)) {
            lastLeggings = currentLeggings.copy();
        }
        if (!ItemStack.areEqual(lastBoots, currentBoots)) {
            lastBoots = currentBoots.copy();
        }
        if (!ItemStack.areEqual(lastOffhand, currentOffhand)) {
            lastOffhand = currentOffhand.copy();
        }

        // Draw column background
        drawContext.drawTexture(COLUMN_TEXTURE, baseX, baseY, 0, 0, 24, 100, 24, 100);

        // Render armor slots with fresh data
        for (int i = 0; i < armorSlots.size(); i++) {
            ArmorSlotWidget slot = armorSlots.get(i);
            // Get current armor stack - this should update in real-time
            // Try both approaches to ensure we get the most current data
            ItemStack stack = inventory.getArmorStack(3 - i); // Reverse order: helmet=3, boots=0

            // Force refresh by checking if the stack has changed
            slot.render(drawContext, stack, mouseX, mouseY);

            // Highlight slot if mouse is over it
            if (slot.isMouseOver(mouseX, mouseY)) {
                drawContext.fill(slot.getX(), slot.getY(), slot.getX() + 16, slot.getY() + 16,
                        0x80FFFFFF); // Semi-transparent white overlay
            }
        }

        // Render offhand slot with fresh data
        ItemStack offhandStack = player.getOffHandStack();
        offhandSlot.render(drawContext, offhandStack, mouseX, mouseY);

        if (offhandSlot.isMouseOver(mouseX, mouseY)) {
            drawContext.fill(offhandSlot.getX(), offhandSlot.getY(),
                    offhandSlot.getX() + 16, offhandSlot.getY() + 16,
                    0x80FFFFFF);
        }
    }

    public void renderTooltips(DrawContext drawContext, int mouseX, int mouseY) {
        if (!visible || !ModConfig.getInstance().shouldShowTooltips())
            return;

        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null)
            return;

        PlayerInventory inventory = player.getInventory();

        // Check armor slots for tooltips
        for (int i = 0; i < armorSlots.size(); i++) {
            ArmorSlotWidget slot = armorSlots.get(i);
            if (slot.isMouseOver(mouseX, mouseY)) {
                ItemStack stack = inventory.getArmorStack(3 - i);
                if (!stack.isEmpty()) {
                    drawContext.drawItemTooltip(mc.textRenderer, stack, mouseX, mouseY);
                } else {
                    // Show empty slot tooltip
                    Text tooltip = Text.translatable("gui.visiblearmorslots.empty." +
                            slot.getSlotType().name().toLowerCase());
                    drawContext.drawTooltip(mc.textRenderer, tooltip, mouseX, mouseY);
                }
                return;
            }
        }

        // Check offhand slot
        if (offhandSlot.isMouseOver(mouseX, mouseY)) {
            ItemStack offhandStack = player.getOffHandStack();
            if (!offhandStack.isEmpty()) {
                drawContext.drawItemTooltip(mc.textRenderer, offhandStack, mouseX, mouseY);
            } else {
                Text tooltip = Text.translatable("gui.visiblearmorslots.empty.offhand");
                drawContext.drawTooltip(mc.textRenderer, tooltip, mouseX, mouseY);
            }
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible)
            return false;

        MinecraftClient mc = MinecraftClient.getInstance();

        // Check armor slots
        for (ArmorSlotWidget slot : armorSlots) {
            if (slot.isMouseOver((int) mouseX, (int) mouseY)) {
                handleSlotClick(slot.getSlotType(), button, mc);
                return true;
            }
        }

        // Check offhand slot
        if (offhandSlot.isMouseOver((int) mouseX, (int) mouseY)) {
            handleSlotClick(SlotInfo.SlotType.OFFHAND, button, mc);
            return true;
        }

        // If click is inside overlay column, block vanilla drop logic
        if (mouseX >= baseX && mouseX < baseX + 24 && mouseY >= baseY && mouseY < baseY + 100) {
            return true;
        }

        return false;
    }

    private void handleSlotClick(SlotInfo.SlotType slotType, int button, MinecraftClient mc) {
        if (mc.player == null || mc.currentScreen == null)
            return;

        boolean isShiftPressed = Screen.hasShiftDown();
        boolean isCtrlPressed = Screen.hasControlDown();

        System.out.println("handleSlotClick: slot=" + slotType + ", button=" + button + ", shift=" + isShiftPressed
                + ", ctrl=" + isCtrlPressed);

        ActionType actionType;
        if (isShiftPressed) {
            actionType = ActionType.QUICK_TRANSFER;
            System.out.println("SHIFT-CLICK detected for slot: " + slotType);
        } else {
            actionType = ActionType.MOUSE_SWAP;
            System.out.println("Normal click for slot: " + slotType);
        }

        // Send packet using the existing networking system
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                NetworkManager.SLOT_ACTION_PACKET_ID,
                createPacketByteBuf(actionType, slotType.getEquipmentSlot(), -1,
                        isShiftPressed, isCtrlPressed,
                        mc.player != null && mc.player.getAbilities().creativeMode));
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!visible)
            return false;

        MinecraftClient mc = MinecraftClient.getInstance();

        // Handle hotbar swapping (keys 1-9)
        if (keyCode >= 49 && keyCode <= 57) { // GLFW key codes for 1-9
            int hotbarSlot = keyCode - 49;
            // For now, we'll swap with the helmet slot when a number is pressed
            // This could be enhanced to swap with the currently highlighted slot
            // TODO swapping with helmet
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                    NetworkManager.SLOT_ACTION_PACKET_ID,
                    createPacketByteBuf(ActionType.HOTBAR_SWAP, SlotInfo.SlotType.HELMET.getEquipmentSlot(),
                            hotbarSlot, false, false,
                            mc.player != null && mc.player.getAbilities().creativeMode));
            return true;
        }

        // Handle F key for offhand swap
        if (keyCode == 70) { // F key
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                    NetworkManager.SLOT_ACTION_PACKET_ID,
                    createPacketByteBuf(ActionType.OFFHAND_SWAP, null, -1, false, false,
                            mc.player != null && mc.player.getAbilities().creativeMode));
            return true;
        }

        return false;
    }

    private net.minecraft.network.PacketByteBuf createPacketByteBuf(ActionType actionType,
            net.minecraft.entity.EquipmentSlot targetSlot,
            int hotbarSlot,
            boolean isShiftPressed,
            boolean isCtrlPressed,
            boolean isCreativeMode) {
        net.minecraft.network.PacketByteBuf buf = new net.minecraft.network.PacketByteBuf(
                io.netty.buffer.Unpooled.buffer());
        buf.writeEnumConstant(actionType);
        buf.writeBoolean(targetSlot != null);
        if (targetSlot != null) {
            buf.writeEnumConstant(targetSlot);
        }
        buf.writeVarInt(hotbarSlot);
        buf.writeBoolean(isShiftPressed);
        buf.writeBoolean(isCtrlPressed);
        buf.writeBoolean(isCreativeMode);
        return buf;
    }

    public boolean isVisible() {
        return visible;
    }
}
