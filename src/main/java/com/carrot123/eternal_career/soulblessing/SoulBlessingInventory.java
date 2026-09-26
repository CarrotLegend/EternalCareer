package com.carrot123.eternal_career.soulblessing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public final class SoulBlessingInventory extends ItemStackHandler {
    private final Runnable changed;
    private final ItemStack[] applied = new ItemStack[SoulBlessingSlots.COUNT];
    private ItemStack legacyNecklace = ItemStack.EMPTY;

    public SoulBlessingInventory(Runnable changed) {
        super(SoulBlessingSlots.COUNT);
        this.changed = changed;
        for (int i = 0; i < applied.length; i++) applied[i] = ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) { return 1; }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return slot >= 0 && slot < SoulBlessingSlots.COUNT
                && stack.getItem() instanceof SoulBlessingItem blessing
                && blessing.getSoulBlessingType() == SoulBlessingSlots.type(slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (!stack.isEmpty() && (!isItemValid(slot, stack) || stack.getCount() != 1)) return;
        super.setStackInSlot(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) { changed.run(); }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        CompoundTag fixed = tag.copy();
        legacyNecklace = tag.contains("LegacyNecklace", Tag.TAG_COMPOUND)
                ? ItemStack.of(tag.getCompound("LegacyNecklace")) : ItemStack.EMPTY;
        // Seven-slot saves used index 2 for the second necklace. Retain that item
        // separately until the owning player can receive it in the normal inventory.
        if (tag.getInt("Size") == 7) {
            ListTag converted = new ListTag();
            ListTag oldItems = tag.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < oldItems.size(); i++) {
                CompoundTag entry = oldItems.getCompound(i).copy();
                int oldSlot = entry.getByte("Slot") & 255;
                if (oldSlot == 2) {
                    legacyNecklace = ItemStack.of(entry);
                } else if (oldSlot < 7) {
                    entry.putByte("Slot", (byte) (oldSlot > 2 ? oldSlot - 1 : oldSlot));
                    converted.add(entry);
                }
            }
            fixed.put("Items", converted);
        }
        fixed.putInt("Size", SoulBlessingSlots.COUNT);
        super.deserializeNBT(fixed);
        for (int slot = 0; slot < SoulBlessingSlots.COUNT; slot++) {
            ItemStack stack = stacks.get(slot);
            if (!stack.isEmpty() && !isItemValid(slot, stack)) {
                stacks.set(slot, ItemStack.EMPTY);
            } else if (stack.getCount() > 1) {
                stack.setCount(1);
            }
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        if (!legacyNecklace.isEmpty()) {
            tag.put("LegacyNecklace", legacyNecklace.save(new CompoundTag()));
        }
        return tag;
    }

    public ItemStack takeLegacyNecklace() {
        ItemStack stack = legacyNecklace;
        legacyNecklace = ItemStack.EMPTY;
        return stack;
    }

    ItemStack previous(int slot) { return applied[slot]; }

    void remember(int slot, ItemStack stack) { applied[slot] = stack.copy(); }
}
