
package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.network.SoulBlessingSyncPacket;
import com.carrot123.eternal_career.soulblessing.SoulBlessingCapability;
import com.carrot123.eternal_career.soulblessing.SoulBlessingSlots;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public final class SoulBlessingClientState {

    private static int inventoryReturnRequestTick = -1;

    private SoulBlessingClientState() {
    }

    public static void accept(SoulBlessingSyncPacket packet) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || packet.stacks().size() != SoulBlessingSlots.COUNT) {
            return;
        }

        minecraft.player.getCapability(SoulBlessingCapability.INVENTORY)
                .ifPresent(inventory -> {
                    for (int i = 0; i < SoulBlessingSlots.COUNT; i++) {
                        inventory.setStackInSlot(
                                i,
                                packet.stacks().get(i).copy()
                        );
                    }
                });
    }

    public static void requestInventoryReturn() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            inventoryReturnRequestTick = -1;
            return;
        }

        inventoryReturnRequestTick = minecraft.player.tickCount;
    }

    public static boolean consumeInventoryReturn() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || inventoryReturnRequestTick < 0) {
            inventoryReturnRequestTick = -1;
            return false;
        }

        int elapsed = minecraft.player.tickCount - inventoryReturnRequestTick;
        inventoryReturnRequestTick = -1;

        return elapsed >= 0 && elapsed <= 100;
    }

    public static void returnToInventory() {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null) {
            inventoryReturnRequestTick = -1;
            return;
        }

        if (minecraft.player.containerMenu != minecraft.player.inventoryMenu) {
            return;
        }

        inventoryReturnRequestTick = -1;

        if (!(minecraft.screen instanceof InventoryScreen)) {
            minecraft.setScreen(new InventoryScreen(minecraft.player));
        }
    }
}
