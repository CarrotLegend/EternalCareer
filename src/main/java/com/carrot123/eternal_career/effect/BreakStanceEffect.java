package com.carrot123.eternal_career.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public final class BreakStanceEffect extends MobEffect {

    public static final int DURATION_TICKS = 5 * 20;

    public BreakStanceEffect() {
        super(
                MobEffectCategory.HARMFUL,
                0xB94A48
        );
    }
}
