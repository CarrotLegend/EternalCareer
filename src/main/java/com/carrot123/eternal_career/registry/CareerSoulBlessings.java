package com.carrot123.eternal_career.registry;

import com.carrot123.eternal_career.soulblessing.SoulBlessingAttribute;
import com.carrot123.eternal_career.soulblessing.SoulBlessingSlotType;
import com.carrot123.eternal_career.soulblessing.SoulBlessingSlots;
import com.carrot123.until_eternity.registry.ModAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class CareerSoulBlessings {
    public static final RegistryObject<Item> ARCHER_HEAD_BLESSING = register(
            "archer_head_blessing", SoulBlessingSlotType.HEAD, -1,
            spec(() -> Attributes.MOVEMENT_SPEED, 0.20D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> ARCHER_HAND_BLESSING = register(
            "archer_hand_blessing", SoulBlessingSlotType.HAND, -1,
            spec(ModAttributes.ALL_DAMAGE::get, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
            spec(external("terra_curio:ranged_damage"), 0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> ARCHER_RING_BLESSING = register(
            "archer_ring_blessing", SoulBlessingSlotType.RING, -1,
            spec(external("terra_curio:crit_chance"), 0.05D, AttributeModifier.Operation.ADDITION),
            spec(external("terra_curio:ranged_damage"), 0.02D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> ARCHER_NECKLACE_BLESSING = register(
            "archer_necklace_blessing", SoulBlessingSlotType.NECKLACE, -1,
            spec(external("terra_curio:ranged_damage"), 0.14D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> ARCHER_LEFT_BOOT_BLESSING = register(
            "archer_left_boot_blessing", SoulBlessingSlotType.BOOTS, SoulBlessingSlots.BOOTS_LEFT,
            spec(external("terra_curio:ranged_velocity"), 0.20D, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<Item> ARCHER_RIGHT_BOOT_BLESSING = register(
            "archer_right_boot_blessing", SoulBlessingSlotType.BOOTS, SoulBlessingSlots.BOOTS_RIGHT,
            spec(ModAttributes.CHARGE_SPEED::get, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<Item> WARRIOR_HEAD_BLESSING = register(
            "warrior_head_blessing", SoulBlessingSlotType.HEAD, -1,
            spec(() -> Attributes.MOVEMENT_SPEED, 0.20D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> WARRIOR_HAND_BLESSING = register(
            "warrior_hand_blessing", SoulBlessingSlotType.HAND, -1,
            spec(ModAttributes.ALL_DAMAGE::get, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
            spec(() -> Attributes.ATTACK_DAMAGE, 0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> WARRIOR_RING_BLESSING = register(
            "warrior_ring_blessing", SoulBlessingSlotType.RING, -1,
            spec(external("terra_curio:crit_chance"), 0.05D, AttributeModifier.Operation.ADDITION),
            spec(() -> Attributes.ATTACK_DAMAGE, 0.02D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> WARRIOR_NECKLACE_BLESSING = register(
            "warrior_necklace_blessing", SoulBlessingSlotType.NECKLACE, -1,
            spec(() -> Attributes.ATTACK_DAMAGE, 0.14D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> WARRIOR_LEFT_BOOT_BLESSING = register(
            "warrior_left_boot_blessing", SoulBlessingSlotType.BOOTS, SoulBlessingSlots.BOOTS_LEFT,
            spec(() -> Attributes.ATTACK_SPEED, 0.20D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> WARRIOR_RIGHT_BOOT_BLESSING = register(
            "warrior_right_boot_blessing", SoulBlessingSlotType.BOOTS, SoulBlessingSlots.BOOTS_RIGHT,
            spec(external("obscure_api:critical_damage"), 0.20D, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<Item> MAGE_HEAD_BLESSING = register(
            "mage_head_blessing", SoulBlessingSlotType.HEAD, -1,
            spec(() -> Attributes.MOVEMENT_SPEED, 0.20D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> MAGE_HAND_BLESSING = register(
            "mage_hand_blessing", SoulBlessingSlotType.HAND, -1,
            spec(ModAttributes.ALL_DAMAGE::get, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
            spec(external("irons_spellbooks:spell_power"), 0.10D, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<Item> MAGE_RING_BLESSING = register(
            "mage_ring_blessing", SoulBlessingSlotType.RING, -1,
            spec(external("terra_curio:crit_chance"), 0.05D, AttributeModifier.Operation.ADDITION),
            spec(external("irons_spellbooks:spell_power"), 0.02D, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<Item> MAGE_NECKLACE_BLESSING = register(
            "mage_necklace_blessing", SoulBlessingSlotType.NECKLACE, -1,
            spec(external("irons_spellbooks:spell_power"), 0.14D, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<Item> MAGE_LEFT_BOOT_BLESSING = register(
            "mage_left_boot_blessing", SoulBlessingSlotType.BOOTS, SoulBlessingSlots.BOOTS_LEFT,
            spec(external("irons_spellbooks:cast_time_reduction"), 0.20D, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<Item> MAGE_RIGHT_BOOT_BLESSING = register(
            "mage_right_boot_blessing", SoulBlessingSlotType.BOOTS, SoulBlessingSlots.BOOTS_RIGHT,
            spec(external("irons_spellbooks:cooldown_reduction"), 0.10D, AttributeModifier.Operation.MULTIPLY_BASE));

    public static final RegistryObject<Item> WARLOCK_HEAD_BLESSING = register(
            "warlock_head_blessing", SoulBlessingSlotType.HEAD, -1,
            spec(() -> Attributes.MOVEMENT_SPEED, 0.20D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> WARLOCK_HAND_BLESSING = register(
            "warlock_hand_blessing", SoulBlessingSlotType.HAND, -1,
            spec(ModAttributes.ALL_DAMAGE::get, 0.10D, AttributeModifier.Operation.MULTIPLY_BASE),
            spec(ModAttributes.FOCUS_DAMAGE::get, 0.10D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> WARLOCK_RING_BLESSING = register(
            "warlock_ring_blessing", SoulBlessingSlotType.RING, -1,
            spec(external("terra_curio:crit_chance"), 0.05D, AttributeModifier.Operation.ADDITION),
            spec(ModAttributes.FOCUS_DAMAGE::get, 0.02D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> WARLOCK_NECKLACE_BLESSING = register(
            "warlock_necklace_blessing", SoulBlessingSlotType.NECKLACE, -1,
            spec(ModAttributes.FOCUS_DAMAGE::get, 0.14D, AttributeModifier.Operation.MULTIPLY_TOTAL));

    public static final RegistryObject<Item> WARLOCK_LEFT_BOOT_BLESSING = register(
            "warlock_left_boot_blessing", SoulBlessingSlotType.BOOTS, SoulBlessingSlots.BOOTS_LEFT,
            spec(external("goety_revelation:spell_cooldown"), 0.10D, AttributeModifier.Operation.ADDITION));

    public static final RegistryObject<Item> WARLOCK_RIGHT_BOOT_BLESSING = register(
            "warlock_right_boot_blessing", SoulBlessingSlotType.BOOTS, SoulBlessingSlots.BOOTS_RIGHT,
            spec(external("goety_revelation:spell_power_multiplier"), 2.0D, AttributeModifier.Operation.ADDITION));

    public static final List<RegistryObject<Item>> ALL = List.of(
            ARCHER_HEAD_BLESSING,
            ARCHER_HAND_BLESSING,
            ARCHER_RING_BLESSING,
            ARCHER_NECKLACE_BLESSING,
            ARCHER_LEFT_BOOT_BLESSING,
            ARCHER_RIGHT_BOOT_BLESSING,
            WARRIOR_HEAD_BLESSING,
            WARRIOR_HAND_BLESSING,
            WARRIOR_RING_BLESSING,
            WARRIOR_NECKLACE_BLESSING,
            WARRIOR_LEFT_BOOT_BLESSING,
            WARRIOR_RIGHT_BOOT_BLESSING,
            MAGE_HEAD_BLESSING,
            MAGE_HAND_BLESSING,
            MAGE_RING_BLESSING,
            MAGE_NECKLACE_BLESSING,
            MAGE_LEFT_BOOT_BLESSING,
            MAGE_RIGHT_BOOT_BLESSING,
            WARLOCK_HEAD_BLESSING,
            WARLOCK_HAND_BLESSING,
            WARLOCK_RING_BLESSING,
            WARLOCK_NECKLACE_BLESSING,
            WARLOCK_LEFT_BOOT_BLESSING,
            WARLOCK_RIGHT_BOOT_BLESSING);

    private CareerSoulBlessings() {
    }

    public static void init() {
    }

    private static RegistryObject<Item> register(String id, SoulBlessingSlotType type,
            int requiredSlot, AttributeSpec... specs) {
        return ModItems.registerCareerBlessing(id, type, requiredSlot, () -> {
            List<SoulBlessingAttribute> attributes = new ArrayList<>(specs.length);
            for (AttributeSpec spec : specs) {
                Attribute attribute = spec.attribute().get();
                if (attribute == null) {
                    throw new IllegalStateException("Missing Soul Blessing attribute for " + id);
                }
                attributes.add(new SoulBlessingAttribute(
                        attribute, spec.amount(), spec.operation()));
            }
            return List.copyOf(attributes);
        });
    }

    private static AttributeSpec spec(Supplier<Attribute> attribute, double amount,
            AttributeModifier.Operation operation) {
        return new AttributeSpec(attribute, amount, operation);
    }

    private static Supplier<Attribute> external(String id) {
        ResourceLocation key = new ResourceLocation(id);
        return () -> ForgeRegistries.ATTRIBUTES.getValue(key);
    }

    private record AttributeSpec(Supplier<Attribute> attribute, double amount,
            AttributeModifier.Operation operation) {
    }
}
