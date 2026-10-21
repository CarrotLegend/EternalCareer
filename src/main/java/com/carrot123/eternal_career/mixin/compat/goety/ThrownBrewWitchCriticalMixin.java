package com.carrot123.eternal_career.mixin.compat.goety;

import com.Polarice3.Goety.common.effects.brew.BrewEffectInstance;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.common.entities.projectiles.ThrownBrew;
import com.Polarice3.Goety.utils.BrewUtils;
import com.carrot123.eternal_career.effect.WitchCriticalImmuneHelper;
import com.carrot123.eternal_career.effect.PerfectStateBrewHelper;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ThrownBrew.class, remap = false)
public abstract class ThrownBrewWitchCriticalMixin {
    @Inject(method = "applySplash(Ljava/util/List;Ljava/util/List;Lnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"), remap = false, require = 1)
    private void eternalCareer$immuneSplash(List<MobEffectInstance> effects,
                                             List<BrewEffectInstance> brewEffects, Entity directHit,
                                             CallbackInfo callback) {
        ThrownBrew brew = (ThrownBrew) (Object) this;
        if (brew.level().isClientSide) {
            return;
        }
        List<MobEffectInstance> criticalEffects = WitchCriticalImmuneHelper.findAll(effects);
        if (criticalEffects.isEmpty()) {
            return;
        }
        int radius = BrewUtils.getAreaOfEffect(brew.getItem()) + 4;
        AABB bounds = brew.getBoundingBox().inflate(radius, radius / 2.0D, radius);
        double radiusSquared = (double) radius * radius;
        for (LivingEntity target : brew.level().getEntitiesOfClass(LivingEntity.class, bounds)) {
            if (brew.distanceToSqr(target) < radiusSquared) {
                for (MobEffectInstance critical : criticalEffects) {
                    WitchCriticalImmuneHelper.apply(brew, brew.getOwner(), target, critical);
                }
            }
        }
    }

    @Redirect(method = "applySplash(Ljava/util/List;Ljava/util/List;Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"),
            remap = true, require = 1)
    private boolean eternalCareer$perfectSplashEffect(LivingEntity target, MobEffectInstance effect, Entity source) {
        return !PerfectStateBrewHelper.blocks(target, effect.getEffect()) && target.addEffect(effect, source);
    }

    @Redirect(method = "applySplash(Ljava/util/List;Ljava/util/List;Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyInstantenousEffect(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;ID)V"),
            remap = true, require = 1)
    private void eternalCareer$perfectSplashInstant(MobEffect effect, Entity direct, Entity source,
                                                      LivingEntity target, int amplifier, double intensity) {
        if (!PerfectStateBrewHelper.blocks(target, effect)) {
            effect.applyInstantenousEffect(direct, source, target, amplifier, intensity);
        }
    }

    @Redirect(method = "applySplash(Ljava/util/List;Ljava/util/List;Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE", target = "Lcom/Polarice3/Goety/common/effects/brew/BrewEffect;applyInstantenousEffect(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;ID)V"),
            remap = false, require = 1)
    private void eternalCareer$perfectSplashCustom(BrewEffect effect, Entity direct, Entity source,
                                                     LivingEntity target, int amplifier, double intensity) {
        if (!PerfectStateBrewHelper.blocks(target, effect)) {
            effect.applyInstantenousEffect(direct, source, target, amplifier, intensity);
        }
    }
}
