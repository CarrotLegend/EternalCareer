package com.carrot123.eternal_career.network;

import com.carrot123.eternal_career.fletching.FletchingTableMenu;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record FletchingModifyPacket(int menuId) {
    public static void encode(FletchingModifyPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.menuId);
    }

    public static FletchingModifyPacket decode(FriendlyByteBuf buffer) {
        return new FletchingModifyPacket(buffer.readVarInt());
    }

    public static void handle(FletchingModifyPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof FletchingTableMenu menu
                    && menu.containerId == packet.menuId) {
                menu.modify(player);
            }
        });
        context.setPacketHandled(true);
    }
}
