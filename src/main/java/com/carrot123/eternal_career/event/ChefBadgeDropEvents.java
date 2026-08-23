package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import com.carrot123.eternal_career.loot.ChefBadgeDropManager;
import com.carrot123.eternal_career.loot.ChefBadgeDropRule;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class ChefBadgeDropEvents {

    private ChefBadgeDropEvents() {
    }

    @SubscribeEvent
    public static void onAddReloadListener(
            AddReloadListenerEvent event
    ) {
        event.addListener(
                ChefBadgeDropManager.INSTANCE
        );
    }

    @SubscribeEvent
    public static void onLivingDrops(
            LivingDropsEvent event
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
                .hasHeadChefSheath(player)) {
            return;
        }

        for (ChefBadgeDropRule rule :
                ChefBadgeDropManager.INSTANCE
                        .getRules()) {

            if (!rule.matches(
                    event.getEntity()
            )) {
                continue;
            }

            if (event.getEntity()
                    .getRandom()
                    .nextFloat()
                    >= rule.chance()) {
                continue;
            }

            ItemStack stack =
                    new ItemStack(
                            rule.badge(),
                            rule.count()
                    );

            ItemEntity drop =
                    new ItemEntity(
                            event.getEntity()
                                    .level(),
                            event.getEntity().getX(),
                            event.getEntity().getY(),
                            event.getEntity().getZ(),
                            stack
                    );

            event.getDrops().add(drop);
        }
    }
}