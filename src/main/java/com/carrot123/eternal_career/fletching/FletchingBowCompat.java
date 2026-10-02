package com.carrot123.eternal_career.fletching;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class FletchingBowCompat {

    public static final ResourceLocation DRAGON_BOW =
            new ResourceLocation(
                    "enigmaticaddons",
                    "dragon_bow"
            );

    public static final ResourceLocation DRAGON_BREATH_ARROW =
            new ResourceLocation(
                    "enigmaticaddons",
                    "dragon_breath_arrow"
            );

    private FletchingBowCompat() {
    }

    public static boolean usesManualProjectileCompatibility(
            ItemStack stack
    ) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        ResourceLocation id =
                ForgeRegistries.ITEMS
                        .getKey(stack.getItem());

        if (id == null) {
            return false;
        }

        if (DRAGON_BOW.equals(id)) {
            return true;
        }

        return "morebows".equals(id.getNamespace())
                && id.getPath().endsWith("_bow");
    }

    public static boolean isDragonBreathArrow(
            Entity entity
    ) {
        if (entity == null) {
            return false;
        }

        ResourceLocation id =
                ForgeRegistries.ENTITY_TYPES
                        .getKey(entity.getType());

        return DRAGON_BREATH_ARROW.equals(id);
    }
}