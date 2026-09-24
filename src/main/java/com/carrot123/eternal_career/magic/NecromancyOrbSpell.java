package com.carrot123.eternal_career.magic;

import java.util.List;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.projectiles.NecroBolt;
import com.Polarice3.Goety.common.magic.ChargingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.SoundUtil;
import com.Polarice3.Goety.utils.WandUtil;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

public final class NecromancyOrbSpell extends ChargingSpell {
    private static final float BASE_DAMAGE = 60.0F;
    private static final int CAST_UP_TICKS = 10;
    private static final int SHOT_INTERVAL_TICKS = 10;
    private static final int BASE_SHOTS = 4;
    private static final int COOLDOWN_TICKS = 20;

    @Override
    public int defaultSoulCost() {
        return 8;
    }

    @Override
    public int defaultCastUp() {
        return CAST_UP_TICKS;
    }

    @Override
    public int Cooldown() {
        return SHOT_INTERVAL_TICKS;
    }

    @Override
    public int shotsNumber() {
        return BASE_SHOTS;
    }

    @Override
    public int shotsNumber(LivingEntity caster, ItemStack staff) {
        return BASE_SHOTS
                + WandUtil.getLevels(ModEnchantments.POTENCY.get(), caster);
    }

    @Override
    public int defaultSpellCooldown() {
        return COOLDOWN_TICKS;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.CAST_SPELL.get();
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        return List.of(ModEnchantments.POTENCY.get());
    }

    @Override
    public void SpellResult(
            ServerLevel world,
            LivingEntity caster,
            ItemStack staff,
            SpellStat spellStat
    ) {
        int potency = spellStat.getPotency();

        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
        }

        Vec3 direction = caster.getViewVector(1.0F);

        NecroBolt orb = new NecroBolt(
                caster.getX() + direction.x * 0.5D,
                caster.getEyeY() - 0.2D,
                caster.getZ() + direction.z * 0.5D,
                direction.x,
                direction.y,
                direction.z,
                world
        );

        float originalNecroBoltDamage =
                SpellConfig.NecroBoltDamage.get().floatValue()
                        * WandUtil.damageMultiply();

        orb.setOwner(caster);
        orb.setExtraDamage(
                BASE_DAMAGE
                        - originalNecroBoltDamage
                        + potency
        );

        world.addFreshEntity(orb);

        SoundUtil.playNecroBolt(caster);
    }
}