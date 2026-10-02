package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.MaterialArrowItem;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MaterialArrowEvents {
    private MaterialArrowEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onArrowJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || event.loadedFromDisk()
                || !(event.getEntity() instanceof Arrow arrow)
                || arrow.getPersistentData().getBoolean(MaterialArrowItem.DAMAGE_APPLIED_TAG)) {
            return;
        }
        MaterialArrowItem.Material material = MaterialArrowItem.materialOf(arrow);
        if (material == null) {
            return;
        }
        double baseDamage = arrow.getBaseDamage();
        if (!Double.isFinite(baseDamage) || baseDamage < 0.0D) {
            return;
        }
        arrow.setBaseDamage(baseDamage * material.multiplier());
        arrow.getPersistentData().putBoolean(MaterialArrowItem.DAMAGE_APPLIED_TAG, true);
    }
}
