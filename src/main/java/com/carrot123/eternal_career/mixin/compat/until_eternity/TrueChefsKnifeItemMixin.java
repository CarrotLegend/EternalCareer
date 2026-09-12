package com.carrot123.eternal_career.mixin.compat.until_eternity;

import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;
import com.carrot123.until_eternity.item.TrueChefsKnifeItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Guards the knife's direct hit callback, including Cooking Frenzy. */
@Mixin(value = TrueChefsKnifeItem.class, remap = false)
public abstract class TrueChefsKnifeItemMixin {
    @Inject(
            method = "hurtEnemy(Lnet/minecraft/world/item/ItemStack;"
                    + "Lnet/minecraft/world/entity/LivingEntity;"
                    + "Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = true,
            require = 1)
    private void eternalCareer$guardKnifeHit(
            ItemStack stack,
            LivingEntity target,
            LivingEntity attacker,
            CallbackInfoReturnable<Boolean> callback) {
        if (attacker instanceof Player player
                && RedemptionAccessController.deny(player, stack)) {
            callback.setReturnValue(false);
        }
    }
}
