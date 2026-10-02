package com.carrot123.eternal_career.mixin.compat.corpse;

import com.carrot123.eternal_career.compat.corpse.CorpseSoulBlessingTransferHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
        targets =
                "de.maxhenkel.corpse.gui.CorpseInventoryContainer",
        remap = false
)
public abstract class CorpseInventoryContainerMixin {

    @Unique
    private CorpseSoulBlessingTransferHelper.TransferState
            eternalCareer$transferState;

    @Inject(
            method = "transferItems()V",
            at = @At("HEAD"),
            remap = false
    )
    private void eternalCareer$beforeTransferItems(
            CallbackInfo ci
    ) {
        eternalCareer$transferState =
                CorpseSoulBlessingTransferHelper
                        .prepareTransfer(
                                this
                        );
    }

    @Inject(
            method = "transferItems()V",
            at = @At("RETURN"),
            remap = false
    )
    private void eternalCareer$afterTransferItems(
            CallbackInfo ci
    ) {
        CorpseSoulBlessingTransferHelper
                .restoreTransfer(
                        this,
                        eternalCareer$transferState,
                        false
                );

        eternalCareer$transferState =
                null;
    }
}
