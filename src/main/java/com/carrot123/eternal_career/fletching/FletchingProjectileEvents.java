package com.carrot123.eternal_career.fletching;

import com.carrot123.eternal_career.EternalCareer;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class FletchingProjectileEvents {

    private static final String ARROW_BLAST =
            "EternalCareerFletchingBlast";

    private static final String ARROW_BLOODTHIRST =
            "EternalCareerFletchingBloodthirst";

    private static final String BLAST_TRIGGERED =
            "EternalCareerFletchingBlastTriggered";

    private static final String PENDING_BLAST =
            "EternalCareerPendingFletchingBlast";

    private static final String PENDING_BLOODTHIRST =
            "EternalCareerPendingFletchingBloodthirst";

    private static final String PENDING_TICK =
            "EternalCareerPendingFletchingTick";

    private static final double BLAST_RADIUS = 1.5D;

    private FletchingProjectileEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onArrowLoose(
            ArrowLooseEvent event
    ) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || event.getLevel().isClientSide()) {
            return;
        }

        CompoundTag data =
                player.getPersistentData();

        clearPending(data);

        if (!BowModificationHelper.isBow(event.getBow())) {
            return;
        }

        int blastLevel =
                BowModificationHelper.getLevel(
                        event.getBow(),
                        BowModifications.BLAST.id()
                );

        int bloodthirstLevel =
                BowModificationHelper.getLevel(
                        event.getBow(),
                        BowModifications.BLOODTHIRST.id()
                );

        if (blastLevel <= 0
                && bloodthirstLevel <= 0) {
            return;
        }

        if (blastLevel > 0) {
            data.putInt(
                    PENDING_BLAST,
                    blastLevel
            );
        }

        if (bloodthirstLevel > 0) {
            data.putInt(
                    PENDING_BLOODTHIRST,
                    bloodthirstLevel
            );
        }

        data.putLong(
                PENDING_TICK,
                player.level().getGameTime()
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onArrowJoin(
            EntityJoinLevelEvent event
    ) {
        if (event.getLevel().isClientSide()
                || event.loadedFromDisk()
                || !(event.getEntity() instanceof AbstractArrow arrow)
                || !(arrow.getOwner() instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag playerData =
                player.getPersistentData();

        if (!playerData.contains(PENDING_TICK)) {
            return;
        }

        long currentTick =
                event.getLevel().getGameTime();

        long pendingTick =
                playerData.getLong(PENDING_TICK);

        if (currentTick != pendingTick) {
            return;
        }

        CompoundTag arrowData =
                arrow.getPersistentData();

        int blastLevel =
                playerData.getInt(PENDING_BLAST);

        int bloodthirstLevel =
                playerData.getInt(PENDING_BLOODTHIRST);

        if (blastLevel > 0) {
            arrowData.putInt(
                    ARROW_BLAST,
                    blastLevel
            );
        }

        if (bloodthirstLevel > 0) {
            arrowData.putInt(
                    ARROW_BLOODTHIRST,
                    bloodthirstLevel
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(
            TickEvent.PlayerTickEvent event
    ) {
        if (event.phase != TickEvent.Phase.END
                || !(event.player instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag data =
                player.getPersistentData();

        if (!data.contains(PENDING_TICK)) {
            return;
        }

        long currentTick =
                player.level().getGameTime();

        long pendingTick =
                data.getLong(PENDING_TICK);

        if (currentTick >= pendingTick) {
            clearPending(data);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onArrowDamage(
            LivingDamageEvent event
    ) {
        if (!(event.getEntity().level()
                instanceof ServerLevel level)
                || !(event.getSource().getDirectEntity()
                instanceof AbstractArrow arrow)
                || !(arrow.getOwner() instanceof Player shooter)
                || event.getSource().getEntity() != shooter
                || event.getAmount() <= 0.0F) {
            return;
        }

        CompoundTag arrowData =
                arrow.getPersistentData();

        float directDamage =
                event.getAmount();

        int blastLevel =
                arrowData.getInt(ARROW_BLAST);

        if (blastLevel > 0
                && !arrowData.getBoolean(BLAST_TRIGGERED)) {

            arrowData.putBoolean(
                    BLAST_TRIGGERED,
                    true
            );

            float blastDamage =
                    (float) (
                            directDamage
                                    * BowModifications.BLAST
                                    .amountPerLevel()
                    );

            event.setAmount(
                    directDamage + blastDamage
            );

            LivingEntity directTarget =
                    event.getEntity();

            AABB area =
                    directTarget
                            .getBoundingBox()
                            .inflate(BLAST_RADIUS);

            for (LivingEntity nearby :
                    level.getEntitiesOfClass(
                            LivingEntity.class,
                            area,
                            entity ->
                                    entity != directTarget
                                            && entity != shooter
                                            && entity.isAlive()
                                            && entity.distanceToSqr(
                                                    directTarget
                                            )
                                            <= BLAST_RADIUS
                                            * BLAST_RADIUS
                    )) {

                if (nearby instanceof Player other
                        && !shooter.canHarmPlayer(other)) {
                    continue;
                }

                nearby.hurt(
                        shooter
                                .damageSources()
                                .explosion(
                                        shooter,
                                        shooter
                                ),
                        blastDamage
                );
            }

            level.sendParticles(
                    ParticleTypes.EXPLOSION,
                    directTarget.getX(),
                    directTarget.getY()
                            + directTarget.getBbHeight()
                            * 0.5D,
                    directTarget.getZ(),
                    1,
                    0.0D,
                    0.0D,
                    0.0D,
                    0.0D
            );

            level.playSound(
                    null,
                    directTarget.blockPosition(),
                    SoundEvents.GENERIC_EXPLODE,
                    SoundSource.PLAYERS,
                    0.35F,
                    1.5F
            );
        }

        int bloodthirstLevel =
                arrowData.getInt(
                        ARROW_BLOODTHIRST
                );

        if (bloodthirstLevel > 0) {
            double actualDamage =
                    Math.min(
                            event.getAmount(),
                            event.getEntity().getHealth()
                    );

            if (actualDamage > 0.0D) {
                double healAmount =
                        actualDamage
                                * BowModifications
                                .BLOODTHIRST
                                .amountPerLevel()
                                * bloodthirstLevel;

                shooter.heal(
                        (float) Math.min(
                                50.0D,
                                healAmount
                        )
                );
            }
        }
    }

    private static void clearPending(
            CompoundTag data
    ) {
        data.remove(PENDING_BLAST);
        data.remove(PENDING_BLOODTHIRST);
        data.remove(PENDING_TICK);
    }
}