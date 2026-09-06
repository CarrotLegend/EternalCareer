package com.carrot123.eternal_career.compat.redemption;

import auviotre.enigmatic.addon.handlers.SuperAddonHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** The single authorization path used by every redemption-item restriction. */
public final class RedemptionAccessController {
    private RedemptionAccessController() {
    }

    public static boolean isRedemptionItem(ItemStack stack) {
        return RedemptionItemHelper.isRedemptionItem(stack);
    }

    public static boolean hasRedemptionAccess(Player player) {
        return player != null && SuperAddonHandler.isTheBlessedOne(player);
    }

    public static boolean canUse(Player player, ItemStack stack) {
        return !isRedemptionItem(stack) || hasRedemptionAccess(player);
    }

    public static boolean deny(Player player, ItemStack stack) {
        return isRedemptionItem(stack) && !hasRedemptionAccess(player);
    }
}
