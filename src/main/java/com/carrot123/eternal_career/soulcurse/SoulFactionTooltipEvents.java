package com.carrot123.eternal_career.soulcurse;

import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SoulFactionTooltipEvents {
    @SubscribeEvent public static void onTooltip(ItemTooltipEvent event) {
        if (!SoulFaction.isSoulItem(event.getItemStack())) return;
        if (event.getItemStack().is(SoulCoreRegistry.BLOOD_HUNTER_CORE.get())) {
            ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedFormattedString(event.getToolTip(), "curios.modifiers.ring", ChatFormatting.GOLD);
            ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.eternal_career.blood_hunter_core.ranged", ChatFormatting.GOLD, "+20%");
            ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.eternal_career.blood_hunter_core.non_ranged", ChatFormatting.GOLD, "-80%");
            ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.enigmaticlegacy.void");
        }
        Player player = event.getEntity() != null ? event.getEntity() : Minecraft.getInstance().player;
        event.getToolTip().add(Component.translatable("tooltip.eternal_career.soul_only").setStyle(Style.EMPTY.withColor(SoulFaction.hasCore(player) ? 0xFFD700 : 0xFF4545)));
    }
}
