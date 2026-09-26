package com.carrot123.eternal_career.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

/** Stable modifier identities shared by Curios and Soul Blessings. */
public final class StableAttributeModifiers {
    private StableAttributeModifiers() {}

    public static UUID id(String key) {
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }

    public static AttributeModifier create(String key, double amount,
            AttributeModifier.Operation operation) {
        return create(id(key), key, amount, operation);
    }

    public static AttributeModifier create(UUID id, String name, double amount,
            AttributeModifier.Operation operation) {
        return new AttributeModifier(id, name, amount, operation);
    }
}
