package com.carrot123.eternal_career.mixin.client;

import com.carrot123.eternal_career.network.ArcheryCoreTogglePacket;
import com.carrot123.eternal_career.network.ModNetwork;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class ArcheryCoreContainerScreenMixin {
    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
    private void eternalCareer$toggleArcheryCore(Slot slot, int slotId, int button,
            ClickType clickType, CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        if (slot == null || button != 1 || clickType != ClickType.PICKUP
                || !screen.getMenu().getCarried().isEmpty()
                || !slot.getItem().is(ModItems.ARCHERY_MASTER_CORE.get())) {
            return;
        }
        ModNetwork.sendToServer(new ArcheryCoreTogglePacket(slotId,
                screen.getMenu().containerId));
        ci.cancel();
    }
}
