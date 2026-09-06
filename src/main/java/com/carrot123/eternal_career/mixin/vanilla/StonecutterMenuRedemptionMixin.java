package com.carrot123.eternal_career.mixin.vanilla;

import com.carrot123.eternal_career.compat.redemption.RedemptionMenuAccess;
import java.util.List;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(StonecutterMenu.class)
public abstract class StonecutterMenuRedemptionMixin implements RedemptionMenuAccess {
    @Shadow @Final Slot inputSlot;
    @Shadow @Final Slot resultSlot;

    @Override
    public boolean eternalCareer$isResultSlot(int menuSlot) {
        return menuSlot == this.resultSlot.index;
    }

    @Override
    public Iterable<ItemStack> eternalCareer$getInputStacks() {
        return List.of(this.inputSlot.getItem());
    }
}
