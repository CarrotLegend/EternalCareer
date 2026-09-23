package com.carrot123.eternal_career.lich;

import com.Polarice3.Goety.api.magic.SpellType;

public final class LichSpellPotency {
    private LichSpellPotency() {
    }

    public static double adjust(double currentPower, int originalPotency,
            SpellType spellType, boolean hasResearchNotes) {
        if (!hasResearchNotes) {
            return currentPower;
        }
        double multiplier = spellType == SpellType.NECROMANCY ? 1.20D : 0.10D;
        return currentPower - originalPotency + originalPotency * multiplier;
    }
}
