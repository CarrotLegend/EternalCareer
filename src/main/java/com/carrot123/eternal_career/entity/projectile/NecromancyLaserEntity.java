package com.carrot123.eternal_career.entity.projectile;

import org.joml.Vector3f;

import com.Polarice3.Goety.common.entities.projectiles.SpellHurtingProjectile;
import com.carrot123.eternal_career.registry.ModEntityTypes;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public final class NecromancyLaserEntity extends SpellHurtingProjectile {
    private static final DustParticleOptions TRAIL =
            new DustParticleOptions(new Vector3f(0.18F, 0.94F, 0.28F), 0.6F);

    public NecromancyLaserEntity(
            EntityType<? extends AbstractHurtingProjectile> type,
            Level level
    ) {
        super(type, level);
    }

    public NecromancyLaserEntity(
            double x,
            double y,
            double z,
            double directionX,
            double directionY,
            double directionZ,
            Level level
    ) {
        super(ModEntityTypes.NECROMANCY_LASER.get(),
                x, y, z, directionX, directionY, directionZ, level);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        if (level().isClientSide) {
            return;
        }

        Entity target = result.getEntity();
        Entity owner = getOwner();
        float baseDamage = 23.0F;

        if (owner instanceof Mob mob
                && mob.getAttribute(Attributes.ATTACK_DAMAGE) != null
                && mob.getAttributeValue(Attributes.ATTACK_DAMAGE) > 0.0D) {
            baseDamage = (float) mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
        }

        float damage = baseDamage + getExtraDamage();
        boolean hurt = owner instanceof LivingEntity livingOwner
                ? target.hurt(target.damageSources().indirectMagic(this, livingOwner), damage)
                : target.hurt(target.damageSources().magic(), damage);

        if (hurt && target instanceof LivingEntity livingTarget) {
            double originX = owner != null ? owner.getX() : getX();
            double originZ = owner != null ? owner.getZ() : getZ();
            livingTarget.knockback(
                    1.0D,
                    originX - target.getX(),
                    originZ - target.getZ()
            );
        }
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!level().isClientSide) {
            discard();
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (tickCount >= 200) {
            discard();
            return;
        }

        if (level().isClientSide && !isRemoved()) {
            level().addParticle(TRAIL, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
