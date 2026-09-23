package com.carrot123.eternal_career.effect;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.carrot123.eternal_career.registry.ModAttributes;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class FeastSatisfactionEffect extends MobEffect {

    public static final int DURATION_TICKS = 2400;

    public static final double KITCHENWARE_DAMAGE_BONUS = 0.10D;
    public static final double ATTACK_SPEED_BONUS = 0.10D;
    public static final double MOVEMENT_SPEED_BONUS = 0.10D;

    private static final UUID KITCHENWARE_DAMAGE_MODIFIER_ID =
            UUID.nameUUIDFromBytes(
                    "eternal_career:feast_satisfaction/kitchenware_damage"
                            .getBytes(StandardCharsets.UTF_8)
            );

    private static final UUID ATTACK_SPEED_MODIFIER_ID =
            UUID.nameUUIDFromBytes(
                    "eternal_career:feast_satisfaction/attack_speed"
                            .getBytes(StandardCharsets.UTF_8)
            );

    private static final UUID MOVEMENT_SPEED_MODIFIER_ID =
            UUID.nameUUIDFromBytes(
                    "eternal_career:feast_satisfaction/movement_speed"
                            .getBytes(StandardCharsets.UTF_8)
            );

    public FeastSatisfactionEffect() {
        super(
                MobEffectCategory.BENEFICIAL,
                0xE5A34D
        );

        addAttributeModifier(
                ModAttributes.KITCHENWARE_DAMAGE.get(),
                KITCHENWARE_DAMAGE_MODIFIER_ID.toString(),
                KITCHENWARE_DAMAGE_BONUS,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        addAttributeModifier(
                Attributes.ATTACK_SPEED,
                ATTACK_SPEED_MODIFIER_ID.toString(),
                ATTACK_SPEED_BONUS,
                AttributeModifier.Operation.MULTIPLY_BASE
        );

        addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                MOVEMENT_SPEED_MODIFIER_ID.toString(),
                MOVEMENT_SPEED_BONUS,
                AttributeModifier.Operation.MULTIPLY_BASE
        );
    }
}