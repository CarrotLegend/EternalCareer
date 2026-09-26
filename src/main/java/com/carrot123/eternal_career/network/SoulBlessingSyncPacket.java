package com.carrot123.eternal_career.network;

import com.carrot123.eternal_career.client.SoulBlessingClientState;
import com.carrot123.eternal_career.soulblessing.SoulBlessingSlots;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record SoulBlessingSyncPacket(List<ItemStack> stacks) {
    public static SoulBlessingSyncPacket fromInventory(
            com.carrot123.eternal_career.soulblessing.SoulBlessingInventory inventory) {
        List<ItemStack> stacks = new ArrayList<>(SoulBlessingSlots.COUNT);
        for (int i = 0; i < SoulBlessingSlots.COUNT; i++) {
            stacks.add(inventory.getStackInSlot(i).copy());
        }
        return new SoulBlessingSyncPacket(List.copyOf(stacks));
    }

    public void encode(FriendlyByteBuf buffer) {
        for (ItemStack stack : stacks) buffer.writeItem(stack);
    }

    public static SoulBlessingSyncPacket decode(FriendlyByteBuf buffer) {
        List<ItemStack> stacks = new ArrayList<>(SoulBlessingSlots.COUNT);
        for (int i = 0; i < SoulBlessingSlots.COUNT; i++) stacks.add(buffer.readItem());
        return new SoulBlessingSyncPacket(List.copyOf(stacks));
    }

    public static void handle(SoulBlessingSyncPacket packet,
            Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> SoulBlessingClientState.accept(packet)));
        context.get().setPacketHandled(true);
    }
}
