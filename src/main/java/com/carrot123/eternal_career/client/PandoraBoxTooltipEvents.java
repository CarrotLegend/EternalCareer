package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class PandoraBoxTooltipEvents {
    private PandoraBoxTooltipEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTooltipColor(
            RenderTooltipEvent.Color event
    ) {
        if (!event.getItemStack().is(
                ModItems.PANDORA_BOX.get()
        )) {
            return;
        }

        event.setBackground(
                0xF7101010
        );

        event.setBorderStart(
                0x50FF0C00
        );

        event.setBorderEnd(
                0x50FF0C00
        );
    }
}