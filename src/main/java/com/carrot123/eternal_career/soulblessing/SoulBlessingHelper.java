package com.carrot123.eternal_career.soulblessing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class SoulBlessingHelper {

    private SoulBlessingHelper() {
    }

    public static SoulBlessingInventory inventory(
            Player player
    ) {
        return player.getCapability(
                        SoulBlessingCapability.INVENTORY
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Missing Soul Blessing inventory for "
                                        + player.getName()
                                        .getString()
                        )
                );
    }

    public static Optional<SoulBlessingInventory> findInventory(
            Player player
    ) {
        if (player == null
                || player.isRemoved()) {
            return Optional.empty();
        }

        return player.getCapability(
                        SoulBlessingCapability.INVENTORY
                )
                .resolve();
    }

    public static List<ItemStack> getEquipped(
            Player player,
            SoulBlessingSlotType type
    ) {
        Optional<SoulBlessingInventory> optional =
                findInventory(
                        player
                );

        if (optional.isEmpty()) {
            return List.of();
        }

        SoulBlessingInventory inventory =
                optional.get();

        List<ItemStack> result =
                new ArrayList<>();

        for (int i = 0;
             i < SoulBlessingSlots.COUNT;
             i++) {

            if (SoulBlessingSlots.type(i)
                    != type) {
                continue;
            }

            ItemStack stack =
                    inventory.getStackInSlot(i);

            if (stack.isEmpty()) {
                continue;
            }

            result.add(
                    stack.copy()
            );
        }

        return List.copyOf(
                result
        );
    }

    public static List<ItemStack> getAllEquipped(
            Player player
    ) {
        Optional<SoulBlessingInventory> optional =
                findInventory(
                        player
                );

        if (optional.isEmpty()) {
            return List.of();
        }

        SoulBlessingInventory inventory =
                optional.get();

        List<ItemStack> result =
                new ArrayList<>();

        for (int i = 0;
             i < SoulBlessingSlots.COUNT;
             i++) {

            ItemStack stack =
                    inventory.getStackInSlot(i);

            if (stack.isEmpty()) {
                continue;
            }

            result.add(
                    stack.copy()
            );
        }

        return List.copyOf(
                result
        );
    }

    public static int countEquipped(
            Player player,
            Item item
    ) {
        if (player == null
                || item == null) {
            return 0;
        }

        int count = 0;

        for (ItemStack stack :
                getAllEquipped(player)) {

            if (stack.is(item)) {
                count++;
            }
        }

        return count;
    }

    public static boolean hasEquipped(
            Player player,
            Item item
    ) {
        return countEquipped(
                player,
                item
        ) > 0;
    }
}