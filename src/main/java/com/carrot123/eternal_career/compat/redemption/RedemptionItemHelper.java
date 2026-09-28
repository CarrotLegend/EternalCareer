package com.carrot123.eternal_career.compat.redemption;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class RedemptionItemHelper {
    public static final ResourceLocation RING_OF_REDEMPTION_ID =
            new ResourceLocation("enigmaticaddons", "bless_ring");

    public static final ResourceLocation FOOD_BOOK_ID =
            new ResourceLocation("solcarrot", "food_book");

    public static final TagKey<Item> REDEMPTION_ITEMS = TagKey.create(
            Registries.ITEM,
            new ResourceLocation(EternalCareer.MOD_ID, "redemption_items")
    );

    private RedemptionItemHelper() {
    }

    public static boolean isRedemptionItem(ItemStack stack) {
        return stack != null
                && !stack.isEmpty()
                && stack.is(REDEMPTION_ITEMS);
    }

    public static boolean isFoodBook(ItemStack stack) {
        return stack != null
                && !stack.isEmpty()
                && FOOD_BOOK_ID.equals(
                        ForgeRegistries.ITEMS.getKey(stack.getItem())
                );
    }

    public static boolean isUseExempt(ItemStack stack) {
        return isFoodBook(stack);
    }
}