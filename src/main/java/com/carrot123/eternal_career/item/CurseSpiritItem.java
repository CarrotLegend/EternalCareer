package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class CurseSpiritItem extends Item implements ICursed {

    private final String tooltipKey;

    public CurseSpiritItem(
            Properties properties,
            String tooltipKey
    ) {
        super(properties);
        this.tooltipKey = tooltipKey;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(
                Component.translatable(
                                tooltipKey
                        )
                        .withStyle(
                                ChatFormatting.GRAY
                        )
        );
    }
}