package com.carrot123.eternal_career.compat.redemption;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/** Prevents active use of the external True Chef's Knife without redemption. */
@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RedemptionUsageEvents {
    public static final ResourceLocation TRUE_CHEFS_KNIFE_ID =
            new ResourceLocation("until_eternity", "true_chefs_knife");

    private RedemptionUsageEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntity(AttackEntityEvent event) {
        if (isRestrictedKnife(event.getEntity(), event.getEntity().getMainHandItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (isRestrictedKnife(event.getEntity(), event.getItemStack())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (isRestrictedKnife(event.getEntity(), event.getItemStack())) {
            event.setUseBlock(Event.Result.DENY);
            event.setUseItem(Event.Result.DENY);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.FAIL);
        }
    }

    private static boolean isRestrictedKnife(Player player, ItemStack stack) {
        return TRUE_CHEFS_KNIFE_ID.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()))
                && !RedemptionItemHelper.canUseRedemptionItem(player, stack);
    }
}
