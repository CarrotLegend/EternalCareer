package com.carrot123.eternal_career;

import org.slf4j.Logger;

import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.registry.ModEffects;
import com.carrot123.eternal_career.registry.ModItems;
import com.carrot123.eternal_career.registry.ModMenus;
import com.mojang.logging.LogUtils;

import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EternalCareer.MOD_ID)
public final class EternalCareer {

    public static final String MOD_ID = "eternal_career";

    private static final Logger LOGGER =
            LogUtils.getLogger();

    public EternalCareer(
            FMLJavaModLoadingContext loadingContext
    ) {
        IEventBus modEventBus =
                loadingContext.getModEventBus();

        com.carrot123.eternal_career.network.ModNetwork
                .register();
        modEventBus.addListener(
                this::registerCapabilities
        );

        ModAttributes.register(
                modEventBus
        );

        ModEffects.register(
                modEventBus
        );

        ModItems.register(
                modEventBus
        );

        ModMenus.register(
                modEventBus
        );

        modEventBus.addListener(
                this::onCommonSetup
        );
    }

    private void registerCapabilities(
            RegisterCapabilitiesEvent event
    ) {
        event.register(
                com.carrot123.eternal_career
                        .career.capability.soul.ISoul.class
        );

        event.register(
                com.carrot123.eternal_career
                        .soulblessing.SoulBlessingInventory.class
        );
    }

    private void onCommonSetup(
            FMLCommonSetupEvent event
    ) {
        event.enqueueWork(
                ModEffects::bindChaoticCookingAttributes
        );

        LOGGER.info(
                "Initializing {}",
                MOD_ID
        );
    }
}