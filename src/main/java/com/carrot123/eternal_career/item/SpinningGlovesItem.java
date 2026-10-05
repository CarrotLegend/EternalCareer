package com.carrot123.eternal_career.item;

import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public final class SpinningGlovesItem extends Item implements ICurioItem {

    public static final String HANDS_SLOT = "hands";

    public SpinningGlovesItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(
            SlotContext slotContext,
            ItemStack stack
    ) {
        return slotContext != null
                && HANDS_SLOT.equals(slotContext.identifier())
                && !slotContext.cosmetic()
                && slotContext.entity() instanceof Player player
                && RedemptionAccessController.canEquip(
                        player,
                        stack
                );
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(
                Component.translatable(
                        "tooltip.eternal_career.spinning_gloves.effect"
                ).withStyle(ChatFormatting.GOLD)
        );

        super.appendHoverText(
                stack,
                level,
                tooltip,
                flag
        );
    }
}