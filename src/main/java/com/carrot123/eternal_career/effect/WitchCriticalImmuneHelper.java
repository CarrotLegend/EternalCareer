package com.carrot123.eternal_career.effect;

import com.carrot123.eternal_career.registry.ModEffects;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class WitchCriticalImmuneHelper {
    private WitchCriticalImmuneHelper() {
    }

    public static MobEffectInstance find(List<MobEffectInstance> effects) {
        for (MobEffectInstance instance : effects) {
            if (instance.getEffect() == ModEffects.WITCH_CRITICAL.get()) {
                return instance;
            }
        }
        return null;
    }

    public static void apply(Entity direct, Entity owner, LivingEntity target, MobEffectInstance effect) {
        if (!target.level().isClientSide && target.isAlive() && !target.isAffectedByPotions()
                && effect != null && !target.hasEffect(ModEffects.PERFECT_STATE.get())) {
            effect.getEffect().applyInstantenousEffect(direct, owner, target, effect.getAmplifier(), 1.0D);
        }
    }
}
