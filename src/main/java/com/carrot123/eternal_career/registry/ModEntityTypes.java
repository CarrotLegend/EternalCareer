package com.carrot123.eternal_career.registry;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.entity.SpinningScytheEntity;
import com.carrot123.eternal_career.entity.projectile.NecromancyLaserEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntityTypes {

    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(
                    ForgeRegistries.ENTITY_TYPES,
                    EternalCareer.MOD_ID
            );

    public static final RegistryObject<EntityType<NecromancyLaserEntity>>
            NECROMANCY_LASER =
            ENTITY_TYPES.register(
                    "necromancy_laser",
                    () -> EntityType.Builder
                            .<NecromancyLaserEntity>of(
                                    NecromancyLaserEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(
                                    0.5F,
                                    0.5F
                            )
                            .clientTrackingRange(
                                    16
                            )
                            .updateInterval(
                                    1
                            )
                            .build(
                                    EternalCareer.MOD_ID
                                            + ":necromancy_laser"
                            )
            );

    public static final RegistryObject<EntityType<SpinningScytheEntity>>
            SPINNING_SCYTHE =
            ENTITY_TYPES.register(
                    "spinning_scythe",
                    () -> EntityType.Builder
                            .<SpinningScytheEntity>of(
                                    SpinningScytheEntity::new,
                                    MobCategory.MISC
                            )
                            .sized(
                                    0.8F,
                                    0.8F
                            )
                            .clientTrackingRange(
                                    12
                            )
                            .updateInterval(
                                    1
                            )
                            .build(
                                    EternalCareer.MOD_ID
                                            + ":spinning_scythe"
                            )
            );

    private ModEntityTypes() {
    }

    public static void register(
            IEventBus eventBus
    ) {
        ENTITY_TYPES.register(
                eventBus
        );
    }
}