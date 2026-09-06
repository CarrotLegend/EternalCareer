package com.carrot123.eternal_career.mixin.vanilla;

import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;
import com.carrot123.eternal_career.compat.redemption.RedemptionMenuAccess;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Prevents recipe consumption and vanilla armor placement before a menu click mutates any slot. */
@Mixin(AbstractContainerMenu.class)
public abstract class AbstractContainerMenuRedemptionMixin {
    @Shadow @Final public NonNullList<Slot> slots;

    @Shadow public abstract ItemStack getCarried();

    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void eternalCareer$validateRedemptionClick(
            int slotIndex, int button, ClickType clickType, Player player, CallbackInfo callback) {
        if (slotIndex < 0 || slotIndex >= this.slots.size()) {
            return;
        }

        AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
        if (self instanceof RedemptionMenuAccess recipeMenu
                && recipeMenu.eternalCareer$isResultSlot(slotIndex)
                && containsRedemptionInput(recipeMenu)
                && !RedemptionAccessController.hasRedemptionAccess(player)) {
            callback.cancel();
            return;
        }

        if (self instanceof InventoryMenu
                && wouldEquipRestrictedArmor(slotIndex, button, clickType, player)) {
            callback.cancel();
        }
    }

    private boolean wouldEquipRestrictedArmor(
            int slotIndex, int button, ClickType clickType, Player player) {
        Slot clicked = this.slots.get(slotIndex);

        ItemStack carried = this.getCarried();
        ItemStack placement = clickType == ClickType.SWAP
                && button >= 0
                && button < player.getInventory().getContainerSize()
                ? player.getInventory().getItem(button)
                : carried;
        if (!placement.isEmpty()
                && clicked.container == player.getInventory()
                && clicked.getContainerSlot() >= 36
                && clicked.getContainerSlot() <= 39) {
            return RedemptionAccessController.deny(player, placement);
        }

        if (clickType != ClickType.QUICK_MOVE) {
            return false;
        }

        ItemStack source = clicked.getItem();
        if (!RedemptionAccessController.deny(player, source)) {
            return false;
        }

        EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(source);
        return equipmentSlot.getType() == EquipmentSlot.Type.ARMOR
                && player.getItemBySlot(equipmentSlot).isEmpty();
    }

    private static boolean containsRedemptionInput(RedemptionMenuAccess menu) {
        for (ItemStack stack : menu.eternalCareer$getInputStacks()) {
            if (RedemptionAccessController.isRedemptionItem(stack)) {
                return true;
            }
        }
        return false;
    }
}
