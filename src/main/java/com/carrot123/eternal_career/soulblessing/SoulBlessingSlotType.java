package com.carrot123.eternal_career.soulblessing;

public enum SoulBlessingSlotType {
    HEAD("head"), NECKLACE("necklace"), HAND("hand"), RING("ring"), BOOTS("boots");

    private final String id;

    SoulBlessingSlotType(String id) { this.id = id; }

    public String id() { return id; }

    public String translationKey() { return "soul_blessing.eternal_career.type." + id; }
}
