package com.carrot123.eternal_career.mixin.compat.goety;

import com.Polarice3.Goety.common.entities.util.BrewEffectCloud;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.carrot123.eternal_career.effect.PerfectStateBrewHelper;
import com.carrot123.eternal_career.effect.WitchCriticalImmuneHelper;
import com.carrot123.eternal_career.registry.ModEffects;
import java.util.List;
import java.util.Map;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BrewEffectCloud.class, remap = false)
public abstract class BrewEffectCloudWitchMixin {
    @Shadow private List<MobEffectInstance> effects;
    @Shadow private Map<Entity, Integer> victims;
    @Shadow private int reapplicationDelay;

    @Redirect(method = "tick()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"),
            remap = true, require = 1)
    private boolean eternalCareer$domainCloudSource(LivingEntity target, MobEffectInstance effect) {
        if (PerfectStateBrewHelper.blocks(target, effect.getEffect())) {
            return false;
        }
        if (effect.getEffect() == ModEffects.WITCH_DOMAIN.get()
                || effect.getEffect() == ModEffects.WITCH_SUPER_DOMAIN.get()
                || effect.getEffect() == ModEffects.WITCH_ULTIMATE_DOMAIN.get()) {
            return target.addEffect(effect, (BrewEffectCloud) (Object) this);
        }
        return target.addEffect(effect);
    }

    @Redirect(method = "tick()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyInstantenousEffect(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;ID)V"),
            remap = true, require = 1)
    private void eternalCareer$perfectCloudInstant(MobEffect effect, Entity direct, Entity source,
                                                     LivingEntity target, int amplifier, double intensity) {
        if (!PerfectStateBrewHelper.blocks(target, effect)) {
            effect.applyInstantenousEffect(direct, source, target, amplifier, intensity);
        }
    }

    @Redirect(method = "tick()V",
            at = @At(value = "INVOKE", target = "Lcom/Polarice3/Goety/common/effects/brew/BrewEffect;applyInstantenousEffect(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;ID)V"),
            remap = false, require = 1)
    private void eternalCareer$perfectCloudCustom(BrewEffect effect, Entity direct, Entity source,
                                                    LivingEntity target, int amplifier, double intensity) {
        if (!PerfectStateBrewHelper.blocks(target, effect)) {
            effect.applyInstantenousEffect(direct, source, target, amplifier, intensity);
        }
    }

    @Inject(method = "tick()V", at = @At("TAIL"), remap = true, require = 1)
    private void eternalCareer$immuneCloud(CallbackInfo callback) {
        BrewEffectCloud cloud = (BrewEffectCloud) (Object) this;
        if (cloud.level().isClientSide || cloud.isRemoved() || cloud.isWaiting()
                || cloud.tickCount % 5 != 0) {
            return;
        }
        List<MobEffectInstance> criticalEffects = WitchCriticalImmuneHelper.findAll(effects);
        if (criticalEffects.isEmpty()) {
            return;
        }
        double radiusSquared = (double) cloud.getRadius() * cloud.getRadius();
        for (LivingEntity target : cloud.level().getEntitiesOfClass(LivingEntity.class, cloud.getBoundingBox())) {
            if (!target.isAlive() || target.isAffectedByPotions() || victims.containsKey(target)) {
                continue;
            }
            double dx = target.getX() - cloud.getX();
            double dz = target.getZ() - cloud.getZ();
            if (dx * dx + dz * dz <= radiusSquared) {
                victims.put(target, cloud.tickCount + reapplicationDelay);
                for (MobEffectInstance critical : criticalEffects) {
                    WitchCriticalImmuneHelper.apply(cloud, cloud.getOwner(), target, critical);
                }
            }
        }
    }
}
