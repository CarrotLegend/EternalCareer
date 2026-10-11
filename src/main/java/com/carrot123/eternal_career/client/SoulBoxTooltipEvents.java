package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.soulcurse.SoulBoxItem;
import com.carrot123.eternal_career.soulcurse.SoulCoreRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SoulBoxTooltipEvents {

    private SoulBoxTooltipEvents() {
    }

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(SoulCoreRegistry.SOUL_BOX.get())) {
            return;
        }

        int count = SoulBoxItem.fragments(stack);
        event.getToolTip().add(
                Component.translatable(
                        "tooltip.eternal_career.soul_box.effect",
                        "+20%", "+10%", "+1%", "+10%"
                ).withStyle(ChatFormatting.GOLD)
        );

        event.getToolTip().add(
                Component.translatable(
                        "tooltip.eternal_career.soul_box.current",
                        "+" + (count * 20) + "%",
                        "+" + (count * 10) + "%",
                        "+" + Math.min(85, count) + "%",
                        "+" + (count * 10) + "%"
                ).withStyle(ChatFormatting.GOLD)
        );

        int stage = SoulBoxItem.stage(stack);
        if (stage >= SoulBoxItem.UNLOCK_ITEMS.length) {
            event.getToolTip().add(
                    Component.translatable("tooltip.eternal_career.soul_box.max_stage")
                            .withStyle(ChatFormatting.GOLD)
            );
            return;
        }

        Item item = ForgeRegistries.ITEMS.getValue(SoulBoxItem.UNLOCK_ITEMS[stage]);
        Component material = item == null || item == Items.AIR
                ? Component.translatable("tooltip.eternal_career.soul_box.unknown_material")
                : new ItemStack(item).getHoverName();
        Component limit = Component.literal("+" + ((stage + 2) * 200) + "%")
                .withStyle(ChatFormatting.GOLD);

        event.getToolTip().add(
                Component.translatable(
                        "tooltip.eternal_career.soul_box.next_material",
                        material.copy().withStyle(ChatFormatting.GOLD),
                        limit
                ).withStyle(ChatFormatting.DARK_PURPLE)
        );
    }
}
