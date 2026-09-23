package com.carrot123.eternal_career.mixin.compat.goety_revelation;

import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.carrot123.eternal_career.lich.LichSpellPotency;
import com.carrot123.eternal_career.lich.LichUtils;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Pseudo
@Mixin(targets = "com.mega.revelationfix.util.EventUtil", remap = false)
public abstract class EventUtilSpellPotencyMixin {
    @ModifyVariable(
            method = "modifySpellStatsWithoutEnchantment(Lcom/Polarice3/Goety/api/magic/ISpell;"
                    + "Lnet/minecraft/server/level/ServerLevel;"
                    + "Lnet/minecraft/world/entity/LivingEntity;"
                    + "Lnet/minecraft/world/item/ItemStack;"
                    + "Lcom/Polarice3/Goety/common/magic/SpellStat;)V",
            at = @At("STORE"),
            slice = @Slice(
                    from = @At(value = "INVOKE",
                            target = "Lcom/Polarice3/Goety/common/magic/SpellStat;getPotency()I"),
                    to = @At(value = "FIELD",
                            target = "Lcom/mega/revelationfix/common/init/ModAttributes;"
                                    + "SPELL_POWER_MULTIPLIER:Lnet/minecraftforge/registries/RegistryObject;")),
            ordinal = 0,
            remap = false,
            require = 1)
    private static double eternalCareer$adjustOriginalPotency(double currentPower,
            ISpell spell, ServerLevel level, LivingEntity caster, ItemStack focus,
            SpellStat stat, @Local(index = 5) Player player) {
        if (!LichUtils.isCareerLich(player)) {
            return currentPower;
        }
        return LichSpellPotency.adjust(currentPower, stat.getPotency(),
                spell.getSpellType(), true);
    }
}
