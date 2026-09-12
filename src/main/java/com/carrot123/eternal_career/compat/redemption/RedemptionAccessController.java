package com.carrot123.eternal_career.compat.redemption;

import auviotre.enigmatic.addon.handlers.SuperAddonHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** The single cached authorization path used by every redemption restriction. */
public final class RedemptionAccessController {
    static final String ACCESS_CACHE_TAG =
            "eternal_career:redemption_access_cached";
    static final String RECHECK_TICKS_TAG =
            "eternal_career:redemption_recheck_ticks";

    private RedemptionAccessController() {
    }

    public static boolean isRedemptionItem(ItemStack stack) {
        return RedemptionItemHelper.isRedemptionItem(stack);
    }

    public static boolean hasRedemptionAccess(Player player) {
        if (player == null) {
            return false;
        }

        // Client-side checks are presentation-only and have no authoritative persisted cache.
        if (player.level().isClientSide) {
            return queryCurrentAccess(player);
        }

        CompoundTag data = player.getPersistentData();
        if (data.contains(ACCESS_CACHE_TAG, Tag.TAG_BYTE)) {
            return data.getBoolean(ACCESS_CACHE_TAG);
        }
        return refreshImmediately(player);
    }

    public static boolean canUse(Player player, ItemStack stack) {
        return !isRedemptionItem(stack) || hasRedemptionAccess(player);
    }

    public static boolean deny(Player player, ItemStack stack) {
        return isRedemptionItem(stack) && !hasRedemptionAccess(player);
    }

    static void initializeCacheIfAbsent(Player player) {
        if (!player.getPersistentData().contains(ACCESS_CACHE_TAG, Tag.TAG_BYTE)) {
            refreshImmediately(player);
        }
    }

    static void copyCacheState(Player original, Player clone) {
        CompoundTag originalData = original.getPersistentData();
        CompoundTag cloneData = clone.getPersistentData();
        if (originalData.contains(ACCESS_CACHE_TAG, Tag.TAG_BYTE)) {
            cloneData.putBoolean(ACCESS_CACHE_TAG, originalData.getBoolean(ACCESS_CACHE_TAG));
        }
        if (originalData.contains(RECHECK_TICKS_TAG, Tag.TAG_INT)) {
            cloneData.putInt(RECHECK_TICKS_TAG, originalData.getInt(RECHECK_TICKS_TAG));
        }
    }

    static void scheduleRecheck(Player player, int ticks) {
        CompoundTag data = player.getPersistentData();
        data.putInt(RECHECK_TICKS_TAG,
                Math.max(data.getInt(RECHECK_TICKS_TAG), Math.max(1, ticks)));
    }

    public static boolean isRecheckPending(Player player) {
        return player != null
                && player.getPersistentData().getInt(RECHECK_TICKS_TAG) > 0;
    }

    static void advanceRecheck(Player player) {
        CompoundTag data = player.getPersistentData();
        int remaining = data.getInt(RECHECK_TICKS_TAG);
        if (remaining <= 0) {
            return;
        }

        if (queryCurrentAccess(player)) {
            data.putBoolean(ACCESS_CACHE_TAG, true);
            data.remove(RECHECK_TICKS_TAG);
            return;
        }

        remaining--;
        if (remaining > 0) {
            data.putInt(RECHECK_TICKS_TAG, remaining);
            return;
        }

        data.putBoolean(ACCESS_CACHE_TAG, false);
        data.remove(RECHECK_TICKS_TAG);
    }

    private static boolean refreshImmediately(Player player) {
        boolean access = queryCurrentAccess(player);
        player.getPersistentData().putBoolean(ACCESS_CACHE_TAG, access);
        return access;
    }

    /** The only call site allowed to query Enigmatic Addons' live identity state. */
    private static boolean queryCurrentAccess(Player player) {
        return SuperAddonHandler.isTheBlessedOne(player);
    }
}
