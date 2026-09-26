package com.carrot123.eternal_career.mixin.compat.goety_revelation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Pseudo
@Mixin(targets = "com.mega.revelationfix.util.entity.ATAHelper2", remap = false)
public abstract class BlessingScrollLookupMixin {
    @Inject(
            method = "hasBlessingScroll(Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("RETURN"),
            cancellable = true,
            remap = false,
            require = 1)
    private static void eternalCareer$requireRedemptionRing(
            LivingEntity entity,
            CallbackInfoReturnable<Boolean> callback) {
        if (callback.getReturnValueZ()
                && entity instanceof Player player
                && !RedemptionAccessController.hasEquipmentAccess(player)) {
            callback.setReturnValue(false);
        }
    }
}
