package com.carrot123.eternal_career.career.api;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;

/** Classifies damage already known to be attributed to a player. */
public final class DamageCategoryHelper {
    private DamageCategoryHelper() {
    }

    /**
     * Returns kitchenware only for the player's direct vanilla-style attack
     * source while the actual main-hand attack item is a Farmer's Delight knife.
     * Every other player-attributed source is non-kitchenware.
     */
    public static PlayerDamageCategory classify(Player player, DamageSource source) {
        boolean directPlayerAttack = source.getEntity() == player
                && source.getDirectEntity() == player
                && source.is(DamageTypes.PLAYER_ATTACK);
        if (directPlayerAttack && KitchenwareHelper.isKitchenKnife(player.getMainHandItem())) {
            return PlayerDamageCategory.KITCHENWARE;
        }
        return PlayerDamageCategory.NON_KITCHENWARE;
    }
}
