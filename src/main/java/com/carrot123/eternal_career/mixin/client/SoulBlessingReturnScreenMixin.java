
package com.carrot123.eternal_career.mixin.client;

import com.carrot123.eternal_career.client.SoulBlessingClientState;
import com.carrot123.eternal_career.client.SoulBlessingScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LocalPlayer.class)
public abstract class SoulBlessingReturnScreenMixin {

    @ModifyArg(
            method = "clientSideCloseContainer()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;setScreen(Lnet/minecraft/client/gui/screens/Screen;)V"
            ),
            index = 0,
            require = 1
    )
    private Screen eternalCareer$returnToInventoryWithoutFlicker(
            Screen originalScreen
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (originalScreen != null
                || minecraft.player == null
                || !(minecraft.screen instanceof SoulBlessingScreen)) {
            return originalScreen;
        }

        if (!SoulBlessingClientState.consumeInventoryReturn()) {
            return originalScreen;
        }

        return new InventoryScreen(minecraft.player);
    }
}
