package com.carrot123.eternal_career.effect;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.carrot123.eternal_career.registry.ModAttributes;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public final class SweetImpulseEffect extends MobEffect {

    public static final int DURATION_TICKS = 2400;
    public static final double KITCHENWARE_DAMAGE_BONUS = 0.25D;

    private static final UUID KITCHENWARE_DAMAGE_MODIFIER_ID =
            UUID.nameUUIDFromBytes(
                    "eternal_career:sweet_impulse/kitchenware_damage"
                            .getBytes(StandardCharsets.UTF_8)
            );

    public SweetImpulseEffect() {
        super(
                MobEffectCategory.BENEFICIAL,
                0xFF8FB7
        );

        addAttributeModifier(
                ModAttributes.KITCHENWARE_DAMAGE.get(),
                KITCHENWARE_DAMAGE_MODIFIER_ID.toString(),
                KITCHENWARE_DAMAGE_BONUS,
                AttributeModifier.Operation.ADDITION
        );
    }
}