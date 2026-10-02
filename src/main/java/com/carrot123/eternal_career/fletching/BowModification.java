package com.carrot123.eternal_career.fletching;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record BowModification(
        ResourceLocation id,
        String nameKey,
        String descriptionKey,
        String effectKey,
        int maxLevel,
        double amountPerLevel,
        boolean fixedEffect,
        Supplier<Item> item,
        List<Material> materials
) {

    public Component effect(int level) {
        return fixedEffect
                ? Component.translatable(effectKey)
                : Component.translatable(
                        effectKey,
                        (int) Math.round(
                                amountPerLevel
                                        * level
                                        * 100.0D
                        )
                );
    }

    public ItemStack icon() {
        return new ItemStack(item.get());
    }

    public record Material(
            Supplier<Item> itemSupplier,
            int count
    ) {

        public Material(
                Item item,
                int count
        ) {
            this(
                    () -> item,
                    count
            );
        }

        public Item item() {
            return itemSupplier.get();
        }

        public ItemStack icon() {
            return new ItemStack(item());
        }
    }
}