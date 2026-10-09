package com.carrot123.eternal_career.fletching;

import com.carrot123.eternal_career.registry.ModMenus;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public final class FletchingTableMenu extends AbstractContainerMenu {
    public static final int BOW_SLOT = 0;
    public static final int UPGRADE_SLOT = 1;
    public static final int INVENTORY_START = 2;
    public static final int HOTBAR_START = 29;
    public static final int SLOT_COUNT = 38;
    private final Container inputs = new SimpleContainer(2);
    private final Inventory playerInventory;
    private final ContainerLevelAccess access;

    public FletchingTableMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, buffer.readBlockPos());
    }

    public FletchingTableMenu(int id, Inventory inventory, BlockPos pos) {
        super(ModMenus.FLETCHING_TABLE.get(), id);
        playerInventory = inventory;
        access = ContainerLevelAccess.create(inventory.player.level(), pos);
        addSlot(new Slot(inputs, BOW_SLOT, 182, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return BowModificationHelper.isBow(stack);
            }
        });
        addSlot(new Slot(inputs, UPGRADE_SLOT, 182, 91) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof BowModificationItem;
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, 9 + row * 9 + column,
                        34 + column * 18, 164 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 34 + column * 18, 222));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return player == playerInventory.player
                && AbstractContainerMenu.stillValid(access, player, Blocks.FLETCHING_TABLE);
    }

    public ItemStack bow() {
        return inputs.getItem(BOW_SLOT);
    }

    public ItemStack upgrade() {
        return inputs.getItem(UPGRADE_SLOT);
    }

    public boolean canModify() {
        if (!(upgrade().getItem() instanceof BowModificationItem item)) {
            return false;
        }
        return BowModificationHelper.canApply(bow(), item.modification());
    }

    public boolean modify(Player player) {
        if (!stillValid(player) || !canModify()) {
            return false;
        }
        BowModificationItem item = (BowModificationItem) upgrade().getItem();
        if (!BowModificationHelper.addLevel(bow(), item.modification().id())) {
            return false;
        }
        upgrade().shrink(1);
        inputs.setChanged();
        broadcastChanges();
        return true;
    }

    public boolean craft(Player player, BowModification modification) {
        if (!stillValid(player) || modification == null || !BowModifications.CRAFTABLE.contains(modification)) {
            return false;
        }
        int[] deductions = new int[36];
        List<BowModification.Material> materials = modification.materials();
        for (BowModification.Material material : materials) {
            int missing = material.count();
            for (int index = 0; index < 36 && missing > 0; index++) {
                ItemStack stack = playerInventory.getItem(index);
                if (stack.is(material.item())) {
                    int available = stack.getCount() - deductions[index];
                    int consumed = Math.min(missing, Math.max(0, available));
                    deductions[index] += consumed;
                    missing -= consumed;
                }
            }
            if (missing > 0) {
                return false;
            }
        }
        ItemStack output = modification.icon();
        boolean fits = false;
        for (int index = 0; index < 36; index++) {
            ItemStack stack = playerInventory.getItem(index);
            int remaining = stack.getCount() - deductions[index];
            if (stack.isEmpty() || remaining <= 0
                    || ItemStack.isSameItemSameTags(stack, output)
                            && remaining < stack.getMaxStackSize()) {
                fits = true;
                break;
            }
        }
        if (!fits) {
            return false;
        }
        for (int index = 0; index < 36; index++) {
            if (deductions[index] > 0) {
                playerInventory.getItem(index).shrink(deductions[index]);
            }
        }
        playerInventory.setChanged();
        if (!playerInventory.add(output)) {
            player.drop(output, false);
        }
        broadcastChanges();
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= SLOT_COUNT) {
            return ItemStack.EMPTY;
        }
        Slot source = slots.get(index);
        if (!source.hasItem() || !source.mayPickup(player)) {
            return ItemStack.EMPTY;
        }
        ItemStack moving = source.getItem();
        ItemStack original = moving.copy();
        boolean moved;
        if (index < INVENTORY_START) {
            moved = moveItemStackTo(moving, INVENTORY_START, SLOT_COUNT, true);
        } else if (index < HOTBAR_START) {
            moved = moveItemStackTo(moving, HOTBAR_START, SLOT_COUNT, false);
        } else {
            moved = moveItemStackTo(moving, INVENTORY_START, HOTBAR_START, false);
        }
        if (!moved) {
            return ItemStack.EMPTY;
        }
        if (moving.isEmpty()) {
            source.setByPlayer(ItemStack.EMPTY);
        } else {
            source.setChanged();
        }
        source.onTake(player, moving);
        return original;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide()) {
            for (int index = 0; index < 2; index++) {
                ItemStack stack = inputs.removeItemNoUpdate(index);
                if (!stack.isEmpty()) {
                    player.getInventory().placeItemBackInInventory(stack);
                }
            }
        }
    }
}

