package com.carrot123.eternal_career.soulcurse;

import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SoulScarTooltipEvents {
    private SoulScarTooltipEvents() {}
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (!event.getItemStack().is(SoulScarRegistry.SOUL_GAUNTLET.get())) return;
        ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.addLocalizedFormattedString(event.getToolTip(), "curios.modifiers.hands", ChatFormatting.GOLD);
        event.getToolTip().add(Component.translatable("tooltip.eternal_career.soul_gauntlet.effect").withStyle(ChatFormatting.GOLD));
        double percent = SoulGauntletItem.damageTenthsPercent(event.getItemStack()) / 10.0D;
        String shown = (percent == Math.floor(percent) ? Integer.toString((int) percent) : Double.toString(percent)) + "%";
        event.getToolTip().add(Component.translatable("tooltip.eternal_career.soul_gauntlet.current",
                Component.literal("+" + shown).withStyle(ChatFormatting.LIGHT_PURPLE)).withStyle(ChatFormatting.GOLD));
        ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.enigmaticlegacy.void");
    }
}
