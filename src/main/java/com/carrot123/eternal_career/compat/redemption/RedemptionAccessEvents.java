package com.carrot123.eternal_career.compat.redemption;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

/** Maintains the existing persistent redemption cache across player initialization races. */
@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RedemptionAccessEvents {
    public static final int INITIALIZATION_RECHECK_TICKS = 20;
    private static final int CURIO_CHANGE_RECHECK_TICKS = 1;

    private RedemptionAccessEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) {
            return;
        }
        RedemptionAccessController.initializeCacheIfAbsent(player);
        RedemptionAccessController.scheduleRecheck(player, INITIALIZATION_RECHECK_TICKS);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player clone = event.getEntity();
        if (clone.level().isClientSide) {
            return;
        }
        RedemptionAccessController.copyCacheState(event.getOriginal(), clone);
        RedemptionAccessController.initializeCacheIfAbsent(clone);
        RedemptionAccessController.scheduleRecheck(clone, INITIALIZATION_RECHECK_TICKS);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (!event.getEntity().level().isClientSide) {
            RedemptionAccessController.scheduleRecheck(
                    event.getEntity(), INITIALIZATION_RECHECK_TICKS);
        }
    }

    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        if (event.getEntity() instanceof Player player && !player.level().isClientSide) {
            // Math.max in scheduleRecheck prevents Curios restoration from shortening login grace.
            RedemptionAccessController.scheduleRecheck(player, CURIO_CHANGE_RECHECK_TICKS);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level().isClientSide) {
            RedemptionAccessController.advanceRecheck(event.player);
        }
    }
}
