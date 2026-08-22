package com.carrot123.eternal_career.mixin.vanilla;

import com.carrot123.eternal_career.registry.ModEffects;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/** Allows only Ingredient Mark to pass an entity's normal effect immunity. */
@Mixin(LivingEntity.class)
public abstract class LivingEntityIngredientMarkMixin {
    @WrapOperation(
            method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;canBeAffected(Lnet/minecraft/world/effect/MobEffectInstance;)Z"))
    private boolean eternalCareer$allowIngredientMark(
            LivingEntity instance,
            MobEffectInstance candidate,
            Operation<Boolean> original) {
        if (candidate.getEffect() == ModEffects.INGREDIENT_MARK.get()) {
            return true;
        }
        return original.call(instance, candidate);
    }
}
