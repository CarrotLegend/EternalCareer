package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.FoodBookCurio;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class WitchFrostEyeItem extends Item implements ICurioItem {
    public static final double BREW_DAMAGE_BONUS = 1.20D;

    public WitchFrostEyeItem(Properties properties) { super(properties); }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return context != null && FoodBookCurio.CHARM_SLOT.equals(context.identifier())
                && !context.cosmetic() && context.entity() instanceof Player;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext context, UUID slotUuid, ItemStack stack) {
        if (!canEquip(context, stack)) return ImmutableMultimap.of();
        return ImmutableMultimap.of(ModAttributes.BREW_DAMAGE.get(),
                StableAttributeModifiers.create(EternalCareer.MOD_ID + ":witch_frost_eye/brew_damage",
                        BREW_DAMAGE_BONUS, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.addLocalizedFormattedString(tooltip, "curios.modifiers.charm", ChatFormatting.GOLD);
        ItemLoreHelper.addLocalizedString(tooltip, "tooltip.eternal_career.witch_frost_eye.brew_damage");
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltip, ItemStack stack) {
        tooltip.clear();
        return tooltip;
    }
}
