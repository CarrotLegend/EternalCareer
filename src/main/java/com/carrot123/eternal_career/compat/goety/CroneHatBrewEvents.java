package com.carrot123.eternal_career.compat.goety;

import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.carrot123.eternal_career.EternalCareer;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CroneHatBrewEvents {
    private record SavedUse(ItemStack original, int bottlesBefore) {}
    private static final Map<LivingEntity, SavedUse> ACTIVE = new WeakHashMap<>();
    private CroneHatBrewEvents() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide() && entity instanceof Player player
                && event.getItem().getItem() instanceof BrewItem
                && hasCroneHat(player)) {
            ACTIVE.put(entity, new SavedUse(event.getItem().copy(), countBottles(player)));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onUseFinish(LivingEntityUseItemEvent.Finish event) {
        SavedUse saved = ACTIVE.remove(event.getEntity());
        if (saved == null || event.getEntity().level().isClientSide()
                || !(event.getEntity() instanceof Player player)
                || !hasCroneHat(player)) return;

        event.setResultStack(saved.original().copy());
        if (!player.getAbilities().instabuild && countBottles(player) > saved.bottlesBefore()) removeOneBottle(player);
    }

    @SubscribeEvent
    public static void onUseStop(LivingEntityUseItemEvent.Stop event) { ACTIVE.remove(event.getEntity()); }

    public static boolean hasCroneHat(LivingEntity entity) {
        return entity != null && (CuriosFinder.hasCurio(entity, ModItems.CRONE_HAT.get())
                || entity.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.CRONE_HAT.get()));
    }

    private static int countBottles(Player player) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.GLASS_BOTTLE) && !stack.hasTag()) count += stack.getCount();
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
