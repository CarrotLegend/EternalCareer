package com.carrot123.eternal_career.career.api;

import com.carrot123.eternal_career.EternalCareer;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import vectorwing.farmersdelight.common.utility.ItemUtils;

public final class KitchenwareHelper {

    public static final TagKey<Item> KITCHENWARE_TAG =
            TagKey.create(
                    Registries.ITEM,
                    new ResourceLocation(
                            EternalCareer.MOD_ID,
                            "kitchenware"
                    )
            );

    private KitchenwareHelper() {
    }

    public static boolean isKitchenware(
            ItemStack stack
    ) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        return stack.is(KITCHENWARE_TAG)
                || ItemUtils.isKnife(stack);
    }
    @Deprecated
    public static boolean isKitchenKnife(
            ItemStack stack
    ) {
        return isKitchenware(stack);
    }
}
