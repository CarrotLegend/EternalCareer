package com.carrot123.eternal_career.soulblessing;

import com.carrot123.eternal_career.registry.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Server-authoritative blessing inventory; no vanilla crafting, armor or offhand slots. */
public final class SoulBlessingMenu extends AbstractContainerMenu {
    public static final int BLESSING_START = 0;
    public static final int PLAYER_START = SoulBlessingSlots.COUNT;
    public static final int HOTBAR_START = PLAYER_START + 27;
    public static final int SLOT_COUNT = HOTBAR_START + 9;

    private final Inventory playerInventory;

    public SoulBlessingMenu(int containerId, Inventory playerInventory) {
        super(ModMenus.SOUL_BLESSING.get(), containerId);
        this.playerInventory = playerInventory;
        SoulBlessingInventory blessings = SoulBlessingHelper.inventory(playerInventory.player);
        for (int index = 0; index < SoulBlessingSlots.COUNT; index++) {
            addSlot(new SoulBlessingSlot(blessings, index));
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, 9 + row * 9 + column,
                        16 + column * 18, 126 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 16 + column * 18, 186));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return player == playerInventory.player && player.isAlive();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot source = slots.get(index);
        if (!source.hasItem() || !source.mayPickup(player)) return ItemStack.EMPTY;
        ItemStack moving = source.getItem();
        ItemStack original = moving.copy();
        boolean moved = false;
        if (index < PLAYER_START) {
            moved = moveItemStackTo(moving, PLAYER_START, SLOT_COUNT, true);
        } else if (moving.getItem() instanceof SoulBlessingItem) {
            for (int target = BLESSING_START; target < PLAYER_START; target++) {
                Slot destination = slots.get(target);
                if (!destination.hasItem() && destination.mayPlace(moving)) {
                    moved = moveItemStackTo(moving, target, target + 1, false);
                    break;
                }
            }
        } else if (index < HOTBAR_START) {
            moved = moveItemStackTo(moving, HOTBAR_START, SLOT_COUNT, false);
        } else {
            moved = moveItemStackTo(moving, PLAYER_START, HOTBAR_START, false);
        }
        if (!moved) return ItemStack.EMPTY;
        if (moving.isEmpty()) source.setByPlayer(ItemStack.EMPTY);
        else source.setChanged();
        source.onTake(player, moving);
        return original;
    }
}
