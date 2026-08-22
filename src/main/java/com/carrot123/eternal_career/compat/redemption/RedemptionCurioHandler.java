package com.carrot123.eternal_career.compat.redemption;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

/** Adds the redemption condition without overriding any item's original Curios rules. */
@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RedemptionCurioHandler {
    private RedemptionCurioHandler() {
    }

    @SubscribeEvent
    public static void onCurioEquip(CurioEquipEvent event) {
        if (event.getEntity() instanceof Player player
                && !RedemptionItemHelper.canUseRedemptionItem(player, event.getStack())) {
            event.setResult(Event.Result.DENY);
        }
    }
}
