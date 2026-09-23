package com.carrot123.eternal_career.network;

import com.carrot123.eternal_career.client.LichClientState;
import com.carrot123.eternal_career.lich.LichStage;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record LichEligibilitySyncPacket(boolean usedPanaceaBeforeLich, LichStage stage) {
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(usedPanaceaBeforeLich);
        buffer.writeEnum(stage);
    }

    public static LichEligibilitySyncPacket decode(FriendlyByteBuf buffer) {
        return new LichEligibilitySyncPacket(buffer.readBoolean(), buffer.readEnum(LichStage.class));
    }

    public static void handle(LichEligibilitySyncPacket packet,
            Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> LichClientState.accept(packet)));
        context.get().setPacketHandled(true);
    }
}
