package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.until_eternity.item.ModCreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SoulScarCreativeEvents {
    private SoulScarCreativeEvents() {}
    @SubscribeEvent public static void add(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(ModCreativeModeTabs.UNTIL_ETERNITY_TAB.getKey())) return;
        event.accept(SoulScarRegistry.SOUL_GAUNTLET);
        event.accept(SoulScarRegistry.TRANSFORMING_SCAR);
        event.accept(SoulScarRegistry.COWARD);
        event.accept(SoulScarRegistry.FRAGILE);
        event.accept(SoulScarRegistry.PESSIMIST);
    }
}
