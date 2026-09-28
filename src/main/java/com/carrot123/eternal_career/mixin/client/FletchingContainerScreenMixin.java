package com.carrot123.eternal_career.mixin.client;

import com.carrot123.eternal_career.client.FletchingTableScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class FletchingContainerScreenMixin {

    @Inject(
            method = {
                    "renderSlot(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;)V",
                    "m_280092_(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/inventory/Slot;)V"
            },
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void eternalCareer$hideFletchingSlot(
            GuiGraphics graphics,
            Slot slot,
            CallbackInfo ci
    ) {
        if ((Object) this instanceof FletchingTableScreen screen
                && screen.hidesSlot(slot)) {
            ci.cancel();
        }
    }

    @Inject(
            method = {
                    "isHovering(Lnet/minecraft/world/inventory/Slot;DD)Z",
                    "m_97774_(Lnet/minecraft/world/inventory/Slot;DD)Z"
            },
            at = @At("HEAD"),
            cancellable = true,
            remap = false,
            require = 0
    )
    private void eternalCareer$ignoreFletchingSlot(
            Slot slot,
            double mouseX,
            double mouseY,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if ((Object) this instanceof FletchingTableScreen screen
                && screen.hidesSlot(slot)) {
            cir.setReturnValue(false);
        }
    }
}