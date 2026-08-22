package com.carrot123.eternal_career.career.api;

import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.common.utility.ItemUtils;

/** Central Farmer's Delight kitchen-knife compatibility check. */
public final class KitchenwareHelper {
    private KitchenwareHelper() {
    }

    /**
     * Uses Farmer's Delight 1.3.2's own knife rule: the {@code knife_harvest}
     * ToolAction or the {@code farmersdelight:tools/knives} item tag.
     */
    public static boolean isKitchenKnife(ItemStack stack) {
        return ItemUtils.isKnife(stack);
    }
}
