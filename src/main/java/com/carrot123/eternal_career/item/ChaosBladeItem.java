package com.carrot123.eternal_career.item;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.util.CurioTooltipHelper;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class ChaosBladeItem extends Item implements ICurioItem {
    private static final UUID KITCHENWARE_ID = stableId("kitchenware_damage");
    private static final UUID LUCK_ID = stableId("luck");

    public ChaosBladeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return functionalCharm(context);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext context, UUID slotUuid, ItemStack stack) {
        if (!functionalCharm(context) || !(context.entity() instanceof Player)) {
            return ImmutableMultimap.of();
        }
        return ImmutableMultimap.of(
                ModAttributes.KITCHENWARE_DAMAGE.get(),
                new AttributeModifier(KITCHENWARE_ID,
                        EternalCareer.MOD_ID + ":chaos_blade/kitchenware_damage",
                        0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
                Attributes.LUCK,
                new AttributeModifier(LUCK_ID,
                        EternalCareer.MOD_ID + ":chaos_blade/luck",
                        5.0D, AttributeModifier.Operation.ADDITION));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        CurioTooltipHelper.addBlank(tooltip);
        tooltip.add(Component.translatable("curios.modifiers.charm")
                .withStyle(ChatFormatting.GOLD));
        CurioTooltipHelper.addLocalizedString(tooltip,
                "tooltip.eternal_career.chaos_blade.kitchenware_damage",
                ChatFormatting.GOLD, "10%");
        CurioTooltipHelper.addLocalizedString(tooltip,
                "tooltip.eternal_career.chaos_blade.luck",
                ChatFormatting.GOLD, "5");
        CurioTooltipHelper.addLocalizedString(tooltip,
                "tooltip.eternal_career.chaos_blade.eating_speed",
                ChatFormatting.GOLD, "50%");
        CurioTooltipHelper.addBlank(tooltip);
        CurioTooltipHelper.addLocalizedString(tooltip,
                "tooltip.eternal_career.chaos_blade.mob_drop");
        CurioTooltipHelper.addLocalizedString(tooltip,
                "tooltip.eternal_career.chaos_blade.boss_drop");
        CurioTooltipHelper.addLocalizedString(tooltip,
                "tooltip.eternal_career.chaos_blade.looting");
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        return CurioTooltipHelper.clearCuriosAttributes(tooltips);
    }

    private static boolean functionalCharm(SlotContext context) {
        return context != null && "charm".equals(context.identifier())
                && !context.cosmetic();
    }

    private static UUID stableId(String path) {
        return UUID.nameUUIDFromBytes((EternalCareer.MOD_ID + ":chaos_blade/" + path)
                .getBytes(StandardCharsets.UTF_8));
    }
}
