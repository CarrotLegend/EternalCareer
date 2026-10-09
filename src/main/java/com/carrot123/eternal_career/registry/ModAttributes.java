package com.carrot123.eternal_career.registry;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModAttributes {
    public static final double DEFAULT_DAMAGE_MULTIPLIER = 1.0D;
    public static final double MIN_DAMAGE_MULTIPLIER = 0.0D;
    public static final double MAX_DAMAGE_MULTIPLIER = 1024.0D;
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, EternalCareer.MOD_ID);
    public static final RegistryObject<Attribute> KITCHENWARE_DAMAGE = damage("kitchenware_damage", "attribute.name.eternal_career.kitchenware_damage");
    public static final RegistryObject<Attribute> NON_KITCHENWARE_DAMAGE = damage("non_kitchenware_damage", "attribute.name.eternal_career.non_kitchenware_damage");
    public static final RegistryObject<Attribute> SCYTHE_DAMAGE = damage("scythe_damage", "attribute.eternal_career.scythe_damage");
    public static final RegistryObject<Attribute> NON_SCYTHE_DAMAGE = damage("non_scythe_damage", "attribute.eternal_career.non_scythe_damage");
    public static final RegistryObject<Attribute> NON_RANGED_DAMAGE = damage("non_ranged_damage", "attribute.name.eternal_career.non_ranged_damage");

    private ModAttributes() {}
    private static RegistryObject<Attribute> damage(String id, String translation) {
        return ATTRIBUTES.register(id, () -> new RangedAttribute(translation, DEFAULT_DAMAGE_MULTIPLIER, MIN_DAMAGE_MULTIPLIER, MAX_DAMAGE_MULTIPLIER).setSyncable(true));
    }
    public static void register(IEventBus bus) { ATTRIBUTES.register(bus); bus.addListener(ModAttributes::addPlayerAttributes); }
    private static void addPlayerAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, KITCHENWARE_DAMAGE.get());
        event.add(EntityType.PLAYER, NON_KITCHENWARE_DAMAGE.get());
        event.add(EntityType.PLAYER, SCYTHE_DAMAGE.get());
        event.add(EntityType.PLAYER, NON_SCYTHE_DAMAGE.get());
        event.add(EntityType.PLAYER, NON_RANGED_DAMAGE.get());
    }
}
