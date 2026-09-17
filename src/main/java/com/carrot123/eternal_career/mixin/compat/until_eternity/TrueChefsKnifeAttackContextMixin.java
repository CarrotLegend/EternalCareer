package com.carrot123.eternal_career.mixin.compat.until_eternity;

import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;
import com.carrot123.until_eternity.combat.TrueChefsKnifeAbsoluteDamageContext;
import net.minecraft.world.damagesource.DamageSource;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Prevents Until Eternity from restoring attacks rejected by the redemption gate. */
@Mixin(value = TrueChefsKnifeAbsoluteDamageContext.class, remap = false)
public abstract class TrueChefsKnifeAttackContextMixin {
    @Inject(
            method = "withAttack(Lnet/minecraft/world/entity/player/Player;"
                    + "Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;"
                    + "FLjava/util/function/Supplier;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1)
    private static void eternalCareer$requireRedemptionRing(
            Player player,
            Entity target,
            DamageSource source,
            float originalDamage,
            Supplier<Boolean> action,
            CallbackInfoReturnable<Boolean> callback) {
        if (RedemptionAccessController.deny(player, player.getMainHandItem())) {
            callback.setReturnValue(false);
        }
    }
}
