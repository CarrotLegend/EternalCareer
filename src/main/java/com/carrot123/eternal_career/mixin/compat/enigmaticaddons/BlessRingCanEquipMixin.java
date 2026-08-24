package com.carrot123.eternal_career.mixin.compat.enigmaticaddons;

import auviotre.enigmatic.addon.contents.items.BlessRing;
import auviotre.enigmatic.addon.registries.EnigmaticAddonItems;

import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;

import com.carrot123.eternal_career.compat.curios.CurioSafeLookup;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import top.theillusivec4.curios.api.SlotContext;

@Mixin(
        value = BlessRing.class,
        remap = false
)
public abstract class BlessRingCanEquipMixin {

    private static final String CURSED_NEXT_SPAWN =
            "CursedNextSpawn";

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
    private void eternalCareer$safeBlessRingCanEquip(
            SlotContext context,
            ItemStack stack,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (!(context.entity()
                instanceof Player player)) {

            callback.setReturnValue(false);
            return;
        }

        Item self =
                (Item) (Object) this;

        if (CurioSafeLookup.hasOtherEquipped(
                context,
                self
        )) {
            callback.setReturnValue(false);
            return;
        }

        boolean cursed =
                player.getPersistentData()
                        .getBoolean(
                                CURSED_NEXT_SPAWN
                        )
                        || CurioSafeLookup.hasEquipped(
                                player,
                                EnigmaticItems.CURSED_RING
                        );

        if (cursed) {
            callback.setReturnValue(false);
            return;
        }

        boolean hasBrokenRing =
                CurioSafeLookup.hasEquipped(
                        player,
                        EnigmaticAddonItems.BROKEN_RING
                );

        callback.setReturnValue(
                !hasBrokenRing
        );
    }
}