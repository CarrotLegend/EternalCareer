package com.carrot123.eternal_career.damage;

import com.carrot123.eternal_career.EternalCareer;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

public final class ModDamageTypes {

    public static final ResourceKey<DamageType> GOD_BURST =
            ResourceKey.create(
                    Registries.DAMAGE_TYPE,
                    new ResourceLocation(
                            EternalCareer.MOD_ID,
                            "god_burst"
                    )
            );

    public static final ResourceKey<DamageType> WITCH_CRITICAL =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    new ResourceLocation(EternalCareer.MOD_ID, "witch_critical"));

    public static final ResourceKey<DamageType> WITCH_DOMAIN =
            ResourceKey.create(Registries.DAMAGE_TYPE,
                    new ResourceLocation(EternalCareer.MOD_ID, "witch_domain"));

    private ModDamageTypes() {
    }

    public static DamageSource godBurst(
            LivingEntity target
    ) {
        Holder.Reference<DamageType> type =
                target.level()
                        .registryAccess()
                        .registryOrThrow(
                                Registries.DAMAGE_TYPE
                        )
                        .getHolderOrThrow(
                                GOD_BURST
                        );

        return new DamageSource(type);
    }

    public static DamageSource witchCritical(LivingEntity target, Entity direct, Player player) {
        Holder.Reference<DamageType> type = target.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(WITCH_CRITICAL);
        return player == null ? new DamageSource(type, direct) : new DamageSource(type, direct, player);
    }

    public static DamageSource witchDomain(LivingEntity target, Player player) {
        Holder.Reference<DamageType> type = target.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(WITCH_DOMAIN);
        return player == null ? new DamageSource(type) : new DamageSource(type, player);
    }
}
