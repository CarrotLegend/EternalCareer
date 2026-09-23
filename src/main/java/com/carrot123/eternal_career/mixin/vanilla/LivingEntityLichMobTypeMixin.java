package com.carrot123.eternal_career.mixin.vanilla;

import com.carrot123.eternal_career.lich.LichUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityLichMobTypeMixin {
    @Inject(method = "getMobType()Lnet/minecraft/world/entity/MobType;", at = @At("HEAD"),
            cancellable = true)
    private void eternalCareer$lichMobType(CallbackInfoReturnable<MobType> result) {
        if ((Object) this instanceof Player player && LichUtils.isLich(player)) {
            result.setReturnValue(MobType.UNDEAD);
        }
    }
}
