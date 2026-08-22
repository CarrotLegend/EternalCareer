package com.carrot123.eternal_career;

import com.mojang.logging.LogUtils;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.registry.ModEffects;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/** Forge entry point for Eternal Career. */
@Mod(EternalCareer.MOD_ID)
public final class EternalCareer {
    public static final String MOD_ID = "eternal_career";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EternalCareer(FMLJavaModLoadingContext loadingContext) {
        IEventBus modEventBus = loadingContext.getModEventBus();
        ModAttributes.register(modEventBus);
        ModEffects.register(modEventBus);
        ModItems.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) {
        LOGGER.info("Initializing {}", MOD_ID);
    }
}
