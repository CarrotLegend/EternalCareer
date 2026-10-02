package com.carrot123.eternal_career.soulblessing;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public final class SoulBlessingProvider
        implements ICapabilitySerializable<CompoundTag> {

    private final SoulBlessingInventory inventory;

    private final LazyOptional<SoulBlessingInventory> optional;

    public SoulBlessingProvider(
            Runnable changed
    ) {
        this.inventory =
                new SoulBlessingInventory(
                        changed
                );

        this.optional =
                LazyOptional.of(
                        () -> inventory
                );
    }

    @Override
    public <T> LazyOptional<T> getCapability(
            Capability<T> capability,
            @Nullable Direction side
    ) {
        if (capability
                != SoulBlessingCapability.INVENTORY) {
            return LazyOptional.empty();
        }

        return optional.cast();
    }

    @Override
    public CompoundTag serializeNBT() {
        return inventory.serializeNBT();
    }

    @Override
    public void deserializeNBT(
            CompoundTag tag
    ) {
        inventory.deserializeNBT(
                tag
        );
    }

    public void invalidate() {
        optional.invalidate();
    }
}