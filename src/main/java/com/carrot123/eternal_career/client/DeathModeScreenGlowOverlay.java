package com.carrot123.eternal_career.client;

import com.carrot123.eternal_career.EternalCareer;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class DeathModeScreenGlowOverlay {

    private static final UUID REAPER_ATTACK_SPEED_UUID =
            UUID.fromString("64c02c8b-56d1-46b9-b40c-7967931f38af");

    private static final int RED_COLOR = 0xD51010;
    private static final int MAX_ALPHA = 24;
    private static final int MAX_LAYERS = 32;

    private static final IGuiOverlay OVERLAY =
            (gui, graphics, partialTick, width, height) -> {
                Minecraft minecraft = Minecraft.getInstance();

                if (minecraft.player == null
                        || minecraft.level == null
                        || minecraft.options.hideGui
                        || minecraft.player.isSpectator()) {
                    return;
                }

                if (!isDeathModeActive(minecraft)) {
                    return;
                }

                renderRedGlow(graphics, width, height);
            };

    private DeathModeScreenGlowOverlay() {
    }

    private static boolean isDeathModeActive(Minecraft minecraft) {
        AttributeInstance attackSpeed =
                minecraft.player.getAttribute(Attributes.ATTACK_SPEED);

        return attackSpeed != null
                && attackSpeed.getModifier(REAPER_ATTACK_SPEED_UUID) != null;
    }

    private static void renderRedGlow(
            GuiGraphics graphics,
            int width,
            int height
    ) {
        if (width <= 0 || height <= 0) {
            return;
        }

        int horizontalThickness = Math.max(
                1,
                Math.min(width / 2, Math.max(10, width / 9))
        );

        int verticalThickness = Math.max(
                1,
                Math.min(height / 2, Math.max(10, height / 6))
        );

        int layers = Math.max(
                1,
                Math.min(MAX_LAYERS,
                        Math.min(horizontalThickness, verticalThickness))
        );

        for (int i = 0; i < layers; i++) {
            float progress = (float) i / layers;
            float fade = 1.0F - progress;
            int alpha = Math.round(MAX_ALPHA * fade * fade);

            if (alpha <= 0) {
                continue;
            }

            int color = (alpha << 24) | RED_COLOR;

            int x0 = i * horizontalThickness / layers;
            int x1 = (i + 1) * horizontalThickness / layers;
            int y0 = i * verticalThickness / layers;
            int y1 = (i + 1) * verticalThickness / layers;

            graphics.fill(x0, 0, x1, height, color);
            graphics.fill(width - x1, 0, width - x0, height, color);

            graphics.fill(0, y0, width, y1, color);
            graphics.fill(0, height - y1, width, height - y0, color);
        }
    }

    @SubscribeEvent
    public static void registerOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("death_mode_red_glow", OVERLAY);
    }
}