package com.carrot123.eternal_career.mixin.vanilla;

import com.carrot123.eternal_career.compat.enigmaticdelicacy.WeaponMasterGloryHelper;
import com.carrot123.eternal_career.damage.ModDamageTypes;
import com.carrot123.eternal_career.registry.ModEffects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityBreakStanceMixin {

    @Inject(
            method = "getDamageAfterArmorAbsorb",
            at = @At("HEAD"),
            cancellable = true
    )
    private void eternalCareer$bypassArmor(
            DamageSource source,
            float amount,
            CallbackInfoReturnable<Float> cir
    ) {

        if (source.is(ModDamageTypes.GOD_BURST)) {
            cir.setReturnValue(amount);
            return;
        }

        LivingEntity target =
                (LivingEntity) (Object) this;

        if (!target.hasEffect(
                ModEffects.BREAK_STANCE.get()
        )) {
            return;
        }

        if (!(source.getEntity()
                instanceof Player player)) {
            return;
        }

        if (source.getDirectEntity()
                != player) {
            return;
        }

        if (!source.is(
                DamageTypes.PLAYER_ATTACK
        )) {
            return;
        }

        if (!WeaponMasterGloryHelper.isEquipped(
                player
        )) {
            return;
        }

        cir.setReturnValue(amount);
    }
}
