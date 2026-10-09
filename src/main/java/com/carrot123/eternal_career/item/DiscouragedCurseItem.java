package com.carrot123.eternal_career.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class DiscouragedCurseItem
        extends LockedCurseItem {

    private static final String REVERSED_TAG =
            "EternalCareerDiscouragedCurseReversed";

    public DiscouragedCurseItem(
            Properties properties
    ) {
        super(properties);
    }

    public static boolean isReversed(
            ItemStack stack
    ) {
        return stack.hasTag()
                && stack.getTag() != null
                && stack.getTag()
                .getBoolean(
                        REVERSED_TAG
                );
    }

    public static void reverse(
            ItemStack stack
    ) {
        if (!stack.isEmpty()) {
            stack.getOrCreateTag()
                    .putBoolean(
                            REVERSED_TAG,
                            true
                    );
        }
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
                                isReversed(stack)
                                        ? "tooltip.eternal_career.discouraged_curse.reversed"
                                        : "tooltip.eternal_career.discouraged_curse"
                        )
                        .withStyle(
                                isReversed(stack)
                                        ? ChatFormatting.GOLD
                                        : ChatFormatting.RED
                        )
        );
    }
}