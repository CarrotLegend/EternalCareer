package com.carrot123.eternal_career.mixin.compat.until_eternity;

import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;
import com.carrot123.until_eternity.combat.TrueChefsKnifeAttackContext;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Prevents Until Eternity from restoring attacks rejected by the redemption gate. */
@Mixin(value = TrueChefsKnifeAttackContext.class, remap = false)
public abstract class TrueChefsKnifeAttackContextMixin {
    @Inject(
            method = "isEligible(Lnet/minecraft/world/entity/player/Player;"
                    + "Lnet/minecraft/world/entity/Entity;)Z",
            at = @At("RETURN"),
            cancellable = true,
            remap = false,
            require = 1)
    private static void eternalCareer$requireRedemptionRing(
            Player player,
            Entity target,
            CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValueZ()
                && RedemptionAccessController.deny(player, player.getMainHandItem())) {
            callback.setReturnValue(false);
        }
    }
}
