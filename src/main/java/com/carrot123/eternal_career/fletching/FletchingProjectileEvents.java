package com.carrot123.eternal_career.fletching;

import com.carrot123.eternal_career.EternalCareer;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class FletchingProjectileEvents {

    private static final String SOURCE_BOW =
            "EternalCareerFletchingSourceBow";

    private static final String MANUAL_COMPAT =
            "EternalCareerFletchingManualCompat";

    private static final String POWER =
            "EternalCareerFletchingPower";

    private static final String VELOCITY =
            "EternalCareerFletchingVelocity";

    private static final String END =
            "EternalCareerFletchingEnd";

    private static final String BLAST =
            "EternalCareerFletchingBlast";

    private static final String BLOODTHIRST =
            "EternalCareerFletchingBloodthirst";

    private static final String BLAST_TRIGGERED =
            "EternalCareerFletchingBlastTriggered";

    private static final double BLAST_RADIUS =
            1.5D;

    private FletchingProjectileEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onProjectileJoin(
            EntityJoinLevelEvent event
    ) {
        if (event.getLevel().isClientSide()
                || event.loadedFromDisk()
                || !(event.getEntity()
                instanceof AbstractArrow arrow)
                || !(arrow.getOwner()
                instanceof ServerPlayer player)) {
            return;
        }

        ItemStack bow =
                findSourceBow(player);

        if (bow.isEmpty()) {
            return;
        }

        CompoundTag data =
                arrow.getPersistentData();

        ResourceLocation bowId =
                ForgeRegistries.ITEMS
                        .getKey(bow.getItem());

        if (bowId != null) {
            data.putString(
                    SOURCE_BOW,
                    bowId.toString()
            );
        }

        int powerLevel =
                BowModificationHelper.getLevel(
                        bow,
                        BowModifications.POWER.id()
                );

        int velocityLevel =
                BowModificationHelper.getLevel(
                        bow,
                        BowModifications.VELOCITY.id()
                );

        int endLevel =
                BowModificationHelper.getLevel(
                        bow,
                        BowModifications.END.id()
                );

        int blastLevel =
                BowModificationHelper.getLevel(
                        bow,
                        BowModifications.BLAST.id()
                );

        int bloodthirstLevel =
                BowModificationHelper.getLevel(
                        bow,
                        BowModifications.BLOODTHIRST.id()
                );

        putLevel(
                data,
                POWER,
                powerLevel
        );

        putLevel(
                data,
                VELOCITY,
                velocityLevel
        );

        putLevel(
                data,
                END,
                endLevel
        );

        putLevel(
                data,
                BLAST,
                blastLevel
        );

        putLevel(
                data,
                BLOODTHIRST,
                bloodthirstLevel
        );

        boolean manualCompat =
                FletchingBowCompat
                        .usesManualProjectileCompatibility(
                                bow
                        );

        if (manualCompat) {
            data.putBoolean(
                    MANUAL_COMPAT,
                    true
            );

            if (velocityLevel > 0) {
                double multiplier =
                        1.0D
                                + velocityLevel
                                * BowModifications.VELOCITY
                                .amountPerLevel();

                arrow.setDeltaMovement(
                        arrow.getDeltaMovement()
                                .scale(multiplier)
                );
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onProjectileHurt(
            LivingHurtEvent event
    ) {
        if (event.getEntity()
                .level()
                .isClientSide()) {
            return;
        }

        if (!(event.getSource()
                .getDirectEntity()
                instanceof AbstractArrow arrow)) {
            return;
        }

        CompoundTag data =
                arrow.getPersistentData();

        if (!data.getBoolean(
                MANUAL_COMPAT
        )) {
            return;
        }

        int powerLevel =
                Math.max(
                        0,
                        data.getInt(POWER)
                );

        int endLevel =
                Math.max(
                        0,
                        data.getInt(END)
                );

        if (powerLevel <= 0
                && endLevel <= 0) {
            return;
        }

        float original =
                event.getAmount();

        if (original <= 0.0F
                || !Float.isFinite(original)) {
            return;
        }

        double bonus =
                powerLevel
                        * BowModifications.POWER
                        .amountPerLevel()
                        + endLevel
                        * BowModifications.END
                        .amountPerLevel();

        double modified =
                original
                        * (1.0D + bonus);

        if (!Double.isFinite(modified)) {
            return;
        }

        event.setAmount(
                modified >= Float.MAX_VALUE
                        ? Float.MAX_VALUE
                        : (float) modified
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onProjectileDamage(
            LivingDamageEvent event
    ) {
        if (!(event.getEntity()
                .level()
                instanceof ServerLevel level)
                || !(event.getSource()
                .getDirectEntity()
                instanceof AbstractArrow arrow)
                || !(arrow.getOwner()
                instanceof Player shooter)
                || event.getAmount() <= 0.0F) {
            return;
        }

        CompoundTag data =
                arrow.getPersistentData();

        float directDamage =
                event.getAmount();

        int blastLevel =
                Math.max(
                        0,
                        data.getInt(BLAST)
                );

        if (blastLevel > 0
                && !data.getBoolean(
                BLAST_TRIGGERED
        )) {
            data.putBoolean(
                    BLAST_TRIGGERED,
                    true
            );

            float blastDamage =
                    (float) (
                            directDamage
                                    * BowModifications.BLAST
                                    .amountPerLevel()
                                    * blastLevel
                    );

            event.setAmount(
                    directDamage
                            + blastDamage
            );

            LivingEntity directTarget =
                    event.getEntity();

            AABB area =
                    directTarget
                            .getBoundingBox()
                            .inflate(
                                    BLAST_RADIUS
                            );

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
                        && !shooter.canHarmPlayer(
                        other
                )) {
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
                Math.max(
                        0,
                        data.getInt(
                                BLOODTHIRST
                        )
                );

        if (bloodthirstLevel <= 0) {
            return;
        }

        double actualDamage =
                Math.min(
                        event.getAmount(),
                        event.getEntity()
                                .getHealth()
                );

        if (actualDamage <= 0.0D) {
            return;
        }

        double healAmount =
                actualDamage
                        * BowModifications.BLOODTHIRST
                        .amountPerLevel()
                        * bloodthirstLevel;

        shooter.heal(
                (float) Math.min(
                        50.0D,
                        healAmount
                )
        );
    }

    private static ItemStack findSourceBow(
            ServerPlayer player
    ) {
        ItemStack using =
                player.getUseItem();

        if (isModifiedBow(using)) {
            return using;
        }

        ItemStack mainHand =
                player.getMainHandItem();

        if (isModifiedBow(mainHand)) {
            return mainHand;
        }

        ItemStack offHand =
                player.getOffhandItem();

        if (isModifiedBow(offHand)) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }

    private static boolean isModifiedBow(
            ItemStack stack
    ) {
        if (!BowModificationHelper.isBow(
                stack
        )) {
            return false;
        }

        for (BowModification modification :
                BowModifications.ALL) {

            if (BowModificationHelper.getLevel(
                    stack,
                    modification.id()
            ) > 0) {
                return true;
            }
        }

        return false;
    }

    private static void putLevel(
            CompoundTag data,
            String key,
            int level
    ) {
        if (level > 0) {
            data.putInt(
                    key,
                    level
            );
        }
    }
}