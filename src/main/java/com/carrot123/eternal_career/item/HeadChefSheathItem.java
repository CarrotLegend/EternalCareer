package com.carrot123.eternal_career.item;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.redemption.RedemptionItemHelper;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio.DropRule;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class HeadChefSheathItem extends Item implements ICurioItem {

    public static final String BELT_SLOT = "belt";

    public static final double KITCHENWARE_DAMAGE_BONUS = 0.10D;
    public static final double NON_KITCHENWARE_DAMAGE_PENALTY = -0.90D;

    public static final UUID KITCHENWARE_DAMAGE_MODIFIER_ID =
            stableModifierId("kitchenware_damage");

    public static final UUID NON_KITCHENWARE_DAMAGE_MODIFIER_ID =
            stableModifierId("non_kitchenware_damage");

    public HeadChefSheathItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return isFunctionalBeltSlot(slotContext)
                && slotContext.entity() instanceof Player player
                && RedemptionItemHelper.canUseRedemptionItem(player, stack);
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        if (slotContext.entity() instanceof Player player
                && SuperpositionHandler.canUnequipBoundRelics(player)) {
            return ICurioItem.super.canUnequip(slotContext, stack);
        }

        return false;
    }

    @Override
    public DropRule getDropRule(
            SlotContext slotContext,
            DamageSource source,
            int lootingLevel,
            boolean recentlyHit,
            ItemStack stack
    ) {
        return DropRule.ALWAYS_KEEP;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            UUID slotUuid,
            ItemStack stack
    ) {
        if (!isFunctionalBeltSlot(slotContext)) {
            return ImmutableMultimap.of();
        }

        return ImmutableMultimap.of(
                ModAttributes.KITCHENWARE_DAMAGE.get(),
                new AttributeModifier(
                        KITCHENWARE_DAMAGE_MODIFIER_ID,
                        EternalCareer.MOD_ID
                                + ":head_chef_sheath/kitchenware_damage",
                        KITCHENWARE_DAMAGE_BONUS,
                        AttributeModifier.Operation.MULTIPLY_BASE
                ),

                ModAttributes.NON_KITCHENWARE_DAMAGE.get(),
                new AttributeModifier(
                        NON_KITCHENWARE_DAMAGE_MODIFIER_ID,
                        EternalCareer.MOD_ID
                                + ":head_chef_sheath/non_kitchenware_damage",
                        NON_KITCHENWARE_DAMAGE_PENALTY,
                        AttributeModifier.Operation.MULTIPLY_BASE
                )
        );
    }

    private static boolean isFunctionalBeltSlot(SlotContext slotContext) {
        return slotContext != null
                && BELT_SLOT.equals(slotContext.identifier())
                && !slotContext.cosmetic();
    }

    private static UUID stableModifierId(String attributePath) {
        String key = EternalCareer.MOD_ID
                + ":head_chef_sheath/"
                + attributePath;

        return UUID.nameUUIDFromBytes(
                key.getBytes(StandardCharsets.UTF_8)
        );
    }
}