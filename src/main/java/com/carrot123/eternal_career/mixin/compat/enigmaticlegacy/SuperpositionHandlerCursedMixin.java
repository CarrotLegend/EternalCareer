package com.carrot123.eternal_career.mixin.compat.enigmaticlegacy;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.registries.EnigmaticItems;
import com.carrot123.eternal_career.compat.curios.CurioSafeLookup;

import net.minecraft.world.entity.player.Player;

@Mixin(
        value = SuperpositionHandler.class,
        remap = false
)
public abstract class SuperpositionHandlerCursedMixin {

    private static final String CURSED_NEXT_SPAWN =
            "CursedNextSpawn";

    @Inject(
            method =
                    "isTheCursedOne(" +
                    "Lnet/minecraft/world/entity/player/Player;" +
                    ")Z",
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 1
    )
    private static void eternalCareer$safeCursedCheck(
            Player player,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (player == null) {
            callback.setReturnValue(false);
            return;
        }

        if (player.getPersistentData()
                .getBoolean(CURSED_NEXT_SPAWN)) {

            callback.setReturnValue(true);
            return;
        }

        callback.setReturnValue(
                CurioSafeLookup.hasEquipped(
                        player,
                        EnigmaticItems.CURSED_RING
                )
        );
    }
}