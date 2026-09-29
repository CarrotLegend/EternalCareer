package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.fletching.BowModification;
import com.carrot123.eternal_career.fletching.BowModificationHelper;
import com.carrot123.eternal_career.fletching.BowModifications;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import java.util.List;
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
        boolean modified = BowModifications.ALL.stream().anyMatch(modification ->
                BowModificationHelper.getLevel(event.getItemStack(), modification.id()) > 0);
        if (!modified) {
            return;
        }
        hideLegacyMainhandModifiers(event.getToolTip());
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
            event.getToolTip().add(Component.translatable("fletching.eternal_career.tooltip.entry",
                    Component.translatable(modification.nameKey()), numeral(level),
                    modification.effect(level))
                    .withStyle(ChatFormatting.GREEN));
        }
    }

    private static void hideLegacyMainhandModifiers(List<Component> lines) {
        for (int index = 0; index < lines.size(); index++) {
            if (!(lines.get(index).getContents() instanceof TranslatableContents contents)
                    || !contents.getKey().equals("item.modifiers.mainhand")) {
                continue;
            }
            if (index > 0 && lines.get(index - 1).getString().isEmpty()) {
                index--;
            }
            lines.remove(index);
            while (index < lines.size()
                    && lines.get(index).getContents() instanceof TranslatableContents attribute
                    && attribute.getKey().startsWith("attribute.modifier.")) {
                lines.remove(index);
            }
            break;
        }
    }
}
