package com.carrot123.eternal_career.event;

public final class ChaosBladeDropChance {
    private ChaosBladeDropChance() {
    }

    public static double calculate(boolean boss, int lootingLevel) {
        double baseChance = boss ? 0.25D : 0.05D;
        return Math.min(1.0D, Math.max(0.0D,
                baseChance * (1.0D + 0.25D * Math.max(0, lootingLevel))));
    }
}
