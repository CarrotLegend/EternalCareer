package com.carrot123.eternal_career.mixin.compat.goety;

import com.Polarice3.Goety.common.items.brew.ThrowableBrewItem;
import com.carrot123.eternal_career.compat.goety.CroneHatBrewEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ThrowableBrewItem.class, remap = false)
public abstract class ThrowableBrewCroneHatMixin {
    @Redirect(
            method = {
                    "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;",
                    "m_7203_(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;"
            },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", remap = true),
            remap = false,
            require = 1
    )
    private void eternalCareer$preserveThrownBrew(ItemStack stack, int amount, Level level, Player player, InteractionHand hand) {
        if (!CroneHatBrewEvents.hasCroneHat(player)) {
            stack.shrink(amount);
        }
    }
}
