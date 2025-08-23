package dev.quentintyr.visiblearmorslots.action;

import net.minecraft.entity.EquipmentSlot;

/**
 * Data class containing information about a slot action
 */
public class SlotAction {
    private final ActionType actionType;
    private final EquipmentSlot targetSlot;
    private final int hotbarSlot; // For hotbar swaps
    private final boolean isShiftPressed;
    private final boolean isCtrlPressed;
    private final boolean isCreativeMode;

    public SlotAction(ActionType actionType, EquipmentSlot targetSlot, int hotbarSlot,
            boolean isShiftPressed, boolean isCtrlPressed, boolean isCreativeMode) {
        this.actionType = actionType;
        this.targetSlot = targetSlot;
        this.hotbarSlot = hotbarSlot;
        this.isShiftPressed = isShiftPressed;
        this.isCtrlPressed = isCtrlPressed;
        this.isCreativeMode = isCreativeMode;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public EquipmentSlot getTargetSlot() {
        return targetSlot;
    }

    public int getHotbarSlot() {
        return hotbarSlot;
    }

    public boolean isShiftPressed() {
        return isShiftPressed;
    }

    public boolean isCtrlPressed() {
        return isCtrlPressed;
    }

    public boolean isCreativeMode() {
        return isCreativeMode;
    }
}
