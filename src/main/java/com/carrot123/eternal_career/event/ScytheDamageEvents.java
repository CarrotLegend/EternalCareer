package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModAttributes;
import com.carrot123.eternal_career.soul.ScytheCombat;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID)
public final class ScytheDamageEvents {
    private ScytheDamageEvents() {}
    @SubscribeEvent
    public static void onHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide || event.getAmount() <= 0) return;
        var player = ScytheCombat.directAttacker(event.getSource());
        if (player == null) return;
        event.setAmount(ScytheCombat.scaledDamage(event.getAmount(),
                player.getAttributeValue(ModAttributes.SCYTHE_DAMAGE.get())));
    }
}
