package com.carrot123.eternal_career.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class CrystalOfDrawnBowItem extends Item implements ICurioItem {
    public CrystalOfDrawnBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return context != null && "charm".equals(context.identifier())
                && !context.cosmetic();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(
                "tooltip.eternal_career.crystal_of_drawn_bow.end_power")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable(
                "tooltip.eternal_career.crystal_of_drawn_bow.bow_damage")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable(
                "tooltip.eternal_career.crystal_of_drawn_bow.draw_power")
                .withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable(
                "tooltip.eternal_career.crystal_of_drawn_bow.draw_effect")
                .withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable(
                "tooltip.eternal_career.crystal_of_drawn_bow.piercing_power")
                .withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.translatable(
                "tooltip.eternal_career.crystal_of_drawn_bow.armor_break")
                .withStyle(ChatFormatting.GOLD));
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }
}
