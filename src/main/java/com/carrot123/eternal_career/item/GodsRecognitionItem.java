package com.carrot123.eternal_career.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** The migrated God of Cookery's Approval charm. */
public final class GodsRecognitionItem extends Item {
    public GodsRecognitionItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.eternal_career.gods_recognition.ingredient_mark")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
