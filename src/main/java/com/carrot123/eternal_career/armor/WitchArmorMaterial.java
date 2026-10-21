package com.carrot123.eternal_career.armor;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

public enum WitchArmorMaterial implements ArmorMaterial {
    INSTANCE;

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return ArmorMaterials.NETHERITE.getDurabilityForType(type);
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 5;
            case CHESTPLATE -> 10;
            case LEGGINGS -> 8;
            case BOOTS -> 5;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return ArmorMaterials.NETHERITE.getEnchantmentValue();
    }

    @Override
    public SoundEvent getEquipSound() {
        return ArmorMaterials.NETHERITE.getEquipSound();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return ArmorMaterials.NETHERITE.getRepairIngredient();
    }

    @Override
    public String getName() {
        return EternalCareer.MOD_ID + ":witch_armor";
    }

    @Override
    public float getToughness() {
        return 4.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return ArmorMaterials.NETHERITE.getKnockbackResistance();
    }
}
