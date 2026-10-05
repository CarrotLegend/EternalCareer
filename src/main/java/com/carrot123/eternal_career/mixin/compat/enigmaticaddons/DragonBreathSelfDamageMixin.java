package com.carrot123.eternal_career.mixin.compat.enigmaticaddons;

import auviotre.enigmatic.addon.contents.effects.RemainDragonBreath;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(
        value = RemainDragonBreath.class,
        remap = false
)
public abstract class DragonBreathSelfDamageMixin {

    @Inject(
            method = {
                    "applyInstantenousEffect(" +
                            "Lnet/minecraft/world/entity/Entity;" +
                            "Lnet/minecraft/world/entity/Entity;" +
                            "Lnet/minecraft/world/entity/LivingEntity;" +
                            "ID)V",

                    "m_19461_(" +
                            "Lnet/minecraft/world/entity/Entity;" +
                            "Lnet/minecraft/world/entity/Entity;" +
                            "Lnet/minecraft/world/entity/LivingEntity;" +
                            "ID)V"
            },
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1
    )
    private void eternalCareer$preventOwnerDamage(
            @Nullable Entity indirectSource,
            @Nullable Entity owner,
            LivingEntity target,
            int amplifier,
            double multiplier,
            CallbackInfo callback
    ) {
        if (owner == target) {
            callback.cancel();
        }
    }
}