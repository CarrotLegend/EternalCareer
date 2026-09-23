package com.carrot123.eternal_career.lich;

import com.carrot123.eternal_career.EternalCareer;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;

public final class LichSpellAttributes {
    public static final ResourceLocation NECROMANCY_POWER = powerId("necromancy");

    private static final UUID LICH_NECROMANCY_POWER_UUID =
            UUID.fromString("85ac4f34-e47f-3039-aa08-8c4b750a2fec");
    private static final UUID LICH_ABYSS_POWER_UUID =
            UUID.fromString("ee0f0a3a-711f-386c-bf3b-7733456709b7");
    private static final UUID LICH_FROST_POWER_UUID =
            UUID.fromString("25e1dc62-91bc-3582-a6be-748b9d3b63b0");
    private static final UUID LICH_GEOMANCY_POWER_UUID =
            UUID.fromString("96b62cc9-4e38-3005-914c-de256b20aa65");
    private static final UUID LICH_ILL_POWER_UUID =
            UUID.fromString("211f5246-58ab-3674-b2e7-a6d643d005ef");
    private static final UUID LICH_NETHER_POWER_UUID =
            UUID.fromString("fa5ce2bc-9546-3e40-992b-5c7634e5fe71");
    private static final UUID LICH_NONE_POWER_UUID =
            UUID.fromString("0442c959-7bc2-38ec-98fc-4e56411739b0");
    private static final UUID LICH_STORM_POWER_UUID =
            UUID.fromString("360e6ec9-00a7-3063-86cd-dfd13f3192d3");
    private static final UUID LICH_VOID_POWER_UUID =
            UUID.fromString("80cb8b12-c2b6-3530-a048-449f0844ec1e");
    private static final UUID LICH_WILD_POWER_UUID =
            UUID.fromString("c6e5a939-aae6-3db6-a0a9-18479bf8e648");
    private static final UUID LICH_WIND_POWER_UUID =
            UUID.fromString("e009a287-14cf-33c9-a041-4649b4ac4ac8");

    // RevelationFix registers one Attribute per Goety SpellType in ModAttributes.spellAttributes.
    // The class is inside Goety Revelation's JarJar and is not on this project's compile classpath.
    static final List<SchoolPower> SCHOOL_POWERS = List.of(
            new SchoolPower(NECROMANCY_POWER, LICH_NECROMANCY_POWER_UUID, 0.20D),
            new SchoolPower(powerId("abyss"), LICH_ABYSS_POWER_UUID, -0.90D),
            new SchoolPower(powerId("frost"), LICH_FROST_POWER_UUID, -0.90D),
            new SchoolPower(powerId("geomancy"), LICH_GEOMANCY_POWER_UUID, -0.90D),
            new SchoolPower(powerId("ill"), LICH_ILL_POWER_UUID, -0.90D),
            new SchoolPower(powerId("nether"), LICH_NETHER_POWER_UUID, -0.90D),
            new SchoolPower(powerId("none"), LICH_NONE_POWER_UUID, -0.90D),
            new SchoolPower(powerId("storm"), LICH_STORM_POWER_UUID, -0.90D),
            new SchoolPower(powerId("void"), LICH_VOID_POWER_UUID, -0.90D),
            new SchoolPower(powerId("wild"), LICH_WILD_POWER_UUID, -0.90D),
            new SchoolPower(powerId("wind"), LICH_WIND_POWER_UUID, -0.90D));

    private LichSpellAttributes() {
    }

    public static Multimap<Attribute, AttributeModifier> modifiers() {
        return modifiers(ForgeRegistries.ATTRIBUTES::getValue);
    }

    static Multimap<Attribute, AttributeModifier> modifiers(
            Function<ResourceLocation, Attribute> lookup) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> result = ImmutableMultimap.builder();
        for (SchoolPower school : SCHOOL_POWERS) {
            Attribute attribute = lookup.apply(school.id());
            if (attribute == null) {
                throw new IllegalStateException("Missing Goety Revelation attribute: " + school.id());
            }
            result.put(attribute, new AttributeModifier(school.uuid(),
                    EternalCareer.MOD_ID + ":lich_research_notes/" + school.id(),
                    school.amount(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        return result.build();
    }

    private static ResourceLocation powerId(String school) {
        return new ResourceLocation("goety_revelation", school + "_power");
    }

    record SchoolPower(ResourceLocation id, UUID uuid, double amount) {
    }
}
