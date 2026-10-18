package com.carrot123.eternal_career.mixin.compat.goety;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Pseudo
@Mixin(targets = "com.Polarice3.Goety.common.blocks.entities.BrewCauldronBlockEntity", remap = false)
public abstract class WitchPoleSoulDiscountMixin {
    @ModifyConstant(
            method = "discountCost()V",
            constant = @Constant(floatValue = 0.99F, ordinal = 0),
            remap = false,
            require = 1
    )
    private float eternalCareer$witchPoleSoulDiscount(float original) {
        return 0.90F;
    }
}