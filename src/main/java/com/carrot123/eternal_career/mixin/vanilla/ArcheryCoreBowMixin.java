package com.carrot123.eternal_career.mixin.vanilla;

import com.carrot123.eternal_career.archery.ArcheryCoreEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BowItem.class)
public abstract class ArcheryCoreBowMixin {
    @ModifyArg(method = "releaseUsing",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
            index = 0)
    private Entity eternalCareer$tagArcheryShot(Entity arrow, ItemStack bow,
            Level level, LivingEntity shooter, int timeLeft) {
        ArcheryCoreEvents.tagShot(arrow, shooter);
        return arrow;
    }
}
