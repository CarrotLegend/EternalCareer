package com.carrot123.eternal_career.effect;

import com.carrot123.eternal_career.EternalCareer;
import com.mojang.logging.LogUtils;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

public final class ChaoticCookingEffect extends MobEffect {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CRITICAL_CHANCE =
            new ResourceLocation("terra_curio", "crit_chance");
    private static final ResourceLocation CRITICAL_DAMAGE =
            new ResourceLocation("obscure_api", "critical_damage");
    private boolean bound;

    public ChaoticCookingEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xA675C3);
    }

    public void bindAttributes() {
        if (bound) {
            return;
        }
        Attribute chance = ForgeRegistries.ATTRIBUTES.getValue(CRITICAL_CHANCE);
        Attribute damage = ForgeRegistries.ATTRIBUTES.getValue(CRITICAL_DAMAGE);
        if (chance != null) {
            addAttributeModifier(chance, stableId("crit_chance"), 0.08D,
                    AttributeModifier.Operation.ADDITION);
        } else {
            LOGGER.warn("Chaotic Cooking attribute is unavailable: {}", CRITICAL_CHANCE);
        }
        if (damage != null) {
            addAttributeModifier(damage, stableId("critical_damage"), 0.25D,
                    AttributeModifier.Operation.MULTIPLY_BASE);
        } else {
            LOGGER.warn("Chaotic Cooking attribute is unavailable: {}", CRITICAL_DAMAGE);
        }
        bound = true;
    }

    private static String stableId(String path) {
        return UUID.nameUUIDFromBytes((EternalCareer.MOD_ID + ":chaotic_cooking/" + path)
                .getBytes(StandardCharsets.UTF_8)).toString();
    }
}
