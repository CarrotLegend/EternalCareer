package com.carrot123.eternal_career.lich;

import com.Polarice3.Goety.common.blocks.entities.ArcaBlockEntity;
import com.Polarice3.Goety.common.capabilities.soulenergy.ISoulEnergy;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SPlayPlayerSoundPacket;
import com.Polarice3.Goety.config.MainConfig;
import com.Polarice3.Goety.utils.LichdomHelper;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.SEHelper;
import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class IntermediateLichAbilities {
    private IntermediateLichAbilities() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.isCanceled()
                || !(event.getEntity() instanceof ServerPlayer player)
                || !LichUtils.isAtLeastLichStage(player, LichStage.INTERMEDIATE_LICH)) {
            return;
        }

        ISoulEnergy soulEnergy = SEHelper.getCapability(player);
        if (!hasValidArca(player, soulEnergy)) {
            return;
        }

        int maxSouls = Math.max(0, MainConfig.MaxSouls.get());
        int currentSouls = Math.max(0, soulEnergy.getSoulEnergy());
        boolean enoughSouls = currentSouls >= maxSouls;

        // Goety also revives at the death location when its Arca has no safe stand-up position.
        SEHelper.teleportToArca(player);
        player.setHealth(1.0F);
        player.removeAllEffects();
        if (enoughSouls) {
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));
        } else {
            player.addEffect(new MobEffectInstance(GoetyEffects.SOUL_HUNGER.get(), 2400, 4,
                    false, false));
        }

        player.playSound(SoundEvents.WITHER_DEATH, 1.0F, 1.0F);
        ModNetwork.sendTo(player, new SPlayPlayerSoundPacket(
                SoundEvents.WITHER_DEATH, 1.0F, 1.0F));
        SEHelper.decreaseSESouls(player, Math.min(currentSouls, maxSouls));
        SEHelper.sendSEUpdatePacket(player);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END
                || !(event.player instanceof ServerPlayer player)
                || !LichUtils.isAtLeastLichStage(player, LichStage.INTERMEDIATE_LICH)) {
            return;
        }

        if (player.hasEffect(GoetyEffects.SOUL_HUNGER.get())
                && SEHelper.getSoulsAmount(player, MainConfig.MaxSouls.get())) {
            player.removeEffect(GoetyEffects.SOUL_HUNGER.get());
        }

        if (!MainConfig.LichSoulHeal.get()
                || (player.isOnFire() && !MobUtil.isFireImmune(player))
                || LichdomHelper.smited(player) > 0
                || player.getHealth() >= player.getMaxHealth()
                || player.tickCount % (MathHelper.secondsToTicks(
                        MainConfig.LichHealSeconds.get()) + 1) != 0
                || !SEHelper.getSoulsAmount(player, MainConfig.LichHealCost.get())) {
            return;
        }

        player.heal(MainConfig.LichHealAmount.get().floatValue());
        Vec3 movement = player.getDeltaMovement();
        player.serverLevel().sendParticles(ParticleTypes.SCULK_SOUL,
                player.getRandomX(0.5D), player.getRandomY(), player.getRandomZ(0.5D),
                0, movement.x * -0.2D, 0.1D, movement.z * -0.2D, 0.5D);
        SEHelper.decreaseSouls(player, MainConfig.LichHealCost.get());
    }

    private static boolean hasValidArca(ServerPlayer player, ISoulEnergy soulEnergy) {
        if (soulEnergy == null || !soulEnergy.getSEActive()) {
            return false;
        }
        BlockPos arcaPos = soulEnergy.getArcaBlock();
        ResourceKey<Level> arcaDimension = soulEnergy.getArcaBlockDimension();
        if (arcaPos == null || arcaDimension == null || player.getServer() == null) {
            return false;
        }
        ServerLevel arcaLevel = player.getServer().getLevel(arcaDimension);
        return arcaLevel != null
                && arcaLevel.getBlockEntity(arcaPos) instanceof ArcaBlockEntity arca
                && player.getUUID().equals(arca.getOwnerUUID());
    }
}
