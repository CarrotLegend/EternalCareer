package com.carrot123.eternal_career.compat.enigmaticdelicacy;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;

import auviotre.enigmatic.delicacy.registries.EnigmaticDelightItems;
import net.minecraft.world.entity.player.Player;

public final class WeaponMasterGloryHelper {

    private WeaponMasterGloryHelper() {
    }

    public static boolean isEquipped(
            Player player
    ) {
        return CurioEquipmentHelper.hasEquippedCurio(
                player,
                EnigmaticDelightItems.WEAPON_CHARM
        );
    }
}