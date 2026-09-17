package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.career.capability.soul.SoulCapability;
import com.carrot123.eternal_career.career.capability.soul.SoulProvider;
import com.carrot123.eternal_career.soul.SoulSetManager;
import com.carrot123.eternal_career.soul.SoulSetReloadListener;
import com.carrot123.eternal_career.soul.SoulSyncManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID)
public final class SoulLifecycleEvents {
    private SoulLifecycleEvents() {}
    @SubscribeEvent public static void attach(AttachCapabilitiesEvent<Entity> event) {
        // The client owns only the HUD snapshot, never an authoritative writable balance.
        if (!(event.getObject() instanceof ServerPlayer player)) return;
        SoulProvider provider = new SoulProvider(() -> SoulSyncManager.mark(player));
        event.addCapability(new ResourceLocation(EternalCareer.MOD_ID, "soul"), provider);
        event.addListener(provider::invalidate);
    }
    @SubscribeEvent public static void clonePlayer(PlayerEvent.Clone event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var original = event.getOriginal();
        original.reviveCaps();
        try {
            original.getCapability(SoulCapability.SOUL).ifPresent(old ->
                    player.getCapability(SoulCapability.SOUL).ifPresent(next -> next.setSoul(old.getSoul())));
        } finally { original.invalidateCaps(); }
        SoulSyncManager.force(player);
    }
    @SubscribeEvent public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) SoulSyncManager.force(player);
    }
    @SubscribeEvent public static void respawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) SoulSyncManager.force(player);
    }
    @SubscribeEvent public static void dimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) SoulSyncManager.force(player);
    }
    @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) SoulSyncManager.forget(player);
    }
    @SubscribeEvent public static void equipment(LivingEquipmentChangeEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getSlot().getType() == net.minecraft.world.entity.EquipmentSlot.Type.ARMOR)
            SoulSyncManager.mark(player);
    }
    @SubscribeEvent public static void reload(AddReloadListenerEvent event) {
        event.addListener(new SoulSetReloadListener());
    }
    @SubscribeEvent public static void tags(TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD)
            SoulSetManager.validateTags(event.getRegistryAccess());
    }
    @SubscribeEvent public static void datapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) SoulSyncManager.force(event.getPlayer());
        else event.getPlayerList().getPlayers().forEach(SoulSyncManager::mark);
    }
    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) SoulSyncManager.flush(event.getServer());
    }
    @SubscribeEvent public static void stopped(ServerStoppedEvent event) {
        SoulSyncManager.clear();
        SoulSetManager.clear();
        SoulCombatEvents.clear();
    }
}
