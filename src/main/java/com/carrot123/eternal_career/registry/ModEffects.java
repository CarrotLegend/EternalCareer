package com.carrot123.eternal_career.registry;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.effect.BreakStanceEffect;
import com.carrot123.eternal_career.effect.ChaoticCookingEffect;
import com.carrot123.eternal_career.effect.FeastSatisfactionEffect;
import com.carrot123.eternal_career.effect.GodBurstEffect;
import com.carrot123.eternal_career.effect.IngredientMarkEffect;
import com.carrot123.eternal_career.effect.PerfectStateEffect;
import com.carrot123.eternal_career.effect.SweetImpulseEffect;
import com.carrot123.eternal_career.effect.WitchCriticalEffect;
import com.carrot123.eternal_career.effect.WitchDomainEffect;
import com.carrot123.eternal_career.effect.WitchSuperCriticalEffect;
import com.carrot123.eternal_career.effect.WitchSuperDomainEffect;
import com.carrot123.eternal_career.effect.WitchUltimateCriticalEffect;
import com.carrot123.eternal_career.effect.WitchUltimateDomainEffect;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {

    private static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(
                    ForgeRegistries.MOB_EFFECTS,
                    EternalCareer.MOD_ID
            );

    public static final RegistryObject<MobEffect> INGREDIENT_MARK =
            EFFECTS.register(
                    "ingredient_mark",
                    IngredientMarkEffect::new
            );

    public static final RegistryObject<MobEffect> BREAK_STANCE =
            EFFECTS.register(
                    "break_stance",
                    BreakStanceEffect::new
            );

    public static final RegistryObject<MobEffect> GOD_BURST =
            EFFECTS.register(
                    "god_burst",
                    GodBurstEffect::new
            );

    public static final RegistryObject<MobEffect> CHAOTIC_COOKING =
            EFFECTS.register(
                    "chaotic_cooking",
                    ChaoticCookingEffect::new
            );

    public static final RegistryObject<MobEffect> SWEET_IMPULSE =
            EFFECTS.register(
                    "sweet_impulse",
                    SweetImpulseEffect::new
            );

    public static final RegistryObject<MobEffect> FEAST_SATISFACTION =
            EFFECTS.register(
                    "feast_satisfaction",
                    FeastSatisfactionEffect::new
            );

    public static final RegistryObject<MobEffect> WITCH_CRITICAL =
            EFFECTS.register("witch_critical", WitchCriticalEffect::new);

    public static final RegistryObject<MobEffect> WITCH_DOMAIN =
            EFFECTS.register("witch_domain", WitchDomainEffect::new);

    public static final RegistryObject<MobEffect> WITCH_SUPER_CRITICAL =
            EFFECTS.register("witch_super_critical", WitchSuperCriticalEffect::new);

    public static final RegistryObject<MobEffect> WITCH_SUPER_DOMAIN =
            EFFECTS.register("witch_super_domain", WitchSuperDomainEffect::new);

    public static final RegistryObject<MobEffect> WITCH_ULTIMATE_CRITICAL =
            EFFECTS.register("witch_ultimate_critical", WitchUltimateCriticalEffect::new);

    public static final RegistryObject<MobEffect> WITCH_ULTIMATE_DOMAIN =
            EFFECTS.register("witch_ultimate_domain", WitchUltimateDomainEffect::new);

    public static final RegistryObject<MobEffect> PERFECT_STATE =
            EFFECTS.register("perfect_state", PerfectStateEffect::new);

    private ModEffects() {
    }

    public static void register(
            IEventBus modEventBus
    ) {
        EFFECTS.register(modEventBus);
    }

    public static void bindChaoticCookingAttributes() {
        ((ChaoticCookingEffect) CHAOTIC_COOKING.get())
                .bindAttributes();
    }
}
