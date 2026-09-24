package com.carrot123.eternal_career.lich;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AdvancedLichAbilities {
    private AdvancedLichAbilities() {
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END
                || !(event.player instanceof ServerPlayer player)
                || !LichUtils.isAtLeastLichStage(player, LichStage.ADVANCED_LICH)) {
            return;
        }
        if (player.isUnderWater() && player.getAirSupply() < player.getMaxAirSupply()) {
            player.setAirSupply(player.getMaxAirSupply());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !LichUtils.isAtLeastLichStage(player, LichStage.ADVANCED_LICH)) {
            return;
        }
        if (event.getSource().is(DamageTypes.DROWN)) {
            event.setCanceled(true);
            return;
        }
        // Goety already halves this damage for a player who is also its native lich.
        if (!LichdomHelper.isLich(player)
                && (ModDamageSource.freezeAttacks(event.getSource())
                        || event.getSource().is(DamageTypeTags.IS_FREEZING))) {
            event.setAmount(event.getAmount() * 0.5F);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getAmount() <= 0.0F
                || event.getEntity().level().isClientSide
                || !(event.getSource().getEntity() instanceof ServerPlayer attacker)
                || event.getSource().getDirectEntity() != attacker
                || event.getEntity() == attacker
                || !event.getSource().is(DamageTypes.PLAYER_ATTACK)
                || !attacker.getMainHandItem().isEmpty()
                || !LichUtils.isAtLeastLichStage(attacker, LichStage.ADVANCED_LICH)) {
            return;
        }
        event.getEntity().addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), 900));
    }
}
