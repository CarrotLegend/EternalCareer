package com.carrot123.eternal_career.compat.solcarrot;

import com.cazsius.solcarrot.api.SOLCarrotAPI;
import net.minecraft.world.entity.player.Player;

/** Read-only adapter for Spice of Life: Carrot Edition's own food history. */
public final class SolCarrotHelper {
    private SolCarrotHelper() {
    }

    public static int getUniqueFoodsEaten(Player player) {
        return SOLCarrotAPI.getFoodCapability(player).getEatenFoodCount();
    }
}
