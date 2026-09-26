package com.carrot123.eternal_career.soulblessing;

public final class SoulBlessingSlots {
    public static final int HEAD = 0;
    public static final int NECKLACE = 1;
    public static final int HAND = 2;
    public static final int RING = 3;
    public static final int BOOTS_LEFT = 4;
    public static final int BOOTS_RIGHT = 5;
    public static final int COUNT = 6;

    private static final SoulBlessingSlotType[] TYPES = {
            SoulBlessingSlotType.HEAD, SoulBlessingSlotType.NECKLACE,
            SoulBlessingSlotType.HAND,
            SoulBlessingSlotType.RING, SoulBlessingSlotType.BOOTS,
            SoulBlessingSlotType.BOOTS
    };

    private SoulBlessingSlots() {}

    public static SoulBlessingSlotType type(int index) {
        if (index < 0 || index >= COUNT) throw new IndexOutOfBoundsException(index);
        return TYPES[index];
    }
}
