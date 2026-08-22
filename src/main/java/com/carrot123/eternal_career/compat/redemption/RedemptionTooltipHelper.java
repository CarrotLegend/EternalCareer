package com.carrot123.eternal_career.compat.redemption;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Side-neutral tooltip composition kept separate for GameTest coverage. */
public final class RedemptionTooltipHelper {
    public static final String LINE_1_KEY = "tooltip.eternal_career.redemption_only.line1";
    public static final String LINE_2_KEY = "tooltip.eternal_career.redemption_only.line2";

    private RedemptionTooltipHelper() {
    }

    public static void appendRestrictionTooltip(Player player, ItemStack stack,
                                                List<Component> tooltip) {
        if (player != null && !RedemptionItemHelper.canUseRedemptionItem(player, stack)) {
            tooltip.add(Component.translatable(LINE_1_KEY).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable(LINE_2_KEY).withStyle(ChatFormatting.GOLD));
        }
    }
}
