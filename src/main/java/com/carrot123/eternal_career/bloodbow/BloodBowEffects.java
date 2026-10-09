package com.carrot123.eternal_career.bloodbow;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BloodBowEffects {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, EternalCareer.MOD_ID);
    public static final RegistryObject<MobEffect> BLOOD_HUNT_MARK = EFFECTS.register("blood_hunt_mark", BloodHuntMarkEffect::new);
    private BloodBowEffects() {}
    public static void register(IEventBus bus) { EFFECTS.register(bus); }
}
