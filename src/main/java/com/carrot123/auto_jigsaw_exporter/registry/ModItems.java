package com.carrot123.auto_jigsaw_exporter.registry;

import com.carrot123.auto_jigsaw_exporter.AutoJigsawExporter;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AutoJigsawExporter.MOD_ID);

    public static final RegistryObject<Item> START_MARKER_ITEM = ITEMS.register(
            "start_marker",
            () -> new BlockItem(ModBlocks.START_MARKER.get(), new Item.Properties()));

    public static final RegistryObject<Item> END_MARKER_ITEM = ITEMS.register(
            "end_marker",
            () -> new BlockItem(ModBlocks.END_MARKER.get(), new Item.Properties()));

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
