package com.carrot123.eternal_career.fletching;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FletchingProjectileEvents {
    private static final String BLAST = "EternalCareerFletchingBlast";
    private static final String BLOODTHIRST = "EternalCareerFletchingBloodthirst";
    private static final String BLAST_TRIGGERED = "EternalCareerFletchingBlastTriggered";
    private static final double BLAST_RADIUS = 1.5D;

    private FletchingProjectileEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onArrowDamage(LivingDamageEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel level)
                || !(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)
                || !(arrow.getOwner() instanceof Player shooter)
                || event.getSource().getEntity() != shooter
                || event.getAmount() <= 0.0F) {
            return;
        }

        float directDamage = event.getAmount();
        if (arrow.getPersistentData().getInt(BLAST) > 0
                && !arrow.getPersistentData().getBoolean(BLAST_TRIGGERED)) {
            arrow.getPersistentData().putBoolean(BLAST_TRIGGERED, true);
            float blastDamage = (float) (directDamage * BowModifications.BLAST.amountPerLevel());
            event.setAmount(directDamage + blastDamage);
            LivingEntity directTarget = event.getEntity();
            AABB area = directTarget.getBoundingBox().inflate(BLAST_RADIUS);
            for (LivingEntity nearby : level.getEntitiesOfClass(LivingEntity.class, area,
                    entity -> entity != directTarget && entity != shooter
                            && entity.isAlive() && entity.distanceToSqr(directTarget) <= BLAST_RADIUS * BLAST_RADIUS)) {
                if (nearby instanceof Player other && !shooter.canHarmPlayer(other)) {
                    continue;
                }
                nearby.hurt(shooter.damageSources().explosion(shooter, shooter), blastDamage);
            }
            level.sendParticles(ParticleTypes.EXPLOSION, directTarget.getX(),
                    directTarget.getY() + directTarget.getBbHeight() * 0.5D,
                    directTarget.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            level.playSound(null, directTarget.blockPosition(), SoundEvents.GENERIC_EXPLODE,
                    SoundSource.PLAYERS, 0.35F, 1.5F);
        }

        if (arrow.getPersistentData().getInt(BLOODTHIRST) > 0) {
            double actualDamage = Math.min(event.getAmount(), event.getEntity().getHealth());
            if (actualDamage > 0.0D) {
                shooter.heal((float) Math.min(50.0D,
                        actualDamage * BowModifications.BLOODTHIRST.amountPerLevel()));
            }
        }
    }
}
