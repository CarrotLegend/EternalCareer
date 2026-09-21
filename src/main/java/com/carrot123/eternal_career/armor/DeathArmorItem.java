package com.carrot123.eternal_career.armor;

import com.carrot123.eternal_career.EternalCareer;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class DeathArmorItem extends ArmorItem {

    public static final double CRITICAL_CHANCE_PER_PIECE = 0.075D;
    public static final double CRITICAL_DAMAGE_PER_PIECE = 0.10D;

    private static final ResourceLocation CRITICAL_CHANCE =
            new ResourceLocation("terra_curio", "crit_chance");

    private static final ResourceLocation CRITICAL_DAMAGE =
            new ResourceLocation("obscure_api", "critical_damage");

    public DeathArmorItem(
            ArmorMaterial material,
            Type type,
            Properties properties
    ) {
        super(material, type, properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(
            EquipmentSlot slot
    ) {
        Multimap<Attribute, AttributeModifier> vanilla =
                super.getDefaultAttributeModifiers(slot);

        if (slot != getEquipmentSlot()) {
            return vanilla;
        }

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder =
                ImmutableMultimap.builder();

        builder.putAll(vanilla);

        Attribute criticalChance =
                ForgeRegistries.ATTRIBUTES.getValue(CRITICAL_CHANCE);

        Attribute criticalDamage =
                ForgeRegistries.ATTRIBUTES.getValue(CRITICAL_DAMAGE);

        if (criticalChance != null) {
            builder.put(
                    criticalChance,
                    new AttributeModifier(
                            createModifierId(slot, "critical_chance"),
                            "Death armor critical chance",
                            CRITICAL_CHANCE_PER_PIECE,
                            AttributeModifier.Operation.ADDITION
                    )
            );
        }

        if (criticalDamage != null) {
            builder.put(
                    criticalDamage,
                    new AttributeModifier(
                            createModifierId(slot, "critical_damage"),
                            "Death armor critical damage",
                            CRITICAL_DAMAGE_PER_PIECE,
                            AttributeModifier.Operation.MULTIPLY_BASE
                    )
            );
        }

        return builder.build();
    }

    private static UUID createModifierId(
            EquipmentSlot slot,
            String attribute
    ) {
        String key =
                EternalCareer.MOD_ID
                        + ":death_armor/"
                        + slot.getName()
                        + "/"
                        + attribute;

        return UUID.nameUUIDFromBytes(
                key.getBytes(StandardCharsets.UTF_8)
        );
    }
}