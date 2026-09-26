package com.carrot123.eternal_career.soulblessing;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.network.ModNetwork;
import com.carrot123.eternal_career.network.SoulBlessingSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID)
public final class SoulBlessingLifecycleEvents {
    private SoulBlessingLifecycleEvents() {}

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player player)) return;
        SoulBlessingProvider provider = new SoulBlessingProvider(() -> {
            if (player instanceof ServerPlayer serverPlayer) {
                SoulBlessingModifiers.reconcile(serverPlayer);
                sync(serverPlayer);
            }
        });
        event.addCapability(new ResourceLocation(EternalCareer.MOD_ID, "soul_blessing"), provider);
        event.addListener(provider::invalidate);
    }

    @SubscribeEvent
    public static void clonePlayer(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        boolean keep = !event.isWasDeath() || player.level().getGameRules()
                .getBoolean(GameRules.RULE_KEEPINVENTORY);
        if (keep) {
            Player original = event.getOriginal();
            original.reviveCaps();
            try {
                original.getCapability(SoulBlessingCapability.INVENTORY).ifPresent(old ->
                        player.getCapability(SoulBlessingCapability.INVENTORY).ifPresent(current ->
                                current.deserializeNBT(old.serializeNBT())));
            } finally {
                original.invalidateCaps();
            }
        }
        returnLegacyNecklace(player);
        SoulBlessingModifiers.reconcile(player);
        sync(player);
    }

    @SubscribeEvent
    public static void drops(LivingDropsEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;
        SoulBlessingInventory inventory = SoulBlessingHelper.inventory(player);
        ItemStack legacy = inventory.takeLegacyNecklace();
        if (!legacy.isEmpty() && !EnchantmentHelper.hasVanishingCurse(legacy)) {
            event.getDrops().add(new ItemEntity(player.level(), player.getX(), player.getY(),
                    player.getZ(), legacy));
        }
        for (int i = 0; i < SoulBlessingSlots.COUNT; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (!EnchantmentHelper.hasVanishingCurse(stack)) {
                event.getDrops().add(new ItemEntity(player.level(), player.getX(), player.getY(),
                        player.getZ(), stack.copy()));
            }
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent event) { refresh(event.getEntity()); }

    @SubscribeEvent
    public static void respawn(PlayerEvent.PlayerRespawnEvent event) { refresh(event.getEntity()); }

    @SubscribeEvent
    public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) { refresh(event.getEntity()); }

    private static void refresh(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            returnLegacyNecklace(serverPlayer);
            SoulBlessingModifiers.reconcile(serverPlayer);
            sync(serverPlayer);
        }
    }

    private static void returnLegacyNecklace(ServerPlayer player) {
        ItemStack legacy = SoulBlessingHelper.inventory(player).takeLegacyNecklace();
        if (legacy.isEmpty()) return;
        player.getInventory().add(legacy);
        if (!legacy.isEmpty()) player.drop(legacy, false);
    }

    public static void sync(ServerPlayer player) {
        ModNetwork.send(player, SoulBlessingSyncPacket.fromInventory(
                SoulBlessingHelper.inventory(player)));
    }
}
