package com.carrot123.eternal_career.client;

import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
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
    private SoulBoxTooltipEvents() {}

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (!stack.is(SoulCoreRegistry.SOUL_BOX.get())) return;
        ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.eternal_career.soul_box.effect", ChatFormatting.GOLD, "20%");
        ItemLoreHelper.addLocalizedString(event.getToolTip(), "tooltip.eternal_career.soul_box.current", ChatFormatting.GOLD, "+" + SoulBoxItem.damagePercent(stack) + "%");
        int stage = SoulBoxItem.stage(stack);
        if (stage >= SoulBoxItem.UNLOCK_ITEMS.length) {
            event.getToolTip().add(Component.translatableWithFallback("tooltip.eternal_career.soul_box.max_stage", "已达到最高阶段").withStyle(ChatFormatting.GOLD));
            return;
        }
        Item item = ForgeRegistries.ITEMS.getValue(SoulBoxItem.UNLOCK_ITEMS[stage]);
        Component material = item == null || item == Items.AIR ? Component.literal(SoulBoxItem.UNLOCK_ITEMS[stage].toString()) : new ItemStack(item).getHoverName();
        Component name = material.copy().withStyle(ChatFormatting.GOLD);
        Component limit = Component.literal("+" + ((stage + 2) * 200) + "%").withStyle(ChatFormatting.GOLD);
        event.getToolTip().add(Component.translatableWithFallback("tooltip.eternal_career.soul_box.next_material", "下一阶段所需材料：%s（解锁后上限%s）", name, limit).withStyle(ChatFormatting.DARK_PURPLE));
    }
}
