package com.carrot123.eternal_career.lich;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.Multimap;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.junit.jupiter.api.Test;

final class LichSpellAttributesTest {
    private static final Set<String> TARGET_SCHOOLS = Set.of(
            "necromancy", "abyss", "frost", "geomancy", "ill", "nether",
            "none", "storm", "void", "wild", "wind");

    @Test
    void targetsExactlyTheRevelationSchoolAttributesWithStableUniqueUuids() {
        assertEquals(11, LichSpellAttributes.SCHOOL_POWERS.size());
        assertEquals(TARGET_SCHOOLS, LichSpellAttributes.SCHOOL_POWERS.stream()
                .map(school -> {
                    assertEquals("goety_revelation", school.id().getNamespace());
                    assertTrue(school.id().getPath().endsWith("_power"));
                    String key = "eternal_career:lich_research_notes/" + school.id();
                    assertEquals(UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8)),
                            school.uuid());
                    return school.id().getPath().replaceFirst("_power$", "");
                }).collect(Collectors.toSet()));
        assertEquals(11, LichSpellAttributes.SCHOOL_POWERS.stream()
                .map(LichSpellAttributes.SchoolPower::uuid).distinct().count());
        assertFalse(LichSpellAttributes.SCHOOL_POWERS.stream().anyMatch(school ->
                Set.of("spell_power", "spell_power_multiplier").contains(school.id().getPath())));
    }

    @Test
    void buildsTheExpectedCuriosModifiersForOnlyThoseAttributes() {
        Map<ResourceLocation, Attribute> attributes = new LinkedHashMap<>();
        for (LichSpellAttributes.SchoolPower school : LichSpellAttributes.SCHOOL_POWERS) {
            attributes.put(school.id(), new RangedAttribute(school.id().toString(), 0.0D,
                    0.0D, 32767.0D));
        }

        Multimap<Attribute, AttributeModifier> modifiers =
                LichSpellAttributes.modifiers(attributes::get);
        assertEquals(11, modifiers.size());
        for (LichSpellAttributes.SchoolPower school : LichSpellAttributes.SCHOOL_POWERS) {
            AttributeModifier modifier = modifiers.get(attributes.get(school.id())).iterator().next();
            assertEquals(school.uuid(), modifier.getId());
            assertEquals(school.id().equals(LichSpellAttributes.NECROMANCY_POWER)
                    ? 0.20D : -0.90D, modifier.getAmount());
            assertEquals(AttributeModifier.Operation.MULTIPLY_TOTAL, modifier.getOperation());
        }
    }

    @Test
    void reportsTheExactMissingRevelationAttribute() {
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> LichSpellAttributes.modifiers(id -> null));
        assertTrue(error.getMessage().contains("goety_revelation:necromancy_power"));
    }
}
