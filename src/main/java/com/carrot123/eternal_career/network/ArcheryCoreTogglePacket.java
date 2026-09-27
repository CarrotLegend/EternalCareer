package com.carrot123.eternal_career.network;

import com.carrot123.eternal_career.registry.ModItems;
import com.carrot123.eternal_career.item.ArcheryMasterCoreItem;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public record ArcheryCoreTogglePacket(int slotId, int menuId) {
    public static void encode(ArcheryCoreTogglePacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.slotId);
        buffer.writeVarInt(packet.menuId);
    }

    public static ArcheryCoreTogglePacket decode(FriendlyByteBuf buffer) {
        return new ArcheryCoreTogglePacket(buffer.readVarInt(), buffer.readVarInt());
    }

    public static void handle(ArcheryCoreTogglePacket packet,
            Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || player.containerMenu.containerId != packet.menuId
                    || !player.containerMenu.getCarried().isEmpty()
                    || packet.slotId < 0 || packet.slotId >= player.containerMenu.slots.size()) {
                return;
            }
            Slot slot = player.containerMenu.getSlot(packet.slotId);
            ItemStack stack = slot.getItem();
            if (stack.is(ModItems.ARCHERY_MASTER_CORE.get()) && slot.mayPickup(player)) {
                ArcheryMasterCoreItem.toggle(stack);
                slot.setChanged();
                player.containerMenu.broadcastChanges();
            }
        });
        context.setPacketHandled(true);
    }
}
