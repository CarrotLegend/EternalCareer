package com.carrot123.eternal_career.fletching;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class BowModificationHelper {
    private static final String ROOT = "EternalCareer";
    private static final String LEVELS = "BowModifications";
    private static final String CAPTURED_POWER = "EternalCareerBowPowerLevel";
    private static final String CAPTURED_VELOCITY = "EternalCareerBowVelocityLevel";
    private static final String APPLIED_POWER = "EternalCareerBowPowerApplied";
    private static final String APPLIED_VELOCITY = "EternalCareerBowVelocityApplied";

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

    public static void capture(Entity entity, ItemStack bow) {
        if (!(entity instanceof AbstractArrow) || !isBow(bow)) {
            return;
        }
        int power = getLevel(bow, BowModifications.POWER.id());
        int velocity = getLevel(bow, BowModifications.VELOCITY.id());
        if (power > 0) {
            entity.getPersistentData().putInt(CAPTURED_POWER, power);
        }
        if (velocity > 0) {
            entity.getPersistentData().putInt(CAPTURED_VELOCITY, velocity);
        }
    }

    public static void applyOnSpawn(AbstractArrow arrow) {
        CompoundTag data = arrow.getPersistentData();
        int power = Math.max(0, Math.min(BowModifications.POWER.maxLevel(),
                data.getInt(CAPTURED_POWER)));
        if (power > 0 && !data.getBoolean(APPLIED_POWER)) {
            arrow.setBaseDamage(arrow.getBaseDamage()
                    * (1.0D + BowModifications.POWER.amountPerLevel() * power));
            data.putBoolean(APPLIED_POWER, true);
        }
        int velocity = Math.max(0, Math.min(BowModifications.VELOCITY.maxLevel(),
                data.getInt(CAPTURED_VELOCITY)));
        if (velocity > 0 && !data.getBoolean(APPLIED_VELOCITY)) {
            Vec3 motion = arrow.getDeltaMovement();
            arrow.setDeltaMovement(motion.scale(
                    1.0D + BowModifications.VELOCITY.amountPerLevel() * velocity));
            data.putBoolean(APPLIED_VELOCITY, true);
        }
    }
}
