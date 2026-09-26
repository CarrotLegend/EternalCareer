package com.carrot123.eternal_career.career.capability.soul;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public final class SoulProvider implements ICapabilitySerializable<CompoundTag> {
    private final Soul data;
    private LazyOptional<ISoul> optional;
    public SoulProvider(Runnable changed) {
        data = new Soul(changed);
        optional = LazyOptional.of(() -> data);
    }
    @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return SoulCapability.SOUL.orEmpty(cap, optional);
    }
    @Override public CompoundTag serializeNBT() { return data.serializeNBT(); }
    @Override public void deserializeNBT(CompoundTag tag) { data.deserializeNBT(tag); }
    public void invalidate() {
        optional.invalidate();
        optional = LazyOptional.of(() -> data);
    }
}
