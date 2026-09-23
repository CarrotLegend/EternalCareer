package com.carrot123.eternal_career.client;

import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.SoulTankItem;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class SoulTankTooltipEvents {

    private SoulTankTooltipEvents() {
    }

    @SubscribeEvent
    public static void onTooltip(
            ItemTooltipEvent event
    ) {
        if (!event.getItemStack().is(
                ModItems.SOUL_TANK.get()
        )) {
            return;
        }

        ItemLoreHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.enigmaticlegacy.void"
        );

        ItemLoreHelper.addLocalizedFormattedString(
                event.getToolTip(),
                "curios.modifiers.charm",
                ChatFormatting.GOLD
        );

        ItemLoreHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.soul_tank.max_health",
                ChatFormatting.GOLD,
                "200"
        );

        ItemLoreHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.soul_tank.armor",
                ChatFormatting.GOLD,
                "4"
        );

        ItemLoreHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.enigmaticlegacy.void"
        );

        ItemLoreHelper.addLocalizedString(
                event.getToolTip(),
                "tooltip.eternal_career.soul_tank.description"
        );

        if (Screen.hasShiftDown()) {
            int bonus =
                    SoulTankItem
                            .getScytheDamageBonusPercent(
                                    SoulHudOverlay.currentSoul()
                            );

            ItemLoreHelper.addLocalizedString(
                    event.getToolTip(),
                    "tooltip.eternal_career.soul_tank.scythe_damage",
                    ChatFormatting.GOLD,
                    bonus + "%"
            );
        } else {
            ItemLoreHelper.addLocalizedString(
                    event.getToolTip(),
                    "tooltip.eternal_career.soul_tank.hold_shift"
            );
        }
    }
}