package com.carrot123.eternal_career.network;

import com.carrot123.eternal_career.client.SoulHudOverlay;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record SoulSyncPacket(int currentSoul, boolean active, int maxSoul) {
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(currentSoul);
        buffer.writeBoolean(active);
        buffer.writeVarInt(maxSoul);
    }
    public static SoulSyncPacket decode(FriendlyByteBuf buffer) {
        return new SoulSyncPacket(buffer.readVarInt(), buffer.readBoolean(), buffer.readVarInt());
    }
    public static void handle(SoulSyncPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> SoulHudOverlay.accept(packet)));
        context.get().setPacketHandled(true);
    }
}
