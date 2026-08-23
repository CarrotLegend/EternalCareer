package com.carrot123.eternal_career.career.api;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;

public final class DamageCategoryHelper {

    private DamageCategoryHelper() {
    }
    public static PlayerDamageCategory classify(
            Player player,
            DamageSource source
    ) {
        boolean directPlayerAttack =
                source.getEntity() == player
                        && source.getDirectEntity() == player
                        && source.is(
                                DamageTypes.PLAYER_ATTACK
                        );

        if (directPlayerAttack
                && KitchenwareHelper.isKitchenware(
                        player.getMainHandItem()
                )) {
            return PlayerDamageCategory.KITCHENWARE;
        }
        return PlayerDamageCategory.NON_KITCHENWARE;
    }
}