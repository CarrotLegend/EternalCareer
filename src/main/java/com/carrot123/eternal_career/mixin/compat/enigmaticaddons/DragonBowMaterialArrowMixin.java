package com.carrot123.eternal_career.mixin.compat.enigmaticaddons;

import auviotre.enigmatic.addon.contents.items.DragonBow;

import com.carrot123.eternal_career.event.SpecialBowProjectileDamageEvents;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
        value = DragonBow.class,
        remap = false
)
public abstract class DragonBowMaterialArrowMixin {

    @Inject(
            method = {
                    "releaseUsing(" +
                            "Lnet/minecraft/world/item/ItemStack;" +
                            "Lnet/minecraft/world/level/Level;" +
                            "Lnet/minecraft/world/entity/LivingEntity;" +
                            "I)V",

                    "m_5551_(" +
                            "Lnet/minecraft/world/item/ItemStack;" +
                            "Lnet/minecraft/world/level/Level;" +
                            "Lnet/minecraft/world/entity/LivingEntity;" +
                            "I)V"
            },
            at = @At("HEAD"),
            remap = false,
            require = 1
    )
    private void eternalCareer$prepareMaterialArrow(
            ItemStack stack,
            Level level,
            LivingEntity user,
            int timeLeft,
            CallbackInfo callback
    ) {
        if (level.isClientSide()
                || !(user instanceof Player player)) {
            return;
        }

        int chargeTicks =
                stack.getUseDuration()
                        - timeLeft;

        float power =
                (float) chargeTicks
                        / 32.0F;

        power =
                (
                        power * power
                                + power * 2.0F
                ) / 3.0F;

        power =
                Math.min(
                        power,
                        1.0F
                );

        if (power < 0.1F) {
            return;
        }

        SpecialBowProjectileDamageEvents
                .prepareDragonBowMaterialArrow(
                        player,
                        stack
                );
    }
}