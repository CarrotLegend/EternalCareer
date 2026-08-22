package com.carrot123.eternal_career.item;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.puffish.PuffishAttributesHelper;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/** A functional hand curio that improves kitchenware damage and life steal. */
public final class CookingMagicHandItem extends Item implements ICurioItem {
    public static final String HANDS_SLOT = "hands";
    public static final double KITCHENWARE_DAMAGE_BONUS = 0.20D;
    public static final double LIFE_STEAL_BONUS = 0.10D;

    public static final UUID KITCHENWARE_DAMAGE_MODIFIER_ID =
            stableModifierId("kitchenware_damage");
    public static final UUID LIFE_STEAL_MODIFIER_ID = stableModifierId("life_steal");

    public CookingMagicHandItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return isFunctionalHandsSlot(slotContext);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            UUID slotUuid,
            ItemStack stack
    ) {
        if (!isFunctionalHandsSlot(slotContext)) {
            return ImmutableMultimap.of();
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers = ImmutableMultimap.builder();
        modifiers.put(
                ModAttributes.KITCHENWARE_DAMAGE.get(),
                new AttributeModifier(
                        KITCHENWARE_DAMAGE_MODIFIER_ID,
                        EternalCareer.MOD_ID + ":cooking_magic_hand/kitchenware_damage",
                        KITCHENWARE_DAMAGE_BONUS,
                        AttributeModifier.Operation.MULTIPLY_BASE));

        Attribute lifeSteal = PuffishAttributesHelper.resolve(PuffishAttributesHelper.LIFE_STEAL);
        if (lifeSteal != null) {
            modifiers.put(
                    lifeSteal,
                    new AttributeModifier(
                            LIFE_STEAL_MODIFIER_ID,
                            EternalCareer.MOD_ID + ":cooking_magic_hand/life_steal",
                            LIFE_STEAL_BONUS,
                            AttributeModifier.Operation.MULTIPLY_BASE));
        }

        return modifiers.build();
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(Component.translatable(
                "tooltip.eternal_career.cooking_magic_hand.bonus_loot")
                .withStyle(ChatFormatting.DARK_PURPLE));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    private static boolean isFunctionalHandsSlot(SlotContext slotContext) {
        return slotContext != null
                && HANDS_SLOT.equals(slotContext.identifier())
                && !slotContext.cosmetic();
    }

    private static UUID stableModifierId(String attributePath) {
        return UUID.nameUUIDFromBytes((EternalCareer.MOD_ID
                + ":cooking_magic_hand/"
                + attributePath).getBytes(StandardCharsets.UTF_8));
    }
}
