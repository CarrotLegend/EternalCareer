package com.carrot123.auto_jigsaw_exporter.registry;

import com.carrot123.auto_jigsaw_exporter.AutoJigsawExporter;
import com.carrot123.auto_jigsaw_exporter.blockentity.StartMarkerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AutoJigsawExporter.MOD_ID);

    public static final RegistryObject<BlockEntityType<StartMarkerBlockEntity>> START_MARKER =
            BLOCK_ENTITIES.register(
                    "start_marker",
                    () -> BlockEntityType.Builder.of(
                            StartMarkerBlockEntity::new,
                            ModBlocks.START_MARKER.get()
                    ).build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
