package com.carrot123.eternal_career.compat.redemption;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Applies tag-driven redemption restrictions at Forge's normal player action boundaries. */
@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RedemptionRestrictionEvents {
    private RedemptionRestrictionEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (RedemptionAccessController.deny(event.getEntity(), event.getItemStack())) {
            failInteraction(event);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (RedemptionAccessController.deny(event.getEntity(), event.getItemStack())) {
            // Preserve block activation while suppressing Item#onItemUseFirst and Item#useOn.
            event.setUseItem(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (RedemptionAccessController.deny(event.getEntity(), event.getItemStack())) {
            failInteraction(event);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (RedemptionAccessController.deny(event.getEntity(), event.getItemStack())) {
            failInteraction(event);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntity(AttackEntityEvent event) {
        if (RedemptionAccessController.deny(event.getEntity(), event.getEntity().getMainHandItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity().level().isClientSide
                || !(event.getSource().getEntity() instanceof Player player)
                || event.getSource().getDirectEntity() != player
                || !event.getSource().is(DamageTypes.PLAYER_ATTACK)) {
            return;
        }
        if (RedemptionAccessController.deny(player, player.getMainHandItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (RedemptionAccessController.deny(event.getEntity(), event.getItemStack())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (RedemptionAccessController.deny(event.getEntity(), event.getEntity().getMainHandItem())) {
            event.setNewSpeed(0.0F);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (RedemptionAccessController.deny(event.getPlayer(), event.getPlayer().getMainHandItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player
                && RedemptionAccessController.deny(player, event.getItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseTick(LivingEntityUseItemEvent.Tick event) {
        if (event.getEntity() instanceof Player player
                && RedemptionAccessController.deny(player, event.getItem())) {
            event.setDuration(0);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player player
                && RedemptionAccessController.deny(player, event.getItem())) {
            event.setCanceled(true);
        }
    }

    private static void failInteraction(PlayerInteractEvent event) {
        event.setCancellationResult(InteractionResult.FAIL);
        event.setCanceled(true);
    }
}
