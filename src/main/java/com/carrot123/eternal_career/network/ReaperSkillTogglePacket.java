package com.carrot123.eternal_career.network;

import java.util.function.Supplier;

import com.carrot123.eternal_career.soul.ReaperSkillManager;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public final class ReaperSkillTogglePacket {
    public static void encode(ReaperSkillTogglePacket packet, FriendlyByteBuf buffer) {
    }

    public static ReaperSkillTogglePacket decode(FriendlyByteBuf buffer) {
        return new ReaperSkillTogglePacket();
    }

    public static void handle(
            ReaperSkillTogglePacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            if (player != null) {
                ReaperSkillManager.toggle(player);
            }
        });

        context.setPacketHandled(true);
    }
}