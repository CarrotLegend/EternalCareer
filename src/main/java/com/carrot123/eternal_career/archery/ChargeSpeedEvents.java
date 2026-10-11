package com.carrot123.eternal_career.archery;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.until_eternity.registry.ModAttributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.BowItem;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ChargeSpeedEvents {
    private ChargeSpeedEvents() {
    }

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        if (!(event.getBow().getItem() instanceof BowItem) || event.getCharge() <= 0) {
            return;
        }

        AttributeInstance chargeSpeed = event.getEntity().getAttribute(ModAttributes.CHARGE_SPEED.get());
        if (chargeSpeed == null) {
            return;
        }

        double multiplier = chargeSpeed.getValue();
        if (Double.isFinite(multiplier) && multiplier > 0.0D && multiplier != 1.0D) {
            event.setCharge((int) Math.min(Integer.MAX_VALUE,
                    Math.round(event.getCharge() * multiplier)));
        }
    }
}
