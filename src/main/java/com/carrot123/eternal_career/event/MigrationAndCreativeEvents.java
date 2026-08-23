package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModItems;
import com.carrot123.until_eternity.item.ModCreativeModeTabs;

import net.minecraft.core.registries.Registries;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.MissingMappingsEvent;

public final class MigrationAndCreativeEvents {

    private MigrationAndCreativeEvents() {
    }

    @Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
    )
    public static final class ForgeEvents {

        private ForgeEvents() {
        }

        @SubscribeEvent
        public static void onMissingMappings(
            MissingMappingsEvent event
        ) {
            for (MissingMappingsEvent.Mapping<net.minecraft.world.item.Item> mapping
                : event.getMappings(
                    Registries.ITEM,
                    "until_eternity"
                ))
                {
                if ("gods_recognition".equals(
                    mapping.getKey().getPath())) {
                    mapping.remap(
                        ModItems.GODS_RECOGNITION.get()
                    );
                }
            }
        }
    }

    @Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
    )
    public static final class ModEvents {

        private ModEvents() {
        }

        @SubscribeEvent
        public static void onCreativeTab(
            BuildCreativeModeTabContentsEvent event
        ) {
            if (!event.getTabKey().equals(
                ModCreativeModeTabs
                    .UNTIL_ETERNITY_TAB
                    .getKey())) {
                return;
            }

            event.accept(ModItems.CHEF_HAT);
            event.accept(ModItems.CHEF_JACKET);
            event.accept(ModItems.CHEF_LEGGINGS);
            event.accept(ModItems.CHEF_BOOTS);
            event.accept(ModItems.SIN_ROCK);
            event.accept(ModItems.HEAD_CHEF_SHEATH);
            event.accept(ModItems.COOKING_MAGIC_HAND);
            event.accept(ModItems.CHEF_APPRENTICE_BADGE);
            event.accept(ModItems.INTERMEDIATE_CHEF_BADGE);
            event.accept(ModItems.ADVANCED_CHEF_BADGE);
            event.accept(ModItems.SENIOR_TECHNICIAN_BADGE);
            event.accept(ModItems.MASTER_CHEF_BADGE);
            event.accept(ModItems.GODS_RECOGNITION);
        }
    }
}