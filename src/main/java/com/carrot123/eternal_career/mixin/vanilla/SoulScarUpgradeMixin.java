package com.carrot123.eternal_career.mixin.vanilla;

import com.carrot123.eternal_career.soulcurse.SoulScarItem;
import com.carrot123.eternal_career.soulcurse.TransformingScarItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.common.inventory.CurioSlot;

@Mixin(AbstractContainerMenu.class)
public abstract class SoulScarUpgradeMixin {
    @Shadow public abstract ItemStack getCarried();
    @Shadow public abstract void setCarried(ItemStack stack);
    @Shadow public abstract Slot getSlot(int index);
    @Shadow public abstract void broadcastChanges();
    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void eternalCareer$reverseSoulScar(int slotId, int button, ClickType click, Player player, CallbackInfo ci) {
        if (player.level().isClientSide || slotId < 0 || button != 1 || click != ClickType.PICKUP || getCarried().isEmpty()) return;
        Slot slot;
        try { slot = getSlot(slotId); } catch (IndexOutOfBoundsException ignored) { return; }
        if (!(slot instanceof CurioSlot curios) || !TransformingScarItem.SLOT.equals(curios.getIdentifier())) return;
        ItemStack target = slot.getItem();
        if (target.isEmpty() || !(target.getItem() instanceof SoulScarItem) || !SoulScarItem.reverse(target, getCarried())) return;
        if (!player.getAbilities().instabuild) getCarried().shrink(1);
        if (getCarried().isEmpty()) setCarried(ItemStack.EMPTY);
        slot.setChanged();
        broadcastChanges();
        ci.cancel();
    }
}
