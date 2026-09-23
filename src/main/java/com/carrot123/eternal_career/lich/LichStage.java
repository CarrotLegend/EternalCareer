package com.carrot123.eternal_career.lich;

import net.minecraft.network.chat.Component;

public enum LichStage {
    PLAYER,
    NOVICE_LICH,
    INTERMEDIATE_LICH,
    ADVANCED_LICH,
    LICH_KING;

    public boolean isAtLeast(LichStage other) {
        return ordinal() >= other.ordinal();
    }

    public Component getDisplayName() {
        return Component.translatable("lich_stage.eternal_career." + name().toLowerCase(java.util.Locale.ROOT));
    }

    public static LichStage fromName(String name) {
        for (LichStage stage : values()) {
            if (stage.name().equals(name)) {
                return stage;
            }
        }
        return PLAYER;
    }
}
