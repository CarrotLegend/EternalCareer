package com.carrot123.eternal_career.compat.corpse;

import com.carrot123.eternal_career.soulblessing.SoulBlessingCapability;
import com.carrot123.eternal_career.soulblessing.SoulBlessingInventory;
import com.carrot123.eternal_career.soulblessing.SoulBlessingItem;
import com.carrot123.eternal_career.soulblessing.SoulBlessingLifecycleEvents;
import com.carrot123.eternal_career.soulblessing.SoulBlessingModifiers;
import com.carrot123.eternal_career.soulblessing.SoulBlessingSlots;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class CorpseSoulBlessingTransferHelper {

    public static final String DEATH_SLOT_TAG =
            "EternalCareerSoulBlessingSlot";

    private CorpseSoulBlessingTransferHelper() {
    }

    public static void markDeathSlot(
            ItemStack stack,
            int slot
    ) {
        if (stack.isEmpty()) {
            return;
        }

        stack.getOrCreateTag().putInt(
                DEATH_SLOT_TAG,
                slot
        );
    }

    public static TransferState prepareTransfer(
            Object menuObject
    ) {
        if (!(menuObject instanceof AbstractContainerMenu)) {
            return TransferState.EMPTY;
        }

        try {
            Method isEditableMethod =
                    menuObject.getClass()
                            .getMethod(
                                    "isEditable"
                            );

            Object editableObject =
                    isEditableMethod.invoke(
                            menuObject
                    );

            if (!(editableObject instanceof Boolean editable)
                    || !editable) {
                return TransferState.EMPTY;
            }

            Inventory playerInventory =
                    getPlayerInventory(
                            menuObject
                    );

            if (playerInventory == null
                    || !(playerInventory.player
                    instanceof ServerPlayer player)) {
                return TransferState.EMPTY;
            }

            List<ItemStack> additionalItems =
                    getAdditionalItems(
                            menuObject
                    );

            if (additionalItems == null) {
                return TransferState.EMPTY;
            }

            SoulBlessingInventory blessingInventory =
                    player.getCapability(
                                    SoulBlessingCapability.INVENTORY
                            )
                            .resolve()
                            .orElse(
                                    null
                            );

            List<ProtectedStack> protectedStacks =
                    new ArrayList<>();

            boolean equippedAny =
                    false;

            for (int index = 0;
                 index < additionalItems.size();
                 index++) {

                ItemStack stack =
                        additionalItems.get(
                                index
                        );

                if (!isMarkedSoulBlessing(
                        stack
                )) {
                    continue;
                }

                boolean equipped =
                        blessingInventory != null
                                && tryEquip(
                                blessingInventory,
                                stack
                        );

                if (equipped) {
                    additionalItems.set(
                            index,
                            ItemStack.EMPTY
                    );

                    equippedAny = true;
                } else {
                    protectedStacks.add(
                            new ProtectedStack(
                                    index,
                                    stack.copy()
                            )
                    );

                    additionalItems.set(
                            index,
                            ItemStack.EMPTY
                    );
                }
            }

            if (equippedAny
                    && blessingInventory != null) {
                SoulBlessingModifiers.reconcile(
                        player,
                        blessingInventory
                );

                SoulBlessingLifecycleEvents.sync(
                        player
                );
            }

            return new TransferState(
                    true,
                    protectedStacks
            );
        } catch (ReflectiveOperationException
                 | RuntimeException ignored) {
            return TransferState.EMPTY;
        }
    }

    public static void restoreTransfer(
            Object menuObject,
            TransferState state,
            boolean restoreOriginalIndices
    ) {
        if (state == null
                || !state.active()
                || state.protectedStacks()
                .isEmpty()) {
            return;
        }

        try {
            List<ItemStack> additionalItems =
                    getAdditionalItems(
                            menuObject
                    );

            if (additionalItems == null) {
                return;
            }

            for (ProtectedStack protectedStack :
                    state.protectedStacks()) {

                ItemStack stack =
                        protectedStack.stack()
                                .copy();

                if (restoreOriginalIndices
                        && protectedStack.index()
                        >= 0
                        && protectedStack.index()
                        < additionalItems.size()
                        && additionalItems
                        .get(
                                protectedStack.index()
                        )
                        .isEmpty()) {

                    additionalItems.set(
                            protectedStack.index(),
                            stack
                    );
                } else {
                    additionalItems.add(
                            stack
                    );
                }
            }

            if (menuObject
                    instanceof AbstractContainerMenu menu) {
                menu.broadcastChanges();
            }
        } catch (ReflectiveOperationException
                 | RuntimeException ignored) {
        }
    }

    private static boolean tryEquip(
            SoulBlessingInventory inventory,
            ItemStack corpseStack
    ) {
        int preferredSlot =
                getPreferredSlot(
                        corpseStack
                );

        int targetSlot =
                findTargetSlot(
                        inventory,
                        corpseStack,
                        preferredSlot
                );

        if (targetSlot < 0) {
            return false;
        }

        ItemStack equipped =
                corpseStack.copy();

        equipped.setCount(
                1
        );

        removeDeathSlotTag(
                equipped
        );

        inventory.setStackInSlot(
                targetSlot,
                equipped
        );

        ItemStack inserted =
                inventory.getStackInSlot(
                        targetSlot
                );

        return !inserted.isEmpty()
                && inserted.getItem()
                == equipped.getItem();
    }

    private static int findTargetSlot(
            SoulBlessingInventory inventory,
            ItemStack stack,
            int preferredSlot
    ) {
        if (preferredSlot >= 0
                && preferredSlot
                < SoulBlessingSlots.COUNT
                && inventory
                .getStackInSlot(
                        preferredSlot
                )
                .isEmpty()
                && inventory.isItemValid(
                preferredSlot,
                stack
        )) {
            return preferredSlot;
        }

        for (int slot = 0;
             slot < SoulBlessingSlots.COUNT;
             slot++) {

            if (!inventory
                    .getStackInSlot(
                            slot
                    )
                    .isEmpty()) {
                continue;
            }

            if (!inventory.isItemValid(
                    slot,
                    stack
            )) {
                continue;
            }

            return slot;
        }

        return -1;
    }

    private static int getPreferredSlot(
            ItemStack stack
    ) {
        CompoundTag tag =
                stack.getTag();

        if (tag == null
                || !tag.contains(
                DEATH_SLOT_TAG,
                Tag.TAG_INT
        )) {
            return -1;
        }

        return tag.getInt(
                DEATH_SLOT_TAG
        );
    }

    private static boolean isMarkedSoulBlessing(
            ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return false;
        }

        if (!(stack.getItem()
                instanceof SoulBlessingItem)) {
            return false;
        }

        CompoundTag tag =
                stack.getTag();

        return tag != null
                && tag.contains(
                DEATH_SLOT_TAG,
                Tag.TAG_INT
        );
    }

    private static void removeDeathSlotTag(
            ItemStack stack
    ) {
        CompoundTag tag =
                stack.getTag();

        if (tag == null) {
            return;
        }

        tag.remove(
                DEATH_SLOT_TAG
        );

        if (tag.isEmpty()) {
            stack.setTag(
                    null
            );
        }
    }

    private static Inventory getPlayerInventory(
            Object menuObject
    ) throws ReflectiveOperationException {
        Method method =
                menuObject.getClass()
                        .getMethod(
                                "getPlayerInventory"
                        );

        Object result =
                method.invoke(
                        menuObject
                );

        if (result instanceof Inventory inventory) {
            return inventory;
        }

        if (result instanceof Container
                && result instanceof Inventory inventory) {
            return inventory;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private static List<ItemStack> getAdditionalItems(
            Object menuObject
    ) throws ReflectiveOperationException {
        Method getCorpse =
                menuObject.getClass()
                        .getMethod(
                                "getCorpse"
                        );

        Object corpse =
                getCorpse.invoke(
                        menuObject
                );

        if (corpse == null) {
            return null;
        }

        Method getDeath =
                corpse.getClass()
                        .getMethod(
                                "getDeath"
                        );

        Object death =
                getDeath.invoke(
                        corpse
                );

        if (death == null) {
            return null;
        }

        Method getAdditionalItems =
                death.getClass()
                        .getMethod(
                                "getAdditionalItems"
                        );

        Object result =
                getAdditionalItems.invoke(
                        death
                );

        if (!(result instanceof List<?> list)) {
            return null;
        }

        return (List<ItemStack>) list;
    }

    public record TransferState(
            boolean active,
            List<ProtectedStack> protectedStacks
    ) {
        private static final TransferState EMPTY =
                new TransferState(
                        false,
                        List.of()
                );
    }

    public record ProtectedStack(
            int index,
            ItemStack stack
    ) {
    }
}