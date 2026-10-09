package com.carrot123.eternal_career.bloodbow;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.fletching.BowModification;
import com.carrot123.eternal_career.fletching.BowModificationItem;
import com.carrot123.eternal_career.fletching.BowModifications;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class BloodBowTooltips {
    private BloodBowTooltips() {}
    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!(event.getItemStack().getItem() instanceof BowModificationItem item)) return;
        BowModification mod = item.modification();
        if (mod == null || !List.of(BowModifications.BLOOD_HUNT, BowModifications.FANG, BowModifications.FANG_II, BowModifications.FANG_III, BowModifications.FANG_IV).contains(mod)) return;
        event.getToolTip().add(Component.translatable(mod.effectKey(), mod.fixedEffect() ? 0 : (int) Math.round(mod.amountPerLevel() * 100)).withStyle(ChatFormatting.LIGHT_PURPLE));
        event.getToolTip().add(Component.translatable("tooltip.eternal_career.blood_bow.once").withStyle(ChatFormatting.GOLD));
    }
}
