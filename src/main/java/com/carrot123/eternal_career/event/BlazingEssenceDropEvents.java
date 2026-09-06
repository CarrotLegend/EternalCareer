package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.redemption.RedemptionItemHelper;
import com.carrot123.eternal_career.registry.ModItems;

import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Adds the Ring of Redemption's exclusive Wither drop. */
@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class BlazingEssenceDropEvents {

    private BlazingEssenceDropEvents() {
    }

    @SubscribeEvent
    public static void onLivingDrops(
            LivingDropsEvent event
    ) {
        if (event.getEntity().level().isClientSide
                || !event.isRecentlyHit()
                || !(event.getEntity() instanceof WitherBoss wither)
                || !(event.getSource().getEntity() instanceof Player player)
                || !RedemptionItemHelper.findRingOfRedemptionNow(player)) {
            return;
        }

        ItemStack stack = new ItemStack(
                ModItems.BLAZING_ESSENCE.get(),
                1 + wither.getRandom().nextInt(4)
        );
        ItemEntity drop = new ItemEntity(
                wither.level(),
                wither.getX(),
                wither.getY(),
                wither.getZ(),
                stack
        );
        drop.setPickUpDelay(10);
        event.getDrops().add(drop);
    }
}
