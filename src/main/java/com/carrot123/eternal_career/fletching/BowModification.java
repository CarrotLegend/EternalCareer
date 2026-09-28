package com.carrot123.eternal_career.fletching;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record BowModification(ResourceLocation id, String nameKey, String descriptionKey,
        String effectKey, int maxLevel, double amountPerLevel, Supplier<Item> item,
        List<Material> materials) {
    public ItemStack icon() {
        return new ItemStack(item.get());
    }

    public record Material(Item item, int count) {
        public ItemStack icon() {
            return new ItemStack(item);
        }
    }
}
