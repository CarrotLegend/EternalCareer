package com.carrot123.eternal_career.soulblessing;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public final class SoulBlessingSlot extends SlotItemHandler {

    private static final int[][] POSITIONS = {
            {36, 34},
            {142, 34},
            {36, 61},
            {142, 61},
            {36, 88},
            {142, 88}
    };

    private final SoulBlessingInventory inventory;

    public SoulBlessingSlot(
            SoulBlessingInventory inventory,
            int slot
    ) {
        super(
                inventory,
                slot,
                POSITIONS[slot][0],
                POSITIONS[slot][1]
        );

        this.inventory = inventory;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return inventory.isItemValid(
                getSlotIndex(),
                stack
        );
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public boolean isSameInventory(Slot other) {
        return other instanceof SoulBlessingSlot blessing
                && blessing.inventory == inventory;
    }
}