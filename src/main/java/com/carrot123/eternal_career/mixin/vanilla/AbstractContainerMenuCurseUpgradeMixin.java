package com.carrot123.eternal_career.mixin.vanilla;

import com.carrot123.eternal_career.item.DiscouragedCurseItem;
import com.carrot123.eternal_career.item.FragileCurseItem;
import com.carrot123.eternal_career.item.HeavyCurseItem;
import com.carrot123.eternal_career.item.HungerCurseItem;
import com.carrot123.eternal_career.item.IgnoranceCurseItem;
import com.carrot123.eternal_career.item.PandoraBoxItem;
import com.carrot123.eternal_career.item.PowerlessCurseItem;
import com.carrot123.eternal_career.item.UndeadCurseItem;
import com.carrot123.eternal_career.item.VulnerabilityCurseItem;
import com.carrot123.eternal_career.registry.ModItems;
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
public abstract class AbstractContainerMenuCurseUpgradeMixin {

    @Shadow
    public abstract ItemStack getCarried();

    @Shadow
    public abstract void setCarried(ItemStack stack);

    @Shadow
    public abstract Slot getSlot(int index);

    @Shadow
    public abstract void broadcastChanges();

    @Inject(
            method = "clicked",
            at = @At("HEAD"),
            cancellable = true
    )
    private void eternalCareer$handleCurseSpirit(
            int slotId,
            int button,
            ClickType clickType,
            Player player,
            CallbackInfo ci
    ) {
        if (player.level().isClientSide) {
            return;
        }

        if (slotId < 0
                || button != 1
                || clickType != ClickType.PICKUP) {
            return;
        }

        ItemStack carried =
                getCarried();

        if (carried.isEmpty()) {
            return;
        }

        Slot slot;

        try {
            slot = getSlot(slotId);
        } catch (IndexOutOfBoundsException exception) {
            return;
        }

        if (!(slot instanceof CurioSlot curioSlot)) {
            return;
        }

        if (!PandoraBoxItem.CURSE_SPIRIT_SLOT.equals(
                curioSlot.getIdentifier()
        )) {
            return;
        }

        ItemStack target =
                slot.getItem();

        if (target.isEmpty()) {
            return;
        }

        boolean success = false;

        if (carried.is(ModItems.DEAD_CURSE_SPIRIT.get())
                && target.is(ModItems.UNDEAD_CURSE.get())
                && !UndeadCurseItem.isUpgraded(target)) {

            UndeadCurseItem.upgrade(target);
            success = true;

        } else if (
                carried.is(ModItems.SHADOW_CURSE_SPIRIT.get())
                        && target.is(ModItems.FRAGILE_CURSE.get())
                        && !FragileCurseItem.isReversed(target)
        ) {

            FragileCurseItem.reverse(target);
            success = true;

        } else if (
                carried.is(ModItems.WEAKNESS_CURSE_SPIRIT.get())
                        && target.is(ModItems.POWERLESS_CURSE.get())
                        && !PowerlessCurseItem.isReversed(target)
        ) {

            PowerlessCurseItem.reverse(target);
            success = true;

        } else if (
                carried.is(ModItems.ARMOR_BREAK_CURSE_SPIRIT.get())
                        && target.is(ModItems.VULNERABILITY_CURSE.get())
                        && !VulnerabilityCurseItem.isReversed(target)
        ) {

            VulnerabilityCurseItem.reverse(target);
            success = true;

        } else if (
                carried.is(ModItems.FOOLISH_CURSE_SPIRIT.get())
                        && target.is(ModItems.IGNORANCE_CURSE.get())
                        && !IgnoranceCurseItem.isReversed(target)
        ) {

            IgnoranceCurseItem.reverse(target);
            success = true;

        } else if (
                carried.is(ModItems.SLOWNESS_CURSE_SPIRIT.get())
                        && target.is(ModItems.HEAVY_CURSE.get())
                        && !HeavyCurseItem.isReversed(target)
        ) {

            HeavyCurseItem.reverse(target);
            success = true;

        } else if (
                carried.is(ModItems.DESPAIR_CURSE_SPIRIT.get())
                        && target.is(ModItems.DISCOURAGED_CURSE.get())
                        && !DiscouragedCurseItem.isReversed(target)
        ) {

            DiscouragedCurseItem.reverse(target);
            success = true;

        } else if (
                carried.is(ModItems.FAMINE_CURSE_SPIRIT.get())
                        && target.is(ModItems.HUNGER_CURSE.get())
                        && !HungerCurseItem.isReversed(target)
        ) {

            HungerCurseItem.reverse(target);
            success = true;
        }

        if (!success) {
            return;
        }

        if (!player.getAbilities().instabuild) {
            carried.shrink(1);
        }

        setCarried(
                carried.isEmpty()
                        ? ItemStack.EMPTY
                        : carried
        );

        slot.setChanged();
        broadcastChanges();
        ci.cancel();
    }
}