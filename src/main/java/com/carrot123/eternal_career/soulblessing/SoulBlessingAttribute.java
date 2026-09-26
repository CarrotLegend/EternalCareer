package com.carrot123.eternal_career.soulblessing;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public record SoulBlessingAttribute(Attribute attribute, double amount,
        AttributeModifier.Operation operation) {}
