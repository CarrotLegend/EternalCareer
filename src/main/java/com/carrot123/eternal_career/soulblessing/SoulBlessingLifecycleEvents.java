package com.carrot123.eternal_career.soulblessing;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.corpse.CorpseSoulBlessingTransferHelper;
import com.carrot123.eternal_career.network.ModNetwork;
import com.carrot123.eternal_career.network.SoulBlessingSyncPacket;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class SoulBlessingLifecycleEvents {

    private static final Map<UUID, CompoundTag>
            DEATH_SNAPSHOTS =
            new ConcurrentHashMap<>();

    private SoulBlessingLifecycleEvents() {
    }

    @SubscribeEvent
    public static void attach(
            AttachCapabilitiesEvent<Entity> event
    ) {
        if (!(event.getObject()
                instanceof Player player)) {
            return;
        }

        SoulBlessingProvider provider =
                new SoulBlessingProvider(() -> {
                    if (!(player
                            instanceof ServerPlayer serverPlayer)) {
                        return;
                    }

                    if (serverPlayer.isRemoved()) {
                        return;
                    }

                    serverPlayer
                            .getCapability(
                                    SoulBlessingCapability.INVENTORY
                            )
                            .ifPresent(inventory -> {
                                SoulBlessingModifiers.reconcile(
                                        serverPlayer,
                                        inventory
                                );

                                sync(
                                        serverPlayer,
                                        inventory
                                );
                            });
                });

        event.addCapability(
                new ResourceLocation(
                        EternalCareer.MOD_ID,
                        "soul_blessing"
                ),
                provider
        );

        event.addListener(
                provider::invalidate
        );
    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public static void onLivingDeath(
            LivingDeathEvent event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        player.getCapability(
                        SoulBlessingCapability.INVENTORY
                )
                .ifPresent(inventory ->
                        DEATH_SNAPSHOTS.put(
                                player.getUUID(),
                                inventory.serializeNBT()
                                        .copy()
                        )
                );
    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public static void onLivingDrops(
            LivingDropsEvent event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        if (player.level()
                .getGameRules()
                .getBoolean(
                        GameRules.RULE_KEEPINVENTORY
                )) {
            return;
        }

        CompoundTag snapshot =
                DEATH_SNAPSHOTS.remove(
                        player.getUUID()
                );

        if (snapshot == null) {
            return;
        }

        List<ItemStack> drops =
                createSoulBlessingDrops(
                        snapshot
                );

        for (ItemStack stack : drops) {
            if (stack.isEmpty()) {
                continue;
            }

            ItemEntity entity =
                    new ItemEntity(
                            player.level(),
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            stack
                    );

            entity.setDefaultPickUpDelay();

            event.getDrops().add(
                    entity
            );
        }
    }

    @SubscribeEvent
    public static void clonePlayer(
            PlayerEvent.Clone event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        if (!event.isWasDeath()) {
            copyFromOriginal(
                    event.getOriginal(),
                    player
            );

            refresh(
                    player
            );

            return;
        }

        boolean keepInventory =
                player.level()
                        .getGameRules()
                        .getBoolean(
                                GameRules.RULE_KEEPINVENTORY
                        );

        if (keepInventory) {
            CompoundTag snapshot =
                    DEATH_SNAPSHOTS.remove(
                            player.getUUID()
                    );

            if (snapshot != null) {
                player.getCapability(
                                SoulBlessingCapability.INVENTORY
                        )
                        .ifPresent(inventory ->
                                inventory.deserializeNBT(
                                        snapshot.copy()
                                )
                        );
            } else {
                copyFromOriginal(
                        event.getOriginal(),
                        player
                );
            }
        } else {
            DEATH_SNAPSHOTS.remove(
                    player.getUUID()
            );
        }

        refresh(
                player
        );
    }

    @SubscribeEvent
    public static void login(
            PlayerEvent.PlayerLoggedInEvent event
    ) {
        refresh(
                event.getEntity()
        );
    }

    @SubscribeEvent
    public static void respawn(
            PlayerEvent.PlayerRespawnEvent event
    ) {
        refresh(
                event.getEntity()
        );
    }

    @SubscribeEvent
    public static void dimension(
            PlayerEvent.PlayerChangedDimensionEvent event
    ) {
        refresh(
                event.getEntity()
        );
    }

    @SubscribeEvent
    public static void logout(
            PlayerEvent.PlayerLoggedOutEvent event
    ) {
        if (!event.getEntity()
                .isDeadOrDying()) {
            DEATH_SNAPSHOTS.remove(
                    event.getEntity()
                            .getUUID()
            );
        }
    }

    private static List<ItemStack>
    createSoulBlessingDrops(
            CompoundTag snapshot
    ) {
        SoulBlessingInventory inventory =
                new SoulBlessingInventory(
                        () -> {
                        }
                );

        inventory.deserializeNBT(
                snapshot.copy()
        );

        List<ItemStack> result =
                new ArrayList<>();

        ItemStack legacy =
                inventory.takeLegacyNecklace();

        if (!legacy.isEmpty()
                && !EnchantmentHelper
                .hasVanishingCurse(
                        legacy
                )) {

            result.add(
                    legacy.copy()
            );
        }

        boolean corpseLoaded =
                ModList.get()
                        .isLoaded(
                                "corpse"
                        );

        for (int slot = 0;
             slot < SoulBlessingSlots.COUNT;
             slot++) {

            ItemStack stack =
                    inventory.getStackInSlot(
                            slot
                    );

            if (stack.isEmpty()) {
                continue;
            }

            if (EnchantmentHelper
                    .hasVanishingCurse(
                            stack
                    )) {
                continue;
            }

            ItemStack dropped =
                    stack.copy();

            dropped.setCount(
                    1
            );

            if (corpseLoaded) {
                CorpseSoulBlessingTransferHelper
                        .markDeathSlot(
                                dropped,
                                slot
                        );
            }

            result.add(
                    dropped
            );
        }

        return result;
    }

    private static void copyFromOriginal(
            Player original,
            ServerPlayer player
    ) {
        original.reviveCaps();

        try {
            original.getCapability(
                            SoulBlessingCapability.INVENTORY
                    )
                    .ifPresent(oldInventory ->
                            player.getCapability(
                                            SoulBlessingCapability.INVENTORY
                                    )
                                    .ifPresent(newInventory ->
                                            newInventory.deserializeNBT(
                                                    oldInventory
                                                            .serializeNBT()
                                            )
                                    )
                    );
        } finally {
            original.invalidateCaps();
        }
    }

    private static void refresh(
            Player player
    ) {
        if (!(player
                instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (serverPlayer.isRemoved()) {
            return;
        }

        serverPlayer.getCapability(
                        SoulBlessingCapability.INVENTORY
                )
                .ifPresent(inventory -> {
                    returnLegacyNecklace(
                            serverPlayer,
                            inventory
                    );

                    SoulBlessingModifiers.reconcile(
                            serverPlayer,
                            inventory
                    );

                    sync(
                            serverPlayer,
                            inventory
                    );
                });
    }

    private static void returnLegacyNecklace(
            ServerPlayer player,
            SoulBlessingInventory inventory
    ) {
        ItemStack legacy =
                inventory.takeLegacyNecklace();

        if (legacy.isEmpty()) {
            return;
        }

        player.getInventory().add(
                legacy
        );

        if (!legacy.isEmpty()) {
            player.drop(
                    legacy,
                    false
            );
        }
    }

    public static void sync(
            ServerPlayer player
    ) {
        if (player == null
                || player.isRemoved()) {
            return;
        }

        player.getCapability(
                        SoulBlessingCapability.INVENTORY
                )
                .ifPresent(inventory ->
                        sync(
                                player,
                                inventory
                        )
                );
    }

    public static void sync(
            ServerPlayer player,
            SoulBlessingInventory inventory
    ) {
        if (player == null
                || inventory == null
                || player.isRemoved()) {
            return;
        }

        ModNetwork.send(
                player,
                SoulBlessingSyncPacket
                        .fromInventory(
                                inventory
                        )
        );
    }
}