package com.carrot123.eternal_career.effect;

import com.carrot123.eternal_career.damage.ModDamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class WitchCriticalEffect extends MobEffect {
    public WitchCriticalEffect() {
        super(MobEffectCategory.HARMFUL, 0x9565BC);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyInstantenousEffect(Entity direct, Entity source, LivingEntity target,
                                         int amplifier, double intensity) {
        if (target.level().isClientSide || !target.isAlive()) {
            return;
        }
        Player player = WitchBrewEvents.resolvePlayer(source);
        if (player == null) {
            player = WitchBrewEvents.resolvePlayer(direct);
        }
        target.hurt(ModDamageTypes.witchCritical(target, direct, player),
                10.0F * (Math.min(3, Math.max(0, amplifier)) + 1));
    }

    @Override
    public void applyEffectTick(LivingEntity target, int amplifier) {
        applyInstantenousEffect(null, null, target, amplifier, 1.0D);
    }
}
