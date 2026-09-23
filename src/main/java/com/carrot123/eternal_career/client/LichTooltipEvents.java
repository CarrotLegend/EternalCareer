package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.lich.LichUtils;
import com.carrot123.eternal_career.item.PurifiedPanaceaItem;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class LichTooltipEvents {
    private LichTooltipEvents() {
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().is(ModItems.NOVICE_PURIFIED_PANACEA.get())) {
            addNoviceDescription(event);
        }
        Player player = event.getEntity();
        if (player == null) {
            player = Minecraft.getInstance().player;
        }
        if (player == null) {
            return;
        }
        if (LichUtils.isLichItem(event.getItemStack())
                && LichUtils.hasUsedPanaceaBeforeLich(player)) {
            event.getToolTip().add(Component.translatable("tooltip.eternal_career.lich.too_late")
                    .withStyle(ChatFormatting.RED));
            return;
        }
        if (LichUtils.isNonLichItem(event.getItemStack())
                && (LichUtils.isCareerLich(player) || LichUtils.isLich(player))) {
            event.getToolTip().add(Component.translatable("tooltip.eternal_career.lich.reject_panacea")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
        if (event.getItemStack().getItem() instanceof PurifiedPanaceaItem panacea
                && panacea.isTooHighFor(player)) {
            event.getToolTip().add(Component.translatable("tooltip.eternal_career.lich.not_yet")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    private static void addNoviceDescription(ItemTooltipEvent event) {
        String prefix = "tooltip.eternal_career.novice_purified_panacea.";
        event.getToolTip().add(Component.translatable(prefix + "stage")
                .withStyle(ChatFormatting.DARK_PURPLE));
        event.getToolTip().add(Component.translatable(prefix + "night_vision")
                .withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.translatable(prefix + "hunger")
                .withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.translatable(prefix + "undead")
                .withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.translatable(prefix + "effect_immunity")
                .withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.translatable(prefix + "sun_burn")
                .withStyle(ChatFormatting.DARK_RED));
        event.getToolTip().add(Component.translatable(prefix + "sun_protection")
                .withStyle(ChatFormatting.GRAY));
        event.getToolTip().add(Component.translatable(prefix + "attack_damage")
                .withStyle(ChatFormatting.DARK_RED));
        event.getToolTip().add(Component.translatable(prefix + "max_health")
                .withStyle(ChatFormatting.DARK_RED));
    }
}
