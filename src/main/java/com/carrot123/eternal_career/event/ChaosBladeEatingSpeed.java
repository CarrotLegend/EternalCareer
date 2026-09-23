package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ChaosBladeEatingSpeed {
    private ChaosBladeEatingSpeed() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack stack = event.getItem();
        if (!stack.isEdible() || stack.is(ModItems.CHAOS_MEAT.get())
                || stack.is(ModItems.CHAOS_STEAK.get())
                || !CurioEquipmentHelper.hasChaosBlade(player)) {
            return;
        }
        int duration = event.getDuration();
        if (duration > 0) {
            event.setDuration(adjustedDuration(duration));
        }
    }

    static int adjustedDuration(int originalDuration) {
        return (int) Math.ceil(originalDuration / 1.5D);
    }
}
