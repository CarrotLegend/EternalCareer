package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.career.api.DamageCategoryHelper;
import com.carrot123.eternal_career.career.api.PlayerDamageCategory;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class CookingMagicHandDamageEvents {

    private CookingMagicHandDamageEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(
            LivingAttackEvent event
    ) {
        if (event.getEntity()
                .level()
                .isClientSide) {
            return;
        }

        if (!(event.getSource()
                .getEntity()
                instanceof Player player)) {
            return;
        }

        if (!CurioEquipmentHelper
                .hasCookingMagicHand(player)) {
            return;
        }

        PlayerDamageCategory category =
                DamageCategoryHelper.classify(
                        player,
                        event.getSource()
                );

        if (category
                != PlayerDamageCategory.KITCHENWARE) {
            event.setCanceled(true);
        }
    }
}