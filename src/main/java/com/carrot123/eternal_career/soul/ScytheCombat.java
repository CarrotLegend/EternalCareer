package com.carrot123.eternal_career.soul;

import com.carrot123.eternal_career.registry.ModTags;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;

public final class ScytheCombat {

    private static final int MAX_SOUL_REWARD = 4000;

    private ScytheCombat() {
    }

    public static ServerPlayer directAttacker(DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer player
                && source.getDirectEntity() == player
                && source.is(DamageTypes.PLAYER_ATTACK)
                && player.getMainHandItem().is(ModTags.Items.SCYTHES)) {

            return player;
        }

        return null;
    }

    public static float scaledDamage(
            float damage,
            double multiplier
    ) {
        if (!Double.isFinite(multiplier)) {
            multiplier = 1.0D;
        }

        multiplier = Math.max(0.0D, multiplier);

        double result = damage * multiplier;

        return (float) Math.min(
                Float.MAX_VALUE,
                Math.max(0.0D, result)
        );
    }

    public static int reward(float maxHealth) {
        if (!Float.isFinite(maxHealth)
                || maxHealth <= 0.0F) {
            return 1;
        }

        return (int) Math.max(
                1L,
                Math.min(
                        MAX_SOUL_REWARD,
                        (long) Math.floor(
                                maxHealth / 20.0D
                        )
                )
        );
    }

    public static int clampSoulReward(int reward) {
        return Math.max(
                0,
                Math.min(
                        MAX_SOUL_REWARD,
                        reward
                )
        );
    }
}