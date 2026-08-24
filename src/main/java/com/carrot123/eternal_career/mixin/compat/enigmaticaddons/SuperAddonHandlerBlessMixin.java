package com.carrot123.eternal_career.mixin.compat.enigmaticaddons;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.carrot123.eternal_career.compat.curios.CurioSafeLookup;

import auviotre.enigmatic.addon.contents.items.BlessRing;
import auviotre.enigmatic.addon.handlers.SuperAddonHandler;
import auviotre.enigmatic.addon.registries.EnigmaticAddonItems;
import net.minecraft.world.entity.player.Player;

@Mixin(
        value = SuperAddonHandler.class,
        remap = false
)
public abstract class SuperAddonHandlerBlessMixin {

    @Inject(
            method =
                    "hasBlessRing(" +
                    "Lnet/minecraft/world/entity/player/Player;" +
                    ")Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1
    )
    private static void eternalCareer$safeBlessRingCheck(
            Player player,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (player == null) {
            callback.setReturnValue(false);
            return;
        }

        if (player.getPersistentData()
                .getBoolean(BlessRing.BLESS_SPAWN)) {

            callback.setReturnValue(true);
            return;
        }

        callback.setReturnValue(
                CurioSafeLookup.hasEquipped(
                        player,
                        EnigmaticAddonItems.BLESS_RING
                )
        );
    }
}