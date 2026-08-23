package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModEffects;

import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class IngredientMarkApplicableEvents {

    private IngredientMarkApplicableEvents() {
    }

    @SubscribeEvent
    public static void onEffectApplicable(
            MobEffectEvent.Applicable event
    ) {
        if (event.getEffectInstance().getEffect()
                != ModEffects.INGREDIENT_MARK.get()) {
            return;
        }

        event.setResult(Event.Result.ALLOW);
    }
}