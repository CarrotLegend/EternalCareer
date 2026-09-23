package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.lich.LichUtils;
import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.network.LichEligibilitySyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class LichClientState {
    private static LichEligibilitySyncPacket pendingState;

    private LichClientState() {
    }

    public static void accept(LichEligibilitySyncPacket packet) {
        pendingState = packet;
        applyPending();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            applyPending();
        }
    }

    private static void applyPending() {
        if (pendingState != null && Minecraft.getInstance().player != null) {
            LichUtils.setClientState(Minecraft.getInstance().player,
                    pendingState.usedPanaceaBeforeLich(), pendingState.stage());
            pendingState = null;
        }
    }
}
