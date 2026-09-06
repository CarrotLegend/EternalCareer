package com.carrot123.eternal_career.compat.redemption;

import net.minecraft.world.item.ItemStack;

/** Internal view implemented by supported player-operated recipe menus. */
public interface RedemptionMenuAccess {
    boolean eternalCareer$isResultSlot(int menuSlot);

    Iterable<ItemStack> eternalCareer$getInputStacks();
}
