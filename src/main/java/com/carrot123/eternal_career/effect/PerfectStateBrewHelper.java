package com.carrot123.eternal_career.effect;

import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.carrot123.eternal_career.registry.ModEffects;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class PerfectStateBrewHelper {
    private PerfectStateBrewHelper() {
    }

    public static boolean blocks(LivingEntity target, MobEffect effect) {
        return target.hasEffect(ModEffects.PERFECT_STATE.get())
                && effect.getCategory() == MobEffectCategory.HARMFUL;
    }

    public static boolean blocks(LivingEntity target, BrewEffect effect) {
        return target.hasEffect(ModEffects.PERFECT_STATE.get())
                && effect.getCategory() == MobEffectCategory.HARMFUL;
    }

    public static Collection<MobEffectInstance> clampLevel(Collection<MobEffectInstance> effects) {
        List<MobEffectInstance> result = new ArrayList<>(effects.size());
        for (MobEffectInstance effect : effects) {
            if (effect.getEffect() == ModEffects.PERFECT_STATE.get() && effect.getAmplifier() != 0) {
                result.add(new MobEffectInstance(effect.getEffect(), effect.getDuration(), 0,
                        effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            } else {
                result.add(effect);
            }
        }
        return result;
    }
}
