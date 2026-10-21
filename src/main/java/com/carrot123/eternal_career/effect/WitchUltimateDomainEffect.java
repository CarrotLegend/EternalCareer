package com.carrot123.eternal_career.effect;

import com.carrot123.eternal_career.damage.ModDamageTypes;
import com.carrot123.eternal_career.registry.ModEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public final class WitchUltimateDomainEffect extends MobEffect {
    public WitchUltimateDomainEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xD5A6F0);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity holder, int amplifier) {
        if (!(holder.level() instanceof ServerLevel level) || !holder.isAlive()
                || level.getGameTime() % 5L != 0L || !holder.hasEffect(ModEffects.WITCH_ULTIMATE_DOMAIN.get())) {
            return;
        }
        float amount = 250.0F * (Math.min(3, Math.max(0, amplifier)) + 1);
        AABB bounds = holder.getBoundingBox().inflate(6.0D);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, bounds,
                entity -> entity != holder && entity.isAlive())) {
            if (holder.distanceToSqr(target) <= 36.0D) {
                target.hurt(ModDamageTypes.witchDomain(target, WitchBrewEvents.ultimateOwner(holder)), amount);
            }
        }
    }
}
