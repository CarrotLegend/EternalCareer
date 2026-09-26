package com.carrot123.eternal_career.network;

import com.carrot123.eternal_career.soulblessing.SoulBlessingLifecycleEvents;
import com.carrot123.eternal_career.soulblessing.SoulBlessingMenu;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkEvent;

/** True opens the blessing menu, false closes it and returns to inventory. */
public record SoulBlessingRequestPacket(boolean open) {
    public void encode(FriendlyByteBuf buffer) { buffer.writeBoolean(open); }

    public static SoulBlessingRequestPacket decode(FriendlyByteBuf buffer) {
        return new SoulBlessingRequestPacket(buffer.readBoolean());
    }

    public static void handle(SoulBlessingRequestPacket packet,
            Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();
            if (sender == null || !sender.containerMenu.getCarried().isEmpty()) return;
            if (packet.open()) {
                if (sender.containerMenu != sender.inventoryMenu) return;
                NetworkHooks.openScreen(sender, new SimpleMenuProvider(
                        (id, inventory, player) -> new SoulBlessingMenu(id, inventory),
                        Component.translatable("soul_blessing.eternal_career.title")));
                SoulBlessingLifecycleEvents.sync(sender);
            } else if (sender.containerMenu instanceof SoulBlessingMenu) {
                sender.closeContainer();
                ModNetwork.send(sender, new SoulBlessingReturnPacket());
            }
        });
        ctx.setPacketHandled(true);
    }
}
