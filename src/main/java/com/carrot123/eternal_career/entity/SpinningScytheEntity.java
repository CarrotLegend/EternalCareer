package com.carrot123.eternal_career.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.carrot123.eternal_career.registry.ModEntityTypes;
import com.carrot123.eternal_career.soul.SpinningScytheFlightData;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public final class SpinningScytheEntity
        extends ThrowableItemProjectile {

    public static final float THROW_SPEED = 1.75F;

    private static final double RETURN_MIN_SPEED = 0.5D;
    private static final double RETURN_MAX_SPEED = 2.0D;
    private static final double RETURN_ACCELERATION = 3.0D;
    private static final double RETURN_LERP = 0.1D;

    private static final int INITIAL_RETURN_TIMER =
            8;

    private static final int MAX_LIFETIME =
            20 * 15;

    private static final double RETURN_DISTANCE =
            2.5D;

    private static final double MAX_DISTANCE =
            128.0D;

    private final Set<UUID> hitEntities =
            new HashSet<>();

    private UUID flightId;

    private float baseDamage;

    private int returnTimer =
            INITIAL_RETURN_TIMER;

    private boolean returning;

    public SpinningScytheEntity(
            EntityType<? extends SpinningScytheEntity> entityType,
            Level level
    ) {
        super(
                entityType,
                level
        );

        this.noPhysics =
                true;
    }

    public SpinningScytheEntity(
            Level level,
            LivingEntity owner
    ) {
        super(
                ModEntityTypes.SPINNING_SCYTHE.get(),
                owner,
                level
        );

        this.noPhysics =
                true;
    }

    public void initialize(
            ServerPlayer player,
            ItemStack scythe,
            UUID flightId
    ) {
        this.flightId =
                flightId;

        this.baseDamage =
                (float) player.getAttributeValue(
                        Attributes.ATTACK_DAMAGE
                );

        this.returnTimer =
                INITIAL_RETURN_TIMER;

        this.returning =
                false;

        setOwner(
                player
        );

        setItem(
                scythe.copy()
        );
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            return;
        }

        Entity owner =
                getOwner();

        if (!(owner instanceof ServerPlayer player)) {
            if (tickCount >= MAX_LIFETIME) {
                discard();
            }

            return;
        }

        if (!SpinningScytheFlightData.matches(
                player,
                flightId
        )) {
            discard();
            return;
        }

        if (!player.isAlive()) {
            returnScythe(
                    player
            );

            return;
        }

        if (player.level() != level()) {
            returnScythe(
                    player
            );

            return;
        }

        if (distanceTo(player)
                > MAX_DISTANCE) {
            returning =
                    true;
        }

        if (!returning) {
            returnTimer--;

            if (returnTimer <= 0) {
                returning =
                        true;
            }
        }

        if (returning) {
            flyBack(
                    player
            );
        }

        if (tickCount >= MAX_LIFETIME) {
            returnScythe(
                    player
            );

            return;
        }

        updateRotation();
    }

    @Override
    protected boolean canHitEntity(
            Entity target
    ) {
        return target != getOwner()
                && super.canHitEntity(
                        target
                );
    }

    @Override
    protected void onHitEntity(
            EntityHitResult result
    ) {
        if (level().isClientSide) {
            return;
        }

        if (!(getOwner()
                instanceof ServerPlayer player)) {
            return;
        }

        Entity target =
                result.getEntity();

        if (target == player
                || !target.canBeHitByProjectile()) {
            return;
        }

        if (!hitEntities.add(
                target.getUUID()
        )) {
            return;
        }

        ItemStack scythe =
                getItem();

        if (scythe.isEmpty()) {
            returnScythe(
                    player
            );

            return;
        }

        float damage =
                baseDamage;

        if (target instanceof LivingEntity living) {
            MobType mobType =
                    living.getMobType();

            damage +=
                    EnchantmentHelper
                            .getDamageBonus(
                                    scythe,
                                    mobType
                            );
        }

        ItemStack previousMainHand =
                player.getMainHandItem()
                        .copy();

        player.setItemInHand(
                InteractionHand.MAIN_HAND,
                scythe
        );

        target.invulnerableTime =
                0;

        boolean success;

        try {
            success =
                    target.hurt(
                            player.damageSources()
                                    .playerAttack(player),
                            damage
                    );
        } finally {
            player.setItemInHand(
                    InteractionHand.MAIN_HAND,
                    previousMainHand
            );
        }

        if (!success) {
            return;
        }

        if (target instanceof LivingEntity living) {
            int fireAspect =
                    EnchantmentHelper
                            .getItemEnchantmentLevel(
                                    Enchantments.FIRE_ASPECT,
                                    scythe
                            );

            if (fireAspect > 0) {
                living.setSecondsOnFire(
                        fireAspect * 4
                );
            }

            int knockback =
                    EnchantmentHelper
                            .getItemEnchantmentLevel(
                                    Enchantments.KNOCKBACK,
                                    scythe
                            );

            if (knockback > 0) {
                Vec3 motion =
                        getDeltaMovement();

                living.knockback(
                        knockback * 0.5D,
                        -motion.x,
                        -motion.z
                );
            }
        }

        scythe.hurtAndBreak(
                1,
                player,
                entity -> {
                }
        );

        setItem(
                scythe
        );

        SpinningScytheFlightData.updateItem(
                player,
                flightId,
                scythe
        );

        if (scythe.isEmpty()) {
            SpinningScytheFlightData.complete(
                    player,
                    flightId,
                    ItemStack.EMPTY
            );

            discard();
            return;
        }

        returnTimer +=
                2;

        level().playSound(
                null,
                target.getX(),
                target.getY(),
                target.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );
    }

    @Override
    protected void onHitBlock(
            BlockHitResult result
    ) {
        super.onHitBlock(
                result
        );

        returning =
                true;

        returnTimer =
                0;

        if (!(getOwner()
                instanceof ServerPlayer player)) {
            return;
        }

        Vec3 destination =
                player.position()
                        .add(
                                0.0D,
                                player.getBbHeight()
                                        * 0.5D,
                                0.0D
                        );

        Vec3 difference =
                destination.subtract(
                        position()
                );

        if (difference.lengthSqr()
                <= 1.0E-8D) {
            return;
        }

        setDeltaMovement(
                difference.normalize()
                        .scale(
                                THROW_SPEED
                        )
        );

        hasImpulse =
                true;
    }

    private void flyBack(
            ServerPlayer player
    ) {
        Vec3 destination =
                player.position()
                        .add(
                                0.0D,
                                player.getBbHeight()
                                        * 0.55D,
                                0.0D
                        );

        Vec3 difference =
                destination.subtract(
                        position()
                );

        double distance =
                difference.length();

        if (distance <= RETURN_DISTANCE) {
            returnScythe(
                    player
            );

            return;
        }

        if (distance
                <= 1.0E-8D) {
            return;
        }

        Vec3 current =
                getDeltaMovement();

        double speed =
                Mth.clamp(
                        current.length()
                                * RETURN_ACCELERATION,
                        RETURN_MIN_SPEED,
                        RETURN_MAX_SPEED
                );

        Vec3 desired =
                difference.normalize()
                        .scale(
                                speed
                        );

        setDeltaMovement(
                new Vec3(
                        Mth.lerp(
                                RETURN_LERP,
                                current.x,
                                desired.x
                        ),
                        Mth.lerp(
                                RETURN_LERP,
                                current.y,
                                desired.y
                        ),
                        Mth.lerp(
                                RETURN_LERP,
                                current.z,
                                desired.z
                        )
                )
        );

        hasImpulse =
                true;
    }

    private void returnScythe(
            ServerPlayer player
    ) {
        SpinningScytheFlightData.complete(
                player,
                flightId,
                getItem()
        );

        level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ITEM_PICKUP,
                SoundSource.PLAYERS,
                0.8F,
                1.35F
        );

        discard();
    }

    public void shootFromRotation(
            Entity shooter,
            float pitch,
            float yaw,
            float pitchOffset,
            float velocity,
            float inaccuracy
    ) {
        float x =
                -Mth.sin(
                        yaw * ((float) Math.PI / 180.0F)
                ) * Mth.cos(
                        pitch * ((float) Math.PI / 180.0F)
                );

        float y =
                -Mth.sin(
                        (pitch + pitchOffset)
                                * ((float) Math.PI / 180.0F)
                );

        float z =
                Mth.cos(
                        yaw * ((float) Math.PI / 180.0F)
                ) * Mth.cos(
                        pitch * ((float) Math.PI / 180.0F)
                );

        shoot(
                x,
                y,
                z,
                velocity,
                inaccuracy
        );

        Vec3 shooterMovement =
                shooter.getDeltaMovement();

        setDeltaMovement(
                getDeltaMovement()
                        .add(
                                shooterMovement.x,
                                0.0D,
                                shooterMovement.z
                        )
        );
    }

    @Override
    protected Item getDefaultItem() {
        return Items.IRON_HOE;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public float getPickRadius() {
        return 2.0F;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void addAdditionalSaveData(
            CompoundTag tag
    ) {
        super.addAdditionalSaveData(
                tag
        );

        if (flightId != null) {
            tag.putUUID(
                    "FlightId",
                    flightId
            );
        }

        tag.putFloat(
                "BaseDamage",
                baseDamage
        );

        tag.putInt(
                "ReturnTimer",
                returnTimer
        );

        tag.putBoolean(
                "Returning",
                returning
        );
    }

    @Override
    public void readAdditionalSaveData(
            CompoundTag tag
    ) {
        super.readAdditionalSaveData(
                tag
        );

        if (tag.hasUUID(
                "FlightId"
        )) {
            flightId =
                    tag.getUUID(
                            "FlightId"
                    );
        }

        baseDamage =
                tag.getFloat(
                        "BaseDamage"
                );

        returnTimer =
                tag.getInt(
                        "ReturnTimer"
                );

        returning =
                tag.getBoolean(
                        "Returning"
                );
    }
}