package com.carrot123.eternal_career.witch;

import com.Polarice3.Goety.common.items.brew.BrewItem;
import com.carrot123.eternal_career.item.WitchHeartItem;
import com.carrot123.eternal_career.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class WitchHeartMenu extends AbstractContainerMenu {
    private static final int PLAYER_START = 1;
    private static final int PLAYER_END = 37;
    private final ItemStack heart;
    private final InteractionHand hand;
    private final SimpleContainer container;

    public WitchHeartMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, buffer.readEnum(InteractionHand.class));
    }

    public WitchHeartMenu(int id, Inventory inventory, InteractionHand hand) {
        super(ModMenus.WITCH_HEART.get(), id);
        this.hand = hand;
        this.heart = inventory.player.getItemInHand(hand);
        this.container = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                if (!inventory.player.level().isClientSide && heart.getItem() instanceof WitchHeartItem) {
                    WitchHeartItem.setStoredBrew(heart, getItem(0));
                }
            }
        };
        container.setItem(0, WitchHeartItem.getStoredBrew(heart));

        addSlot(new Slot(container, 0, 80, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) { return stack.getItem() instanceof BrewItem; }
        });
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, 9 + row * 9 + col, 8 + col * 18, 49 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 107));
    }

    public boolean stores(ItemStack stack) { return this.heart == stack; }

    public void updateStoredBrew(ItemStack brew) {
        container.setItem(0, brew.copy());
        broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive() && heart.getItem() instanceof WitchHeartItem && player.getItemInHand(hand) == heart;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem() || !slot.mayPickup(player)) return ItemStack.EMPTY;
        ItemStack moving = slot.getItem();
        ItemStack result = moving.copy();
        boolean success;
        if (slotIndex == 0) {
            success = moveItemStackTo(moving, PLAYER_START, PLAYER_END, true);
        } else {
            ItemStack stored = container.getItem(0);
            if (!(moving.getItem() instanceof BrewItem) || (!stored.isEmpty() && !ItemStack.isSameItemSameTags(stored, moving))) return ItemStack.EMPTY;
            int space = moving.getMaxStackSize() - stored.getCount();
            int transferred = Math.min(space, moving.getCount());
            if (transferred <= 0) return ItemStack.EMPTY;
            if (stored.isEmpty()) {
                ItemStack inserted = moving.copy();
                inserted.setCount(transferred);
                container.setItem(0, inserted);
            } else {
                stored.grow(transferred);
                container.setChanged();
            }
            moving.shrink(transferred);
            success = true;
        }
        if (!success) return ItemStack.EMPTY;
        if (moving.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, moving);
        return result;
    }
}
