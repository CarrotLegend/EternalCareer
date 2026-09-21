package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.career.capability.soul.SoulCapability;
import com.carrot123.eternal_career.soul.ReaperSkillManager;
import com.carrot123.eternal_career.soul.ScytheCombat;
import com.carrot123.eternal_career.soul.SoulSetManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID)
public final class SoulCombatEvents {
    private static final Set<LivingEntity> REWARDED =
            Collections.newSetFromMap(new WeakHashMap<>());

    private static final Set<LivingDeathEvent> REVIVED =
            Collections.newSetFromMap(new WeakHashMap<>());

    private static final Set<UUID> REVIVING = new HashSet<>();

    private SoulCombatEvents() {
    }

    public static void clear() {
        REWARDED.clear();
        REVIVED.clear();
        REVIVING.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void reward(LivingDropsEvent event) {
        LivingEntity target = event.getEntity();

        if (target.level().isClientSide
                || !(target instanceof Enemy)
                || target instanceof Player) {
            return;
        }

        ServerPlayer player = ScytheCombat.directAttacker(event.getSource());

        if (player == null || REWARDED.contains(target)) {
            return;
        }

        var set = SoulSetManager.findActiveSoulSet(player);

        if (set.isEmpty()) {
            return;
        }

        player.getCapability(SoulCapability.SOUL).ifPresent(soul -> {
            if (!REWARDED.add(target)) {
                return;
            }

            int reward = ScytheCombat.reward(target.getMaxHealth());

            if (ReaperSkillManager.isActive(player)) {
                reward = ReaperSkillManager.boostSoulReward(reward);
            }

            soul.addSoul(reward, set.get().maxSoul());
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void revive(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || event.isCanceled()
                || event.getSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                || REVIVED.contains(event)
                || REVIVING.contains(player.getUUID())
                || SoulSetManager.findActiveSoulSet(player).isEmpty()) {
            return;
        }

        player.getCapability(SoulCapability.SOUL).ifPresent(soul -> {
            if (soul.getSoul() < 100) {
                return;
            }

            REVIVING.add(player.getUUID());

            try {
                event.setCanceled(true);
                player.setHealth(1.0F);

                if (player.getHealth() <= 0) {
                    event.setCanceled(false);
                    return;
                }

                if (!soul.consumeSoul(100)) {
                    event.setCanceled(false);
                    player.setHealth(0);
                    return;
                }

                REVIVED.add(event);

                player.removeAllEffects();

                player.addEffect(
                        new MobEffectInstance(
                                MobEffects.REGENERATION,
                                900,
                                1
                        )
                );

                player.addEffect(
                        new MobEffectInstance(
                                MobEffects.ABSORPTION,
                                100,
                                1
                        )
                );

                player.addEffect(
                        new MobEffectInstance(
                                MobEffects.FIRE_RESISTANCE,
                                800,
                                0
                        )
                );

                player.serverLevel().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.TOTEM_USE,
                        SoundSource.PLAYERS,
                        1.0F,
                        1.0F
                );

                player.serverLevel().sendParticles(
                        ParticleTypes.TOTEM_OF_UNDYING,
                        player.getX(),
                        player.getY() + player.getBbHeight() * 0.5D,
                        player.getZ(),
                        60,
                        0.5D,
                        0.7D,
                        0.5D,
                        0.15D
                );
            } finally {
                REVIVING.remove(player.getUUID());
            }
        });
    }
}