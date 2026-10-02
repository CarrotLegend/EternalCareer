package com.carrot123.eternal_career.mixin.vanilla;

import com.carrot123.eternal_career.item.MaterialArrowItem;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Arrow.class)
public abstract class MaterialArrowPickupMixin {
    @Inject(method = "getPickupItem", at = @At("HEAD"), cancellable = true, require = 1)
    private void eternalCareer$pickupMaterialArrow(CallbackInfoReturnable<ItemStack> result) {
        Arrow arrow = (Arrow) (Object) this;
        MaterialArrowItem.Material material = MaterialArrowItem.materialOf(arrow);
        if (material != null) {
            result.setReturnValue(material.pickupStack());
        }
    }
}
