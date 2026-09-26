package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.network.SoulBlessingSyncPacket;
import com.carrot123.eternal_career.soulblessing.SoulBlessingCapability;
import com.carrot123.eternal_career.soulblessing.SoulBlessingSlots;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;

public final class SoulBlessingClientState {
    private SoulBlessingClientState() {}

    public static void accept(SoulBlessingSyncPacket packet) {
        if (Minecraft.getInstance().player == null
                || packet.stacks().size() != SoulBlessingSlots.COUNT) return;
        Minecraft.getInstance().player.getCapability(SoulBlessingCapability.INVENTORY)
                .ifPresent(inventory -> {
                    for (int i = 0; i < SoulBlessingSlots.COUNT; i++) {
                        inventory.setStackInSlot(i, packet.stacks().get(i).copy());
                    }
                });
    }

    public static void returnToInventory() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.containerMenu == minecraft.player.inventoryMenu) {
            minecraft.setScreen(new InventoryScreen(minecraft.player));
        }
    }
}
