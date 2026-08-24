package com.carrot123.eternal_career.compat.curios;

import java.util.Map;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public final class CurioSafeLookup {

    private CurioSafeLookup() {
    }

    public static boolean hasEquipped(
        LivingEntity entity,
            Item item
    ) {
        if (entity == null || item == null) {
            return false;
        }

        return CuriosApi.getCuriosInventory(entity)
                .resolve()
                .map(handler -> {

                    for (Map.Entry<String, ICurioStacksHandler>
                            entry
                            : handler.getCurios().entrySet()) {

                        IDynamicStackHandler stacks =
                                entry.getValue().getStacks();

                        for (int i = 0;
                             i < stacks.getSlots();
                             i++) {

                            ItemStack stack =
                                    stacks.getStackInSlot(i);

                            if (!stack.isEmpty()
                                    && stack.is(item)) {
                                return true;
                            }
                        }
                    }

                    return false;
                })
                .orElse(false);
    }

    public static boolean hasOtherEquipped(
            SlotContext currentContext,
            Item item
    ) {
        if (currentContext == null
                || currentContext.entity() == null
                || item == null) {
            return false;
        }

        return CuriosApi
                .getCuriosInventory(
                        currentContext.entity()
                )
                .resolve()
                .map(handler -> {

                    for (Map.Entry<String, ICurioStacksHandler>
                            entry
                            : handler.getCurios().entrySet()) {

                        String identifier =
                                entry.getKey();

                        IDynamicStackHandler stacks =
                                entry.getValue()
                                        .getStacks();

                        for (int i = 0;
                             i < stacks.getSlots();
                             i++) {

                            boolean isCurrentSlot =
                                !currentContext.cosmetic()
                                    && identifier.equals(
                                        currentContext
                                            .identifier()
                                    )
                                    && i
                                    == currentContext
                                        .index();

                            if (isCurrentSlot) {
                                continue;
                            }

                            ItemStack stack =
                                    stacks.getStackInSlot(i);

                            if (!stack.isEmpty()
                                    && stack.is(item)) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .orElse(false);
    }
}