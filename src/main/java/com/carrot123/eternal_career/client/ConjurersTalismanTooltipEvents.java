package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class ConjurersTalismanTooltipEvents {
    private static final ResourceLocation TALISMAN_ID = new ResourceLocation("irons_spellbooks", "conjurers_talisman");
    private static final ResourceLocation GREATER_TALISMAN_ID = new ResourceLocation("irons_spellbooks", "greater_conjurers_talisman");

    private ConjurersTalismanTooltipEvents() {
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(event.getItemStack().getItem());
        String key;
        if (TALISMAN_ID.equals(itemId)) {
            key = "tooltip.eternal_career.conjurers_talisman.health";
        } else if (GREATER_TALISMAN_ID.equals(itemId)) {
            key = "tooltip.eternal_career.greater_conjurers_talisman.health";
        } else {
            return;
        }
        event.getToolTip().add(Component.translatable(key).withStyle(ChatFormatting.GOLD));
    }
}
