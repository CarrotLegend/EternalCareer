package com.carrot123.eternal_career.career.capability.soul;

import net.minecraft.nbt.CompoundTag;

/** Persistent balance, deliberately independent of equipment and its capacity. */
public final class Soul implements ISoul {
    private int soul;
    private final Runnable changed;

    public Soul(Runnable changed) { this.changed = changed; }
    @Override public int getSoul() { return soul; }
    @Override public void setSoul(int value) {
        int next = Math.max(0, value);
        if (soul != next) { soul = next; changed.run(); }
    }
    @Override public void addSoul(int amount, int maxSoul) {
        if (amount <= 0 || soul >= maxSoul) return;
        setSoul((int) Math.min((long) soul + amount, maxSoul));
    }
    @Override public boolean consumeSoul(int amount) {
        if (amount <= 0 || soul < amount) return false;
        setSoul(soul - amount);
        return true;
    }
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("soul", soul);
        return tag;
    }
    public void deserializeNBT(CompoundTag tag) {
        soul = Math.max(0, tag.getInt("soul"));
    }
}
