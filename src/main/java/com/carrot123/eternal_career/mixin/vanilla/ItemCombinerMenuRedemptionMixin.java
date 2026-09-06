package com.carrot123.eternal_career.mixin.vanilla;

import com.carrot123.eternal_career.compat.redemption.RedemptionMenuAccess;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemCombinerMenu.class)
public abstract class ItemCombinerMenuRedemptionMixin implements RedemptionMenuAccess {
    @Shadow @Final protected Container inputSlots;

    @Shadow public abstract int getResultSlot();

    @Override
    public boolean eternalCareer$isResultSlot(int menuSlot) {
        return menuSlot == this.getResultSlot();
    }

    @Override
    public Iterable<ItemStack> eternalCareer$getInputStacks() {
        List<ItemStack> inputs = new ArrayList<>(this.inputSlots.getContainerSize());
        for (int slot = 0; slot < this.inputSlots.getContainerSize(); slot++) {
            inputs.add(this.inputSlots.getItem(slot));
        }
        return inputs;
    }
}
