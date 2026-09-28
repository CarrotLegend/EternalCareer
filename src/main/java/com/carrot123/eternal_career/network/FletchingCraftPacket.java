package com.carrot123.eternal_career.network;

import com.carrot123.eternal_career.fletching.BowModifications;
import com.carrot123.eternal_career.fletching.FletchingTableMenu;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record FletchingCraftPacket(int menuId, ResourceLocation modificationId) {
    public static void encode(FletchingCraftPacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.menuId);
        buffer.writeResourceLocation(packet.modificationId);
    }

    public static FletchingCraftPacket decode(FriendlyByteBuf buffer) {
        return new FletchingCraftPacket(buffer.readVarInt(), buffer.readResourceLocation());
    }

    public static void handle(FletchingCraftPacket packet,
            Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof FletchingTableMenu menu
                    && menu.containerId == packet.menuId) {
                menu.craft(player, BowModifications.byId(packet.modificationId));
            }
        });
        context.setPacketHandled(true);
    }
}
