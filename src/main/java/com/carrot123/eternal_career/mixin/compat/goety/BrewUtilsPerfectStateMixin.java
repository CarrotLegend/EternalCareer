package com.carrot123.eternal_career.mixin.compat.goety;

import com.Polarice3.Goety.utils.BrewUtils;
import com.carrot123.eternal_career.effect.PerfectStateBrewHelper;
import java.util.Collection;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = BrewUtils.class, remap = false)
public abstract class BrewUtilsPerfectStateMixin {
    @ModifyVariable(method = "setCustomEffects(Lnet/minecraft/world/item/ItemStack;Ljava/util/Collection;Ljava/util/Collection;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"), argsOnly = true, index = 1, remap = false, require = 1)
    private static Collection<MobEffectInstance> eternalCareer$limitPerfectState(Collection<MobEffectInstance> effects) {
        return PerfectStateBrewHelper.clampLevel(effects);
    }
}
