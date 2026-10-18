package com.carrot123.eternal_career.mixin.compat.goety;

import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.common.effects.brew.BrewEffect;
import com.carrot123.eternal_career.effect.PerfectStateBrewHelper;
import com.carrot123.eternal_career.registry.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BrewItem.class, remap = false)
public abstract class BrewItemWitchDomainMixin {
    @Redirect(method = "finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"),
            remap = true, require = 1)
    private boolean eternalCareer$domainDrinkSource(LivingEntity target, MobEffectInstance effect) {
        if (PerfectStateBrewHelper.blocks(target, effect.getEffect())) {
            return false;
        }
        if (effect.getEffect() == ModEffects.WITCH_DOMAIN.get()) {
            return target.addEffect(effect, target);
        }
        return target.addEffect(effect);
    }

    @Redirect(method = "finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffect;applyInstantenousEffect(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;ID)V"),
            remap = true, require = 1)
    private void eternalCareer$perfectDrinkInstant(MobEffect effect, Entity direct, Entity source,
                                                     LivingEntity target, int amplifier, double intensity) {
        if (!PerfectStateBrewHelper.blocks(target, effect)) {
            effect.applyInstantenousEffect(direct, source, target, amplifier, intensity);
        }
    }

    @Redirect(method = "finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;",
            at = @At(value = "INVOKE", target = "Lcom/Polarice3/Goety/common/effects/brew/BrewEffect;drinkBlockEffect(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/LivingEntity;II)V"),
            remap = false, require = 1)
    private void eternalCareer$perfectDrinkCustom(BrewEffect effect, Entity direct, Entity source,
                                                    LivingEntity target, int amplifier, int area) {
        if (!PerfectStateBrewHelper.blocks(target, effect)) {
            effect.drinkBlockEffect(direct, source, target, amplifier, area);
        }
    }
}
