package com.carrot123.eternal_career.client;

import org.lwjgl.glfw.GLFW;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.network.ModNetwork;
import com.carrot123.eternal_career.network.ReaperSkillTogglePacket;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        value = Dist.CLIENT
)
public final class ReaperSkillClientEvents {
    private static final KeyMapping REAPER_SKILL = new KeyMapping(
            "key.eternal_career.reaper_skill",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.MOUSE,
            GLFW.GLFW_MOUSE_BUTTON_MIDDLE,
            "key.categories.misc"
    );

    private ReaperSkillClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null || minecraft.screen != null) {
            return;
        }

        while (REAPER_SKILL.consumeClick()) {
            ModNetwork.sendToServer(new ReaperSkillTogglePacket());
        }
    }

    @Mod.EventBusSubscriber(
            modid = EternalCareer.MOD_ID,
            value = Dist.CLIENT,
            bus = Mod.EventBusSubscriber.Bus.MOD
    )
    public static final class Registration {
        private Registration() {
        }

        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(REAPER_SKILL);
        }
    }
}