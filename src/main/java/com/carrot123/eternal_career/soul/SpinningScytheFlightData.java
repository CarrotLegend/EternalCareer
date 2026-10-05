package com.carrot123.eternal_career.soul;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public final class SpinningScytheFlightData {

    private static final String ROOT_KEY =
            "eternal_career:spinning_scythe";

    private static final String FLIGHT_ID =
            "FlightId";

    private static final String ITEM =
            "Item";

    private static final String SLOT =
            "Slot";

    private static final String EXPIRE_TIME =
            "ExpireTime";

    private static final long RECOVERY_DELAY =
            20L * 15L;

    private SpinningScytheFlightData() {
    }

    public static UUID begin(
            ServerPlayer player,
            ItemStack stack,
            int slot
    ) {
        UUID flightId =
                UUID.randomUUID();

        CompoundTag data =
                new CompoundTag();

        data.putUUID(
                FLIGHT_ID,
                flightId
        );

        data.putInt(
                SLOT,
                slot
        );

        data.putLong(
                EXPIRE_TIME,
                player.level().getGameTime()
                        + RECOVERY_DELAY
        );

        putItem(
                data,
                stack
        );

        player.getPersistentData().put(
                ROOT_KEY,
                data
        );

        return flightId;
    }

    public static boolean hasActiveFlight(
            ServerPlayer player
    ) {
        return getData(player) != null;
    }

    public static boolean matches(
            ServerPlayer player,
            UUID flightId
    ) {
        CompoundTag data =
                getData(player);

        return data != null
                && flightId != null
                && data.hasUUID(FLIGHT_ID)
                && flightId.equals(
                        data.getUUID(FLIGHT_ID)
                );
    }

    public static void updateItem(
            ServerPlayer player,
            UUID flightId,
            ItemStack stack
    ) {
        CompoundTag data =
                getData(player);

        if (data == null
                || !matches(player, flightId)) {
            return;
        }

        putItem(
                data,
                stack
        );

        player.getPersistentData().put(
                ROOT_KEY,
                data
        );
    }

    public static void complete(
            ServerPlayer player,
            UUID flightId,
            ItemStack returnedStack
    ) {
        CompoundTag data =
                getData(player);

        if (data == null
                || !matches(player, flightId)) {
            return;
        }

        int slot =
                data.getInt(SLOT);

        player.getPersistentData().remove(
                ROOT_KEY
        );

        restoreStack(
                player,
                slot,
                returnedStack
        );
    }

    public static void recoverIfExpired(
            ServerPlayer player
    ) {
        CompoundTag data =
                getData(player);

        if (data == null) {
            return;
        }

        if (player.level().getGameTime()
                < data.getLong(EXPIRE_TIME)) {
            return;
        }

        recoverImmediately(player);
    }

    public static void recoverImmediately(
            ServerPlayer player
    ) {
        CompoundTag data =
                getData(player);

        if (data == null) {
            return;
        }

        int slot =
                data.getInt(SLOT);

        ItemStack stack =
                ItemStack.of(
                        data.getCompound(ITEM)
                );

        player.getPersistentData().remove(
                ROOT_KEY
        );

        restoreStack(
                player,
                slot,
                stack
        );
    }

    public static void copy(
            ServerPlayer original,
            ServerPlayer clone
    ) {
        CompoundTag data =
                getData(original);

        if (data == null) {
            return;
        }

        clone.getPersistentData().put(
                ROOT_KEY,
                data.copy()
        );
    }

    private static void restoreStack(
            ServerPlayer player,
            int slot,
            ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return;
        }

        Inventory inventory =
                player.getInventory();

        if (slot >= 0
                && slot < inventory.getContainerSize()
                && inventory.getItem(slot).isEmpty()) {
            inventory.setItem(
                    slot,
                    stack.copy()
            );
        } else {
            ItemHandlerHelper.giveItemToPlayer(
                    player,
                    stack.copy()
            );
        }

        player.containerMenu.broadcastChanges();
    }

    private static void putItem(
            CompoundTag data,
            ItemStack stack
    ) {
        CompoundTag itemTag =
                new CompoundTag();

        stack.save(
                itemTag
        );

        data.put(
                ITEM,
                itemTag
        );
    }

    private static CompoundTag getData(
            ServerPlayer player
    ) {
        CompoundTag persistent =
                player.getPersistentData();

        if (!persistent.contains(
                ROOT_KEY,
                Tag.TAG_COMPOUND
        )) {
            return null;
        }

        return persistent.getCompound(
                ROOT_KEY
        );
    }
}