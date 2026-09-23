package com.carrot123.eternal_career.item;

import com.carrot123.eternal_career.util.ChaoticCookingHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class ChaosFoodItem extends Item {
    private final int amplifier;
    private final String tooltipPath;

    public ChaosFoodItem(Properties properties, int amplifier, String tooltipPath) {
        super(properties);
        this.amplifier = amplifier;
        this.tooltipPath = tooltipPath;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 5;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && entity instanceof Player player) {
            ChaoticCookingHelper.extendEffect(player, amplifier);
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(
                "tooltip.eternal_career." + tooltipPath + ".effect")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable(
                "tooltip.eternal_career." + tooltipPath + ".extend")
                .withStyle(ChatFormatting.DARK_PURPLE));
        tooltip.add(Component.translatable("tooltip.eternal_career.chaos_food.fast_eating")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
