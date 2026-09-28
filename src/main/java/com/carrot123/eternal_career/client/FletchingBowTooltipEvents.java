package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.fletching.BowModification;
import com.carrot123.eternal_career.fletching.BowModificationHelper;
import com.carrot123.eternal_career.fletching.BowModifications;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, value = Dist.CLIENT)
public final class FletchingBowTooltipEvents {
    private static final String[] NUMERALS = {"", "I", "II", "III", "IV", "V"};

    private FletchingBowTooltipEvents() {
    }

    public static String numeral(int level) {
        return NUMERALS[Math.max(0, Math.min(NUMERALS.length - 1, level))];
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        if (!BowModificationHelper.isBow(event.getItemStack())) {
            return;
        }
        boolean heading = false;
        for (BowModification modification : BowModifications.ALL) {
            int level = BowModificationHelper.getLevel(event.getItemStack(), modification.id());
            if (level <= 0) {
                continue;
            }
            if (!heading) {
                event.getToolTip().add(Component.translatable("fletching.eternal_career.modified")
                        .withStyle(ChatFormatting.GOLD));
                heading = true;
            }
            int percent = (int) Math.round(100.0D * modification.amountPerLevel() * level);
            event.getToolTip().add(Component.translatable("fletching.eternal_career.tooltip.entry",
                    Component.translatable(modification.nameKey()), numeral(level),
                    Component.translatable(modification.effectKey(), percent))
                    .withStyle(ChatFormatting.GREEN));
        }
    }
}
