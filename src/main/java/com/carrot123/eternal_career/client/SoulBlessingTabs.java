package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import dev.xkmc.l2tabs.tabs.core.TabManager;
import dev.xkmc.l2tabs.tabs.core.TabRegistry;
import dev.xkmc.l2tabs.tabs.core.TabToken;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class SoulBlessingTabs {

    static final ResourceLocation BLESSING_ICON = new ResourceLocation(
            EternalCareer.MOD_ID,
            "textures/gui/soul_blessing_tab.png"
    );

    private static TabToken<TabSoulBlessing> soulBlessingTab;

    private SoulBlessingTabs() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(SoulBlessingTabs::register);
    }

    public static void register() {
        if (soulBlessingTab != null) {
            return;
        }

        soulBlessingTab = TabRegistry.registerTab(
                4000,
                TabSoulBlessing::new,
                () -> Items.AIR,
                Component.translatable(
                        "soul_blessing.eternal_career.title"
                )
        );
    }

    public static void addToBlessing(
            SoulBlessingScreen screen
    ) {
        if (soulBlessingTab == null) {
            register();
        }

        new TabManager(screen).init(
                screen::addTab,
                soulBlessingTab
        );
    }
}