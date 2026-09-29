package com.carrot123.eternal_career.fletching;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStack.TooltipPart;
import net.minecraft.world.entity.projectile.AbstractArrow;

public final class BowModificationHelper {
    private static final String ROOT = "EternalCareer";
    private static final String LEVELS = "BowModifications";

    private BowModificationHelper() {
    }

    public static boolean isBow(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BowItem;
    }

    public static int getLevel(ItemStack stack, ResourceLocation id) {
        BowModification modification = BowModifications.byId(id);
        if (!isBow(stack) || modification == null || !stack.hasTag()) {
            return 0;
        }
        CompoundTag root = stack.getTag().getCompound(ROOT);
        int level = root.getCompound(LEVELS).getInt(id.getPath());
        return Math.max(0, Math.min(modification.maxLevel(), level));
    }

    public static void setLevel(ItemStack stack, ResourceLocation id, int level) {
        BowModification modification = BowModifications.byId(id);
        if (!isBow(stack) || modification == null) {
            return;
        }
        CompoundTag root = stack.getOrCreateTagElement(ROOT);
        CompoundTag levels = root.getCompound(LEVELS);
        levels.putInt(id.getPath(), Math.max(0, Math.min(modification.maxLevel(), level)));
        root.put(LEVELS, levels);
        stack.hideTooltipPart(TooltipPart.MODIFIERS);
    }

    public static boolean canApply(ItemStack stack, BowModification modification) {
        return modification != null && isBow(stack)
                && getLevel(stack, modification.id()) < modification.maxLevel();
    }

    public static boolean addLevel(ItemStack stack, ResourceLocation id) {
        BowModification modification = BowModifications.byId(id);
        if (!canApply(stack, modification)) {
            return false;
        }
        setLevel(stack, id, getLevel(stack, id) + 1);
        return true;
    }

    public static void captureProjectile(AbstractArrow arrow, ItemStack bow) {
        int blast = getLevel(bow, BowModifications.BLAST.id());
        int bloodthirst = getLevel(bow, BowModifications.BLOODTHIRST.id());
        if (blast > 0) {
            arrow.getPersistentData().putInt("EternalCareerFletchingBlast", blast);
        }
        if (bloodthirst > 0) {
            arrow.getPersistentData().putInt("EternalCareerFletchingBloodthirst", bloodthirst);
        }
    }
}
