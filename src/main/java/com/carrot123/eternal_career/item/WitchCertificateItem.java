package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.FoodBookCurio;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class WitchCertificateItem extends Item implements ICurioItem {

    public WitchCertificateItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return context != null
                && !context.cosmetic()
                && FoodBookCurio.CHARM_SLOT.equals(context.identifier())
                && context.entity() instanceof Player;
    }

    @Override
    public boolean canUnequip(SlotContext context, ItemStack stack) {
        if (context != null
                && context.entity() instanceof Player player
                && SuperpositionHandler.canUnequipBoundRelics(player)) {
            return ICurioItem.super.canUnequip(context, stack);
        }
        return false;
    }

    @Override
    public ICurio.DropRule getDropRule(
            SlotContext context,
            DamageSource source,
            int lootingLevel,
            boolean recentlyHit,
            ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext context,
            UUID slotUuid,
            ItemStack stack) {
        if (!canEquip(context, stack)) {
            return ImmutableMultimap.of();
        }

        return ImmutableMultimap.of(
                ModAttributes.BREW_DAMAGE.get(),
                StableAttributeModifiers.create(
                        EternalCareer.MOD_ID + ":witch_certificate/brew_damage",
                        0.20D,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                ),
                ModAttributes.NON_BREW_DAMAGE.get(),
                StableAttributeModifiers.create(
                        EternalCareer.MOD_ID + ":witch_certificate/non_brew_damage",
                        -0.60D,
                        AttributeModifier.Operation.MULTIPLY_TOTAL
                )
        );
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Level level,
            List<Component> tooltip,
            TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        ItemLoreHelper.addLocalizedString(
                tooltip, "tooltip.enigmaticlegacy.void");

        ItemLoreHelper.addLocalizedFormattedString(
                tooltip, "curios.modifiers.charm", ChatFormatting.GOLD);

        ItemLoreHelper.addLocalizedString(
                tooltip, "tooltip.eternal_career.witch_certificate.brew_damage");

        ItemLoreHelper.addLocalizedString(
                tooltip, "tooltip.eternal_career.witch_certificate.non_brew_damage");

        ItemLoreHelper.addLocalizedString(
                tooltip, "tooltip.enigmaticlegacy.void");

        ItemLoreHelper.addLocalizedString(
                tooltip, "tooltip.eternal_career.bound_curio");
    }

    @Override
    public List<Component> getAttributesTooltip(
            List<Component> tooltip,
            ItemStack stack) {
        tooltip.clear();
        return tooltip;
    }

    @Override
    public List<Component> getSlotsTooltip(
            List<Component> tooltip,
            ItemStack stack) {
        tooltip.clear();
        return tooltip;
    }
}