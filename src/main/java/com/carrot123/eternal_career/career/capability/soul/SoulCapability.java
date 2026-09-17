package com.carrot123.eternal_career.career.capability.soul;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class SoulCapability {
    public static final Capability<ISoul> SOUL = CapabilityManager.get(new CapabilityToken<>() {});
    private SoulCapability() {}
}
