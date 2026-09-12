package com.carrot123.eternal_career.compat.redemption;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Data-driven identity helpers for redemption items and the physical redemption ring. */
public final class RedemptionItemHelper {
    public static final ResourceLocation RING_OF_REDEMPTION_ID =
            new ResourceLocation("enigmaticaddons", "bless_ring");
    public static final TagKey<Item> REDEMPTION_ITEMS = TagKey.create(
            Registries.ITEM,
            new ResourceLocation(EternalCareer.MOD_ID, "redemption_items"));

    private RedemptionItemHelper() {
    }

    public static boolean isRedemptionItem(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(REDEMPTION_ITEMS);
    }

}
