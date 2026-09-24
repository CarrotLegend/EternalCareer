package com.carrot123.eternal_career.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class DarkBootsItem extends Item implements ICurioItem {
    public static final String SLOT = "feet";

    public DarkBootsItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return context != null
                && SLOT.equals(context.identifier())
                && !context.cosmetic();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext context,
            UUID slotUuid,
            ItemStack stack
    ) {
        return ImmutableMultimap.of();
    }
}