package com.carrot123.eternal_career.mixin.compat.goety;

import com.Polarice3.Goety.common.entities.util.BrewGas;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.Polarice3.Goety.utils.ConstantPaths;
import com.carrot123.eternal_career.effect.WitchCriticalImmuneHelper;
import com.carrot123.eternal_career.effect.PerfectStateBrewHelper;
import java.util.List;
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

@Mixin(value = BrewGas.class, remap = false)
public abstract class BrewGasWitchCriticalMixin {
    @Shadow public List<MobEffectInstance> effects;

    @Inject(method = "tick()V", at = @At("TAIL"), remap = true, require = 1)
    private void eternalCareer$immuneGas(CallbackInfo callback) {
        BrewGas gas = (BrewGas) (Object) this;
        if (gas.level().isClientSide || gas.isRemoved()) {
            return;
        }
        List<MobEffectInstance> criticalEffects = WitchCriticalImmuneHelper.findAll(effects);
        if (criticalEffects.isEmpty()) {
            return;
        }
        String hitTag = ConstantPaths.gassed();
        for (LivingEntity target : gas.level().getEntitiesOfClass(LivingEntity.class, gas.getBoundingBox())) {
            if (target.isAlive() && !target.isAffectedByPotions() && !target.getTags().contains(hitTag)) {
                target.addTag(hitTag);
                for (MobEffectInstance critical : criticalEffects) {
                    WitchCriticalImmuneHelper.apply(gas, gas.getOwner(), target, critical);
                }
            }
        }
    }

    @Redirect(method = "tick()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z"),
            remap = true, require = 1)
    private boolean eternalCareer$perfectGasEffect(LivingEntity target, MobEffectInstance effect, Entity source) {
        return !PerfectStateBrewHelper.blocks(target, effect.getEffect()) && target.addEffect(effect, source);
    }

    @Redirect(method = "tick()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyInstantenousEffect(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;ID)V"),
            remap = true, require = 1)
    private void eternalCareer$perfectGasInstant(MobEffect effect, Entity direct, Entity source,
                                                   LivingEntity target, int amplifier, double intensity) {
        if (!PerfectStateBrewHelper.blocks(target, effect)) {
            effect.applyInstantenousEffect(direct, source, target, amplifier, intensity);
        }
    }

    @Redirect(method = "tick()V",
            at = @At(value = "INVOKE", target = "Lcom/Polarice3/Goety/common/effects/brew/BrewEffect;applyInstantenousEffect(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;ID)V"),
            remap = false, require = 1)
    private void eternalCareer$perfectGasCustom(BrewEffect effect, Entity direct, Entity source,
                                                  LivingEntity target, int amplifier, double intensity) {
        if (!PerfectStateBrewHelper.blocks(target, effect)) {
            effect.applyInstantenousEffect(direct, source, target, amplifier, intensity);
        }
    }
}
