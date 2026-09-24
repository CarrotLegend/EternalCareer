package com.carrot123.eternal_career.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class LichEssenceItem extends Item {
    private final String tooltipKey;

    public LichEssenceItem(Properties properties, String itemId) {
        super(properties);
        this.tooltipKey = "tooltip.eternal_career." + itemId + ".drop";
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(tooltipKey).withStyle(ChatFormatting.GRAY));
    }
}
