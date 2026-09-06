package com.carrot123.eternal_career.mixin.vanilla;

import com.carrot123.eternal_career.compat.redemption.RedemptionMenuAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InventoryMenu.class)
public abstract class InventoryMenuRedemptionMixin implements RedemptionMenuAccess {
    @Shadow @Final private CraftingContainer craftSlots;

    @Shadow public abstract int getResultSlotIndex();

    @Override
    public boolean eternalCareer$isResultSlot(int menuSlot) {
        return menuSlot == this.getResultSlotIndex();
    }

    @Override
    public Iterable<ItemStack> eternalCareer$getInputStacks() {
        return this.craftSlots.getItems();
    }
}
