package com.carrot123.eternal_career.compat.puffish;

import com.carrot123.until_eternity.compat.PuffishAttributesCompat;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

public final class PuffishAttributesHelper {

    public static final ResourceLocation LIFE_STEAL =
            new ResourceLocation(
                    "puffish_attributes",
                    "life_steal"
            );

    public static final ResourceLocation RESISTANCE =
            new ResourceLocation(
                    "puffish_attributes",
                    "resistance"
            );

    public static final ResourceLocation NATURAL_REGENERATION =
            new ResourceLocation(
                    "puffish_attributes",
                    "natural_regeneration"
            );

    public static final ResourceLocation STAMINA =
            new ResourceLocation(
                    "puffish_attributes",
                    "stamina"
            );

    private PuffishAttributesHelper() {
    }

    @Nullable
    public static Attribute resolve(
            ResourceLocation id
    ) {
        return PuffishAttributesCompat.resolve(id);
    }
}