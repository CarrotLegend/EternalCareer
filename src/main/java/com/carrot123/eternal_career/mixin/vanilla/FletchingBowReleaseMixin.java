package com.carrot123.eternal_career.mixin.vanilla;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.carrot123.eternal_career.fletching.BowModificationHelper;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(BowItem.class)
public abstract class FletchingBowReleaseMixin {

    @Redirect(
            method = {"releaseUsing", "m_5551_"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private boolean eternalCareer$captureFletchingMojmap(
            Level targetLevel,
            Entity arrow,
            ItemStack bow,
            Level level,
            LivingEntity shooter,
            int timeLeft
    ) {
        if (!level.isClientSide()) {
            BowModificationHelper.capture(arrow, bow);
        }

        return targetLevel.addFreshEntity(arrow);
    }

    @Redirect(
            method = {"releaseUsing", "m_5551_"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;m_7967_(Lnet/minecraft/world/entity/Entity;)Z",
                    remap = false
            ),
            remap = false,
            require = 0
    )
    private boolean eternalCareer$captureFletchingSrg(
            Level targetLevel,
            Entity arrow,
            ItemStack bow,
            Level level,
            LivingEntity shooter,
            int timeLeft
    ) {
        if (!level.isClientSide()) {
            BowModificationHelper.capture(arrow, bow);
        }

        return targetLevel.addFreshEntity(arrow);
    }
}
