package com.carrot123.eternal_career.compat.puffish;

import com.carrot123.until_eternity.compat.PuffishAttributesCompat;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

/** Centralized Eternal Career access to attributes owned by Pufferfish's Attributes. */
public final class PuffishAttributesHelper {
    public static final ResourceLocation LIFE_STEAL =
            new ResourceLocation("puffish_attributes", "life_steal");

    private PuffishAttributesHelper() {
    }

    /** Uses the core mod's shared resolver and missing-attribute error reporting. */
    @Nullable
    public static Attribute resolve(ResourceLocation id) {
        return PuffishAttributesCompat.resolve(id);
    }
}
