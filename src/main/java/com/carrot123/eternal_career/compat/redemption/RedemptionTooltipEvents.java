package com.carrot123.eternal_career.compat.redemption;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Client-only adapter that applies the shared redemption tooltip. */
@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT)
public final class RedemptionTooltipEvents {
    private RedemptionTooltipEvents() {
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        Player player = event.getEntity();
        if (player == null) {
            player = Minecraft.getInstance().player;
        }
        RedemptionTooltipHelper.appendRestrictionTooltip(
                player, event.getItemStack(), event.getToolTip());
    }
}
