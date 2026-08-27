package com.carrot123.eternal_career.damage;

import com.carrot123.eternal_career.EternalCareer;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

public final class ModDamageTypes {

    public static final ResourceKey<DamageType> GOD_BURST =
            ResourceKey.create(
                    Registries.DAMAGE_TYPE,
                    new ResourceLocation(
                            EternalCareer.MOD_ID,
                            "god_burst"
                    )
            );

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
}
