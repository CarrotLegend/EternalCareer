package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

/** Server-side fallback for redemption equipment inserted by commands or nonstandard menus. */
@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RedemptionEquipmentEvents {
    private static final String LEGACY_ACCESS_CACHE_TAG =
            "eternal_career:redemption_access_cached";
    private static final String LEGACY_RECHECK_TICKS_TAG =
            "eternal_career:redemption_recheck_ticks";

    private RedemptionEquipmentEvents() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getEntity().level().isClientSide) {
            clearLegacyCache(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.getEntity().level().isClientSide) {
            clearLegacyCache(event.getEntity());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEquipmentChanged(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getSlot().getType() == EquipmentSlot.Type.ARMOR) {
            ejectArmorSlot(player, event.getSlot());
        }
    }

    /** Runs before Curios' default-priority LivingTickEvent handler. */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || RedemptionAccessController.hasRedemptionAccess(player)) {
            return;
        }

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ejectArmorSlot(player, slot);
            }
        }
        ejectUnauthorizedCurios(player);
    }

    private static void ejectArmorSlot(ServerPlayer player, EquipmentSlot slot) {
        ItemStack equipped = player.getItemBySlot(slot);
        if (!RedemptionAccessController.deny(player, equipped)) {
            return;
        }

        ItemStack returning = equipped.copy();
        player.setItemSlot(slot, ItemStack.EMPTY);
        returnToPlayer(player, returning);
    }

    private static void ejectUnauthorizedCurios(ServerPlayer player) {
        CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
            for (Map.Entry<String, ICurioStacksHandler> entry : inventory.getCurios().entrySet()) {
                ICurioStacksHandler slots = entry.getValue();
                ejectFromHandler(player, slots.getStacks());
                ejectFromHandler(player, slots.getCosmeticStacks());
            }
        });
    }

    private static void ejectFromHandler(ServerPlayer player, IDynamicStackHandler handler) {
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack equipped = handler.getStackInSlot(slot);
            if (!RedemptionAccessController.deny(player, equipped)) {
                continue;
            }

            ItemStack returning = equipped.copy();
            handler.setStackInSlot(slot, ItemStack.EMPTY);
            returnToPlayer(player, returning);
        }
    }

    private static void returnToPlayer(Player player, ItemStack stack) {
        player.getInventory().add(stack);
        if (!stack.isEmpty()) {
            player.drop(stack, false);
        }
    }

    private static void clearLegacyCache(Player player) {
        player.getPersistentData().remove(LEGACY_ACCESS_CACHE_TAG);
        player.getPersistentData().remove(LEGACY_RECHECK_TICKS_TAG);
    }
}
