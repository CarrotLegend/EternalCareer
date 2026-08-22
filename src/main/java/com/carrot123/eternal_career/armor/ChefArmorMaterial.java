package com.carrot123.eternal_career.armor;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

/** Chef armor: iron defense defaults with netherite toughness and knockback resistance. */
public enum ChefArmorMaterial implements ArmorMaterial {
    INSTANCE;

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return ArmorMaterials.IRON.getDurabilityForType(type);
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> 2;
            case CHESTPLATE -> 6;
            case LEGGINGS -> 5;
            case BOOTS -> 2;
        };
    }

    @Override
    public int getEnchantmentValue() {
        return ArmorMaterials.IRON.getEnchantmentValue();
    }

    @Override
    public SoundEvent getEquipSound() {
        return ArmorMaterials.IRON.getEquipSound();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return ArmorMaterials.IRON.getRepairIngredient();
    }

    @Override
    public String getName() {
        return EternalCareer.MOD_ID + ":chef";
    }

    @Override
    public float getToughness() {
        return ArmorMaterials.NETHERITE.getToughness();
    }

    @Override
    public float getKnockbackResistance() {
        return ArmorMaterials.NETHERITE.getKnockbackResistance();
    }
}
