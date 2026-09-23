package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.carrot123.eternal_career.lich.LichSpellAttributes;
import com.carrot123.eternal_career.lich.LichUtils;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class LichResearchNotesItem extends Item implements ICurioItem {
    public LichResearchNotesItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return functionalCharm(context)
                && context.entity() instanceof Player player
                && !LichUtils.hasUsedPanaceaBeforeLich(player)
                && !LichUtils.isCareerLich(player);
    }

    @Override
    public boolean canUnequip(SlotContext context, ItemStack stack) {
        if (context.entity() instanceof Player player
                && SuperpositionHandler.canUnequipBoundRelics(player)) {
            return ICurioItem.super.canUnequip(context, stack);
        }
        return false;
    }

    @Override
    public DropRule getDropRule(SlotContext context, DamageSource source,
            int lootingLevel, boolean recentlyHit, ItemStack stack) {
        return DropRule.ALWAYS_KEEP;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext context, UUID slotUuid, ItemStack stack) {
        if (!functionalCharm(context) || !(context.entity() instanceof Player player)
                || LichUtils.hasUsedPanaceaBeforeLich(player)
                || !isPrimaryNote(player, context.index())) {
            return ImmutableMultimap.of();
        }
        return LichSpellAttributes.modifiers();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level level,
            List<Component> tooltip, TooltipFlag flag) {
        ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.addLocalizedFormattedString(tooltip, "curios.modifiers.charm",
                ChatFormatting.GOLD);
        ItemLoreHelper.addLocalizedString(tooltip,
                "tooltip.eternal_career.lich_research_notes.necromancy_power",
                ChatFormatting.GOLD, "20%");
        ItemLoreHelper.addLocalizedString(tooltip,
                "tooltip.eternal_career.lich_research_notes.other_spell_power",
                ChatFormatting.GOLD, "90%");
        ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.addLocalizedString(tooltip, "tooltip.eternal_career.bound_curio");
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, ItemStack stack) {
        tooltips.clear();
        return tooltips;
    }

    private static boolean functionalCharm(SlotContext context) {
        return context != null && "charm".equals(context.identifier()) && !context.cosmetic();
    }

    private static boolean isPrimaryNote(Player player, int index) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .map(handler -> handler.findCurios(
                        com.carrot123.eternal_career.registry.ModItems.LICH_RESEARCH_NOTES.get())
                        .stream()
                        .filter(result -> "charm".equals(result.slotContext().identifier())
                                && !result.slotContext().cosmetic())
                        .mapToInt(result -> result.slotContext().index())
                        .min().orElse(index) == index)
                .orElse(true);
    }
}
