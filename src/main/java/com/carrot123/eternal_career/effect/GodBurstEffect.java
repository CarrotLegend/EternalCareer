package com.carrot123.eternal_career.effect;

import com.carrot123.eternal_career.damage.ModDamageTypes;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public final class GodBurstEffect extends MobEffect {

    public static final int DURATION_TICKS = 2 * 20;

    public static final float MAX_HEALTH_DAMAGE_RATIO =
            0.05F;

    public GodBurstEffect() {
        super(
                MobEffectCategory.HARMFUL,
                0xF3C542
        );
    }

    @Override
    public boolean isDurationEffectTick(
            int duration,
            int amplifier
    ) {
        return duration == 1;
    }

    @Override
    public void applyEffectTick(
            LivingEntity entity,
            int amplifier
    ) {
        if (entity.level().isClientSide
                || !entity.isAlive()) {
            return;
        }

        float damage =
                entity.getMaxHealth()
                        * MAX_HEALTH_DAMAGE_RATIO;

        if (damage <= 0.0F) {
            return;
        }

        entity.hurt(
                ModDamageTypes.godBurst(entity),
                damage
        );
    }
}