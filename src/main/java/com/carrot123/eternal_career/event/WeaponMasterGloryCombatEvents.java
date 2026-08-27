package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.enigmaticdelicacy.WeaponMasterGloryHelper;
import com.carrot123.eternal_career.effect.BreakStanceEffect;
import com.carrot123.eternal_career.effect.GodBurstEffect;
import com.carrot123.eternal_career.registry.ModEffects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class WeaponMasterGloryCombatEvents {

    public static final float MELEE_DAMAGE_MULTIPLIER =
            2.0F;

    public static final float GOD_BURST_CHANCE =
            0.10F;

    private WeaponMasterGloryCombatEvents() {
    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public static void onLivingHurt(
            LivingHurtEvent event
    ) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        Player player =
                getDirectMeleePlayer(
                        event.getSource()
                );

        if (player == null) {
            return;
        }

        if (!WeaponMasterGloryHelper.isEquipped(
                player
        )) {
            return;
        }

        if (event.getAmount() <= 0.0F) {
            return;
        }

        LivingEntity target =
                event.getEntity();

        target.addEffect(
                new MobEffectInstance(
                        ModEffects.BREAK_STANCE.get(),
                        BreakStanceEffect.DURATION_TICKS,
                        0,
                        false,
                        true,
                        true
                ),
                player
        );

        event.setAmount(
                event.getAmount()
                        * MELEE_DAMAGE_MULTIPLIER
        );
    }

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public static void onLivingDamage(
            LivingDamageEvent event
    ) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (event.getAmount() <= 0.0F) {
            return;
        }

        Player player =
                getDirectMeleePlayer(
                        event.getSource()
                );

        if (player == null) {
            return;
        }

        if (!WeaponMasterGloryHelper.isEquipped(
                player
        )) {
            return;
        }

        if (player.getRandom().nextFloat()
                >= GOD_BURST_CHANCE) {
            return;
        }

        LivingEntity target =
                event.getEntity();

        target.addEffect(
                new MobEffectInstance(
                        ModEffects.GOD_BURST.get(),
                        GodBurstEffect.DURATION_TICKS,
                        0,
                        false,
                        true,
                        true
                ),
                player
        );
    }

    private static Player getDirectMeleePlayer(
            DamageSource source
    ) {
        if (!(source.getEntity()
                instanceof Player player)) {
            return null;
        }

        if (source.getDirectEntity()
                != player) {
            return null;
        }

        if (!source.is(
                DamageTypes.PLAYER_ATTACK
        )) {
            return null;
        }

        return player;
    }
}