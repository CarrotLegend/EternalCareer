package com.carrot123.eternal_career.mixin.compat.enigmaticlegacy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.items.generic.ItemBaseCurio;
import com.carrot123.eternal_career.compat.curios.CurioSafeLookup;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

@Mixin(
        value = ItemBaseCurio.class,
        remap = false
)
public abstract class ItemBaseCurioCanEquipMixin {

    @Inject(
            method =
                    "canEquip(" +
                    "Ltop/theillusivec4/curios/api/SlotContext;" +
                    "Lnet/minecraft/world/item/ItemStack;" +
                    ")Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1
    )
    private void eternalCareer$safeCanEquip(
            SlotContext context,
            ItemStack stack,
            CallbackInfoReturnable<Boolean> callback
    ) {
        Item self =
                (Item) (Object) this;

        callback.setReturnValue(
                !CurioSafeLookup.hasOtherEquipped(
                        context,
                        self
                )
        );
    }
}