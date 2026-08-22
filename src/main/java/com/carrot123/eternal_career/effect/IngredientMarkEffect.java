package com.carrot123.eternal_career.effect;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

/** Marks a target as a vulnerable cooking ingredient. */
public final class IngredientMarkEffect extends MobEffect {
    public static final int INGREDIENT_MARK_DURATION_TICKS = 200;
    public static final double ARMOR_MULTIPLIER = -0.25D;
    private static final UUID ARMOR_MODIFIER_ID = UUID.nameUUIDFromBytes(
            "eternal_career:ingredient_mark/armor".getBytes(StandardCharsets.UTF_8));

    public IngredientMarkEffect() {
        super(MobEffectCategory.HARMFUL, 0xA94A3F);
        addAttributeModifier(
                Attributes.ARMOR,
                ARMOR_MODIFIER_ID.toString(),
                ARMOR_MULTIPLIER,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
