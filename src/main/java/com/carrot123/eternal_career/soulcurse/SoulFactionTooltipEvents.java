package com.carrot123.eternal_career.soulcurse;

import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.carrot123.eternal_career.EternalCareer;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SoulFactionTooltipEvents {

    private SoulFactionTooltipEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (stack.is(SoulCoreRegistry.SOUL_CORE.get())) {
            List<Component> tooltip = event.getToolTip();
            Component title = tooltip.isEmpty() ? stack.getHoverName() : tooltip.get(0);
            tooltip.clear();
            tooltip.add(title);
            tooltip.add(Component.translatable("tooltip.eternal_career.soul_core.fragment_drop")
                    .withStyle(ChatFormatting.GOLD));
            return;
        }

        if (!SoulFaction.isSoulItem(stack)) {
            return;
        }

        if (stack.is(SoulCoreRegistry.BLOOD_HUNTER_CORE.get())) {
            ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.enigmaticlegacy.void");
            ItemLoreHelper.addLocalizedFormattedString(event.getToolTip(), "curios.modifiers.ring", ChatFormatting.GOLD);
            ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.eternal_career.blood_hunter_core.ranged", ChatFormatting.GOLD, "+20%");
            ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.eternal_career.blood_hunter_core.non_ranged", ChatFormatting.GOLD, "-80%");
            ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.enigmaticlegacy.void");
        }

        Player player = event.getEntity() != null ? event.getEntity() : Minecraft.getInstance().player;
        event.getToolTip().add(Component.translatable("tooltip.eternal_career.soul_only")
                .setStyle(Style.EMPTY.withColor(SoulFaction.hasCore(player) ? 0xFFD700 : 0xFF4545)));
    }
}
