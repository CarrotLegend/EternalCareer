package com.carrot123.eternal_career.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

public final class CurseEquipHelper {

    private CurseEquipHelper() {
    }

    public static boolean canEquipSingle(
            Player player,
            Item item,
            SlotContext context
    ) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler ->
                        handler.findCurios(item)
                                .stream()
                                .noneMatch(result -> {
                                    SlotContext equipped =
                                            result.slotContext();

                                    if (equipped.cosmetic()) {
                                        return false;
                                    }

                                    return !(
                                            context.identifier()
                                                    .equals(
                                                            equipped.identifier()
                                                    )
                                                    && context.index()
                                                    == equipped.index()
                                                    && context.cosmetic()
                                                    == equipped.cosmetic()
                                    );
                                })
                )
                .orElse(false);
    }
}