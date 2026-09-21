package com.carrot123.eternal_career.armor;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

public enum DeathArmorMaterial implements ArmorMaterial {
    INSTANCE;

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return ArmorMaterials.NETHERITE.getDurabilityForType(type);
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 6;
            case CHESTPLATE -> 12;
            case LEGGINGS -> 8;
            case BOOTS -> 6;
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
        return Ingredient.EMPTY;
    }

    @Override
    public String getName() {
        return EternalCareer.MOD_ID + ":death_armor";
    }

    @Override
    public float getToughness() {
        return 4.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 1.0F;
    }
}