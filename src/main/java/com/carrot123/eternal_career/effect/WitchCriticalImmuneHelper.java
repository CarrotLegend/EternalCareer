package com.carrot123.eternal_career.effect;

import com.carrot123.eternal_career.registry.ModEffects;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class WitchCriticalImmuneHelper {
    private WitchCriticalImmuneHelper() {
    }

    public static List<MobEffectInstance> findAll(List<MobEffectInstance> effects) {
        MobEffectInstance critical = null;
        MobEffectInstance superCritical = null;
        MobEffectInstance ultimateCritical = null;
        for (MobEffectInstance instance : effects) {
            if (instance.getEffect() == ModEffects.WITCH_CRITICAL.get() && critical == null) {
                critical = instance;
            } else if (instance.getEffect() == ModEffects.WITCH_SUPER_CRITICAL.get() && superCritical == null) {
                superCritical = instance;
            } else if (instance.getEffect() == ModEffects.WITCH_ULTIMATE_CRITICAL.get() && ultimateCritical == null) {
                ultimateCritical = instance;
            }
        }
        List<MobEffectInstance> result = new ArrayList<>(3);
        if (critical != null) {
            result.add(critical);
        }
        if (superCritical != null) {
            result.add(superCritical);
        }
        if (ultimateCritical != null) {
            result.add(ultimateCritical);
        }
        return result;
    }

    public static void apply(Entity direct, Entity owner, LivingEntity target, MobEffectInstance effect) {
        if (!target.level().isClientSide && target.isAlive() && !target.isAffectedByPotions()
                && effect != null && !target.hasEffect(ModEffects.PERFECT_STATE.get())) {
            effect.getEffect().applyInstantenousEffect(direct, owner, target, effect.getAmplifier(), 1.0D);
        }
    }
}
