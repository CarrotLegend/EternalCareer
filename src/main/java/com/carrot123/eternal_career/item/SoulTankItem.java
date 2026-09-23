package com.carrot123.eternal_career.item;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.FoodBookCurio;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class SoulTankItem extends Item implements ICurioItem {

    public static final double MAX_HEALTH_BONUS = 200.0D;
    public static final double ARMOR_BONUS = 4.0D;

    public static final int SOUL_PER_LEVEL = 240;
    public static final double SCYTHE_DAMAGE_PER_LEVEL = 0.05D;

    private static final UUID MAX_HEALTH_MODIFIER_ID =
            stableModifierId("max_health");

    private static final UUID ARMOR_MODIFIER_ID =
            stableModifierId("armor");

    public SoulTankItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(
            SlotContext slotContext,
            ItemStack stack
    ) {
        return slotContext != null
                && FoodBookCurio.CHARM_SLOT.equals(
                        slotContext.identifier()
                )
                && !slotContext.cosmetic();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
            SlotContext slotContext,
            UUID slotUuid,
            ItemStack stack
    ) {
        if (!canEquip(slotContext, stack)
                || !(slotContext.entity() instanceof Player)) {
            return ImmutableMultimap.of();
        }

        return ImmutableMultimap.of(
                Attributes.MAX_HEALTH,
                new AttributeModifier(
                        MAX_HEALTH_MODIFIER_ID,
                        EternalCareer.MOD_ID
                                + ":soul_tank/max_health",
                        MAX_HEALTH_BONUS,
                        AttributeModifier.Operation.ADDITION
                ),
                Attributes.ARMOR,
                new AttributeModifier(
                        ARMOR_MODIFIER_ID,
                        EternalCareer.MOD_ID
                                + ":soul_tank/armor",
                        ARMOR_BONUS,
                        AttributeModifier.Operation.ADDITION
                )
        );
    }

    @Override
    public List<Component> getAttributesTooltip(
            List<Component> tooltips,
            ItemStack stack
    ) {
        tooltips.clear();
        return tooltips;
    }

    public static double getScytheDamageBonus(int soul) {
        int safeSoul = Math.max(0, soul);
        int levels = safeSoul / SOUL_PER_LEVEL;

        return levels * SCYTHE_DAMAGE_PER_LEVEL;
    }

    public static int getScytheDamageBonusPercent(int soul) {
        return (int) Math.round(
                getScytheDamageBonus(soul) * 100.0D
        );
    }

    private static UUID stableModifierId(String path) {
        return UUID.nameUUIDFromBytes(
                (
                        EternalCareer.MOD_ID
                                + ":soul_tank/"
                                + path
                ).getBytes(StandardCharsets.UTF_8)
        );
    }
}