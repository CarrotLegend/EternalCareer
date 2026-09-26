package com.carrot123.eternal_career.soulblessing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class SoulBlessingCapability {
    public static final Capability<SoulBlessingInventory> INVENTORY =
            CapabilityManager.get(new CapabilityToken<>() {});

    private SoulBlessingCapability() {}
}
