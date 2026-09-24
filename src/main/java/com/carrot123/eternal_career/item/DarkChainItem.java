package com.carrot123.eternal_career.item;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.until_eternity.compat.GoetyRevelationAttributesCompat;
import com.carrot123.until_eternity.registry.ModAttributes;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class DarkChainItem extends Item implements ICurioItem {
    public static final String SLOT = "necklace";

    public DarkChainItem(Properties properties) {
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
        if (context == null
                || !SLOT.equals(context.identifier())
                || context.cosmetic()) {
            return ImmutableMultimap.of();
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder =
                ImmutableMultimap.builder();

        builder.put(
                ModAttributes.FOCUS_DAMAGE.get(),
                new AttributeModifier(
                        modifierId(slotUuid, "focus_damage"),
                        EternalCareer.MOD_ID + ":dark_chain/focus_damage",
                        0.25D,
                        AttributeModifier.Operation.MULTIPLY_BASE
                )
        );

        Attribute spellPowerMultiplier =
                GoetyRevelationAttributesCompat.resolve(
                        GoetyRevelationAttributesCompat.SPELL_POWER_MULTIPLIER
                );

        if (spellPowerMultiplier != null) {
            builder.put(
                    spellPowerMultiplier,
                    new AttributeModifier(
                            modifierId(slotUuid, "spell_power_multiplier"),
                            EternalCareer.MOD_ID + ":dark_chain/spell_power_multiplier",
                            0.25D,
                            AttributeModifier.Operation.ADDITION
                    )
            );
        }

        Attribute castDuration =
                GoetyRevelationAttributesCompat.resolve(
                        GoetyRevelationAttributesCompat.CAST_DURATION
                );

        if (castDuration != null) {
            builder.put(
                    castDuration,
                    new AttributeModifier(
                            modifierId(slotUuid, "cast_duration"),
                            EternalCareer.MOD_ID + ":dark_chain/cast_duration",
                            0.10D,
                            AttributeModifier.Operation.ADDITION
                    )
            );
        }

        return builder.build();
    }

    private static UUID modifierId(UUID slotUuid, String key) {
        return UUID.nameUUIDFromBytes(
                (EternalCareer.MOD_ID
                        + ":dark_chain/"
                        + slotUuid
                        + "/"
                        + key)
                        .getBytes(StandardCharsets.UTF_8)
        );
    }
}