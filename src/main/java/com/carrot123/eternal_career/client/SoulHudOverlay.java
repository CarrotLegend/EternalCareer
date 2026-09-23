package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.network.SoulSyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SoulHudOverlay {
    private static SoulSyncPacket state = new SoulSyncPacket(0, false, 0);
    private SoulHudOverlay() {}
    public static void accept(SoulSyncPacket packet) { state = packet; }
    public static int currentSoul() { return state.currentSoul(); }
    public static void reset() { state = new SoulSyncPacket(0, false, 0); }
    public static final IGuiOverlay OVERLAY = (gui, graphics, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.player.isSpectator()
                || !state.active() || state.maxSoul() <= 0) return;
        Component text = Component.translatable("gui.eternal_career.soul")
                .append(" " + state.currentSoul() + " / " + state.maxSoul());
        int textWidth = mc.font.width(text);
        int panelWidth = Math.max(12, textWidth);
        int centerX = width - 8 - panelWidth / 2;
        int x = centerX - 6;
        int y = Math.max(2, (height - 94) / 2);
        graphics.fill(x, y, x + 12, y + 80, 0xDD172D32);
        graphics.fill(x + 1, y + 1, x + 11, y + 79, 0xCC071418);
        double ratio = Math.max(0, Math.min(1, (double) state.currentSoul() / state.maxSoul()));
        int fill = (int) Math.floor(78 * ratio);
        graphics.fill(x + 1, y + 79 - fill, x + 11, y + 79, 0xFF49DBBC);
        graphics.drawString(mc.font, text, centerX - textWidth / 2, y + 84, 0xD3FFF0, true);
    };
    @SubscribeEvent public static void register(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("soul", OVERLAY);
    }
    @Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, value = Dist.CLIENT)
    public static final class ConnectionEvents {
        @SubscribeEvent public static void logout(ClientPlayerNetworkEvent.LoggingOut event) { reset(); }
    }
}
