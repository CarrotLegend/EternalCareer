package com.carrot123.auto_jigsaw_exporter;

import com.carrot123.auto_jigsaw_exporter.registry.ModBlockEntities;
import com.carrot123.auto_jigsaw_exporter.registry.ModBlocks;
import com.carrot123.auto_jigsaw_exporter.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@Mod(AutoJigsawExporter.MOD_ID)
public final class AutoJigsawExporter {
    public static final String MOD_ID = "auto_jigsaw_exporter";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AutoJigsawExporter(FMLJavaModLoadingContext context) {
        IEventBus modBus = context.getModEventBus();
        ModBlocks.register(modBus);
        ModItems.register(modBus);
        ModBlockEntities.register(modBus);
        modBus.addListener(this::addCreativeItems);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.START_MARKER_ITEM);
            event.accept(ModItems.END_MARKER_ITEM);
        }
    }
}
