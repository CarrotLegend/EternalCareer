package com.carrot123.eternal_career.network;

import com.carrot123.eternal_career.client.SoulBlessingClientState;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

/** Sent after the server has closed the blessing container. */
public record SoulBlessingReturnPacket() {
    public void encode(FriendlyByteBuf buffer) {}

    public static SoulBlessingReturnPacket decode(FriendlyByteBuf buffer) {
        return new SoulBlessingReturnPacket();
    }

    public static void handle(SoulBlessingReturnPacket packet,
            Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> SoulBlessingClientState::returnToInventory));
        ctx.setPacketHandled(true);
    }
}
