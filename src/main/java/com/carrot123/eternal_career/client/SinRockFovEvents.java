package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.SinRockItem;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Adds the bow-like zoom that vanilla only grants to the vanilla bow item. */
@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT)
public final class SinRockFovEvents {
    private static final float MAX_ZOOM_REDUCTION = 0.15F;

    private SinRockFovEvents() {
    }

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        if (player != Minecraft.getInstance().player
                || !player.isUsingItem()
                || !player.getUseItem().is(ModItems.SIN_ROCK.get())) {
            return;
        }

        float progress = Mth.clamp(
                player.getTicksUsingItem() / (float) SinRockItem.USE_DURATION_TICKS,
                0.0F,
                1.0F);
        event.setNewFovModifier(
                event.getNewFovModifier() * (1.0F - MAX_ZOOM_REDUCTION * progress));
    }
}
