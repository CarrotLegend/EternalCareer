package com.carrot123.auto_jigsaw_exporter.registry;

import com.carrot123.auto_jigsaw_exporter.AutoJigsawExporter;
import com.carrot123.auto_jigsaw_exporter.block.EndMarkerBlock;
import com.carrot123.auto_jigsaw_exporter.block.StartMarkerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, AutoJigsawExporter.MOD_ID);

    public static final RegistryObject<Block> START_MARKER = BLOCKS.register(
            "start_marker",
            () -> new StartMarkerBlock(BlockBehaviour.Properties.copy(Blocks.EMERALD_BLOCK)
                    .strength(2.0F, 6.0F)));

    public static final RegistryObject<Block> END_MARKER = BLOCKS.register(
            "end_marker",
            () -> new EndMarkerBlock(BlockBehaviour.Properties.copy(Blocks.REDSTONE_BLOCK)
                    .strength(2.0F, 6.0F)));

    private ModBlocks() {
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
