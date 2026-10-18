package com.carrot123.eternal_career.compat.goety;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.carrot123.eternal_career.EternalCareer;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CroneHatBrewEvents {
    private static final Map<LivingEntity, Integer> BOTTLES_BEFORE_USE = new WeakHashMap<>();

    private CroneHatBrewEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        rememberBottles(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onUseTick(LivingEntityUseItemEvent.Tick event) {
        rememberBottles(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onUseFinish(LivingEntityUseItemEvent.Finish event) {
        Integer bottlesBefore = BOTTLES_BEFORE_USE.remove(event.getEntity());
        if (event.getEntity().level().isClientSide()
                || !(event.getItem().getItem() instanceof BrewItem)
                || !CuriosFinder.hasCurio(event.getEntity(), ModItems.CRONE_HAT.get())) {
            return;
        }

        event.setResultStack(event.getItem().copy());
        if (event.getEntity() instanceof Player player
                && !player.getAbilities().instabuild
                && event.getItem().getCount() > 1
                && bottlesBefore != null
                && countBottles(player) > bottlesBefore) {
            removeOneBottle(player);
        }
    }

    @SubscribeEvent
    public static void onUseStop(LivingEntityUseItemEvent.Stop event) {
        BOTTLES_BEFORE_USE.remove(event.getEntity());
    }

    private static void rememberBottles(LivingEntityUseItemEvent event) {
        if (event.getEntity() instanceof Player player
                && !player.level().isClientSide()
                && event.getItem().getItem() instanceof BrewItem
                && CuriosFinder.hasCurio(player, ModItems.CRONE_HAT.get())) {
            BOTTLES_BEFORE_USE.put(player, countBottles(player));
        }
    }

    private static int countBottles(Player player) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.GLASS_BOTTLE) && !stack.hasTag()) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static void removeOneBottle(Player player) {
        for (int index = 0; index < player.getInventory().items.size(); index++) {
            ItemStack stack = player.getInventory().items.get(index);
            if (stack.is(Items.GLASS_BOTTLE) && !stack.hasTag()) {
                player.getInventory().removeItem(index, 1);
                player.getInventory().setChanged();
                return;
            }
        }
    }
}
