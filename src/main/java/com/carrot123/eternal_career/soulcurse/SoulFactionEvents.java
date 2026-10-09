package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
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
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioEquipEvent;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SoulFactionEvents {
    private SoulFactionEvents() {}
    private static boolean deny(Player player, ItemStack stack) { return !SoulFaction.canUse(player, stack); }
    private static void fail(PlayerInteractEvent event) { event.setCancellationResult(InteractionResult.FAIL); event.setCanceled(true); }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onCurioEquip(CurioEquipEvent event) {
        if (event.getEntity() instanceof Player player && !SoulFaction.canEquip(player, event.getStack())) event.setResult(Event.Result.DENY);
    }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) { if (deny(event.getEntity(), event.getItemStack())) fail(event); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) { if (deny(event.getEntity(), event.getItemStack())) event.setUseItem(Event.Result.DENY); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) { if (deny(event.getEntity(), event.getItemStack())) fail(event); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) { if (deny(event.getEntity(), event.getItemStack())) fail(event); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttack(AttackEntityEvent event) { if (deny(event.getEntity(), event.getEntity().getMainHandItem())) event.setCanceled(true); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingAttack(LivingAttackEvent event) { if (event.getSource().getEntity() instanceof Player player && deny(player, player.getMainHandItem())) event.setCanceled(true); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) { if (deny(event.getEntity(), event.getItemStack())) event.setCanceled(true); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) { if (deny(event.getEntity(), event.getEntity().getMainHandItem())) { event.setNewSpeed(0); event.setCanceled(true); } }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBreakBlock(BlockEvent.BreakEvent event) { if (event.getPlayer() != null && deny(event.getPlayer(), event.getPlayer().getMainHandItem())) event.setCanceled(true); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseStart(LivingEntityUseItemEvent.Start event) { if (event.getEntity() instanceof Player player && deny(player, event.getItem())) event.setCanceled(true); }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseTick(LivingEntityUseItemEvent.Tick event) { if (event.getEntity() instanceof Player player && deny(player, event.getItem())) { event.setDuration(0); event.setCanceled(true); } }
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseStop(LivingEntityUseItemEvent.Stop event) { if (event.getEntity() instanceof Player player && deny(player, event.getItem())) event.setCanceled(true); }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level().isClientSide || player.tickCount % 20 != 0) return;
        boolean soul = SoulFaction.hasCore(player);
        CuriosApi.getCuriosInventory(player).ifPresent(handler -> handler.getCurios().forEach((id, slot) -> {
            for (int i = 0; i < slot.getStacks().getSlots(); i++) {
                ItemStack stack = slot.getStacks().getStackInSlot(i);
                if (stack.isEmpty() || SoulFaction.isCore(stack)) continue;
                if ((soul && SoulFaction.isForeign(stack)) || (!soul && SoulFaction.isSoulItem(stack))) {
                    ItemStack rescued = stack.copy();
                    handler.setEquippedCurio(id, i, ItemStack.EMPTY);
                    player.getInventory().add(rescued);
                    if (!rescued.isEmpty()) player.drop(rescued, false);
                }
            }
        }));
        for (int i = 0; i < player.getInventory().armor.size(); i++) {
            ItemStack stack = player.getInventory().armor.get(i);
            if (stack.isEmpty() || !((soul && SoulFaction.isForeign(stack)) || (!soul && SoulFaction.isSoulItem(stack)))) continue;
            ItemStack rescued = stack.copy();
            player.getInventory().armor.set(i, ItemStack.EMPTY);
            player.getInventory().add(rescued);
            if (!rescued.isEmpty()) player.drop(rescued, false);
        }
    }
}
