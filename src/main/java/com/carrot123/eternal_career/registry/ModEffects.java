package com.carrot123.eternal_career.registry;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.effect.IngredientMarkEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Eternal Career mob-effect registrations. */
public final class ModEffects {
    private static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, EternalCareer.MOD_ID);

    public static final RegistryObject<MobEffect> INGREDIENT_MARK =
            EFFECTS.register("ingredient_mark", IngredientMarkEffect::new);

    private ModEffects() {
    }

    public static void register(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
    }
}
