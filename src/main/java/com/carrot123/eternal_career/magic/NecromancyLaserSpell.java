package com.carrot123.eternal_career.magic;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.SoundUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.carrot123.eternal_career.entity.projectile.NecromancyLaserEntity;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

public final class NecromancyLaserSpell extends Spell {
    @Override
    public int defaultSoulCost() {
        return 5;
    }

    @Override
    public int defaultCastDuration() {
        return 0;
    }

    @Override
    public int defaultSpellCooldown() {
        return 20;
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
        return List.of(
                ModEnchantments.POTENCY.get(),
                ModEnchantments.VELOCITY.get()
        );
    }

    @Override
    public void SpellResult(
            ServerLevel world,
            LivingEntity caster,
            ItemStack staff,
            SpellStat spellStat
    ) {
        int potency = spellStat.getPotency();
        float velocity = spellStat.getVelocity();

        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            velocity += WandUtil.getLevels(ModEnchantments.VELOCITY.get(), caster);
        }

        Vec3 direction = caster.getViewVector(1.0F).normalize();
        NecromancyLaserEntity laser = new NecromancyLaserEntity(
                caster.getX() + direction.x * 0.5D,
                caster.getEyeY() - 0.2D,
                caster.getZ() + direction.z * 0.5D,
                direction.x,
                direction.y,
                direction.z,
                world
        );

        laser.setOwner(caster);
        laser.setExtraDamage(potency);
        laser.setBoltSpeed(Math.max(0, (int) velocity));
        laser.setDeltaMovement(direction.scale(1.6D + 0.2D * Math.max(0.0F, velocity)));
        world.addFreshEntity(laser);
        SoundUtil.playNecroBolt(caster);
    }
}
