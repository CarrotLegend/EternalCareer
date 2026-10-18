package com.carrot123.eternal_career.client;

import com.Polarice3.Goety.common.items.ModItems;
import com.carrot123.eternal_career.EternalCareer;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class CroneHatTooltipEvents {
    private CroneHatTooltipEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTooltip(ItemTooltipEvent event) {
        if (!event.getItemStack().is(ModItems.CRONE_HAT.get())) {
            return;
        }

        List<Component> tooltip = event.getToolTip();
        for (int index = 0; index < tooltip.size(); index++) {
            Component original = tooltip.get(index);
            if (original.getContents() instanceof TranslatableContents translated
                    && "info.goety.crone_hat_potion".equals(translated.getKey())) {
                tooltip.set(index, Component.translatable("tooltip.eternal_career.crone_hat_potion")
                        .withStyle(original.getStyle()));
                return;
            }
        }
    }
}
