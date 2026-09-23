package com.carrot123.eternal_career.lich;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.Polarice3.Goety.api.magic.SpellType;
import org.junit.jupiter.api.Test;

final class LichSpellPotencyTest {
    @Test
    void onlyTheOriginalPotencyContributionChanges() {
        assertEquals(24L, result(10, 4.0D, 2.0D, 1.5D, SpellType.FROST, false));
        assertEquals(11L, result(10, 4.0D, 2.0D, 1.5D, SpellType.FROST, true));
        assertEquals(27L, result(10, 4.0D, 2.0D, 1.5D, SpellType.NECROMANCY, true));
        assertEquals(11L, result(10, 4.0D, 2.0D, 1.5D, SpellType.NETHER, true));
        assertEquals(11L, result(10, 4.0D, 2.0D, 1.5D, SpellType.WILD, true));
        assertEquals(11L, result(10, 4.0D, 2.0D, 1.5D, SpellType.NONE, true));
    }

    @Test
    void lowPotencyRetainsFractionsUntilRevelationFixRounds() {
        for (int original = 1; original <= 9; original++) {
            double adjusted = LichSpellPotency.adjust(original, original, SpellType.FROST, true);
            assertEquals(original * 0.10D, adjusted, 1.0E-12D);
            assertEquals(Math.round(original * 0.10D), Math.round(adjusted));
        }
        assertTrue(LichSpellPotency.adjust(5.0D, 5, SpellType.FROST, true) > 0.0D);
        assertEquals(1L, result(5, 0.0D, 0.0D, 1.0D, SpellType.FROST, true));
    }

    @Test
    void repeatedCastsUseTheUnmodifiedOriginalEachTime() {
        for (int cast = 0; cast < 10; cast++) {
            assertEquals(1.0D, LichSpellPotency.adjust(10.0D, 10, SpellType.FROST, true));
            assertEquals(12.0D, LichSpellPotency.adjust(10.0D, 10, SpellType.NECROMANCY, true));
        }
    }

    private static long result(int original, double schoolPower, double genericPower,
            double powerMultiplier, SpellType type, boolean hasResearchNotes) {
        double sum = schoolPower + genericPower + original;
        return Math.round(LichSpellPotency.adjust(sum, original, type, hasResearchNotes)
                * powerMultiplier);
    }
}
