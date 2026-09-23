package com.carrot123.eternal_career.lich;

import com.Polarice3.Goety.utils.LichdomHelper;
import com.carrot123.eternal_career.EternalCareer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PanaceaUsageEvents {
    private static final ResourceLocation PANACEA = new ResourceLocation("goety", "undeath_potion");
    private static final Map<UUID, Boolean> STARTED_AS_GOETY_LICH = new ConcurrentHashMap<>();

    private PanaceaUsageEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof ServerPlayer player && isPanacea(event.getItem())
                && !event.isCanceled()) {
            STARTED_AS_GOETY_LICH.put(player.getUUID(), LichdomHelper.isLich(player));
        }
    }

    @SubscribeEvent
    public static void onFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player && isPanacea(event.getItem())) {
            Boolean wasGoetyLich = STARTED_AS_GOETY_LICH.remove(player.getUUID());
            if (Boolean.FALSE.equals(wasGoetyLich) && LichdomHelper.isLich(player)) {
                LichUtils.markPanaceaUsed(player);
            }
        }
    }

    @SubscribeEvent
    public static void onStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof ServerPlayer player && isPanacea(event.getItem())) {
            STARTED_AS_GOETY_LICH.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        STARTED_AS_GOETY_LICH.remove(event.getEntity().getUUID());
    }

    private static boolean isPanacea(ItemStack stack) {
        return stack != null && !stack.isEmpty()
                && PANACEA.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()));
    }
}
