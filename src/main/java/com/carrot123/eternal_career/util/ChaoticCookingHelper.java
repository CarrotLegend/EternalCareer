package com.carrot123.eternal_career.util;

import com.carrot123.eternal_career.registry.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public final class ChaoticCookingHelper {
    private static final int EXTENSION_TICKS = 200;
    private static final int LEVEL_ONE_CAP_TICKS = 2400;
    private static final int HIGH_LEVEL_CAP_TICKS = 3000;

    private ChaoticCookingHelper() {
    }

    public static void extendEffect(Player player, int foodAmplifier) {
        if (player.level().isClientSide) {
            return;
        }
        MobEffect effect = ModEffects.CHAOTIC_COOKING.get();
        MobEffectInstance current = player.getEffect(effect);
        CookingState next = nextState(current == null ? 0 : current.getDuration(),
                current == null ? 0 : current.getAmplifier(), foodAmplifier);
        player.removeEffect(effect);
        player.addEffect(new MobEffectInstance(effect, next.duration(), next.amplifier(),
                false, true, true));
    }

    static CookingState nextState(int currentDuration, int currentAmplifier,
            int foodAmplifier) {
        int amplifier = Math.max(foodAmplifier, currentAmplifier);
        int cap = amplifier == 0 ? LEVEL_ONE_CAP_TICKS : HIGH_LEVEL_CAP_TICKS;
        int duration = (int) Math.min(cap,
                (long) Math.max(0, currentDuration) + EXTENSION_TICKS);
        return new CookingState(duration, amplifier);
    }

    record CookingState(int duration, int amplifier) {
    }
}
