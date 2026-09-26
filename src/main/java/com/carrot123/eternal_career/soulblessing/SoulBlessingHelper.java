package com.carrot123.eternal_career.soulblessing;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class SoulBlessingHelper {
    private SoulBlessingHelper() {}

    public static SoulBlessingInventory inventory(Player player) {
        return player.getCapability(SoulBlessingCapability.INVENTORY).orElseThrow(
                () -> new IllegalStateException("Missing Soul Blessing inventory for " + player.getName().getString()));
    }

    public static List<ItemStack> getEquipped(Player player, SoulBlessingSlotType type) {
        SoulBlessingInventory inventory = inventory(player);
        List<ItemStack> result = new ArrayList<>();
        for (int i = 0; i < SoulBlessingSlots.COUNT; i++) {
            if (SoulBlessingSlots.type(i) == type && !inventory.getStackInSlot(i).isEmpty()) {
                result.add(inventory.getStackInSlot(i).copy());
            }
        }
        return List.copyOf(result);
    }

    public static List<ItemStack> getAllEquipped(Player player) {
        SoulBlessingInventory inventory = inventory(player);
        List<ItemStack> result = new ArrayList<>();
        for (int i = 0; i < SoulBlessingSlots.COUNT; i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) result.add(inventory.getStackInSlot(i).copy());
        }
        return List.copyOf(result);
    }

    public static int countEquipped(Player player, Item item) {
        int count = 0;
        for (ItemStack stack : getAllEquipped(player)) if (stack.is(item)) count++;
        return count;
    }

    public static boolean hasEquipped(Player player, Item item) {
        return countEquipped(player, item) > 0;
    }
}
