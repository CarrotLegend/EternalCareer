package com.carrot123.eternal_career.network;

import com.carrot123.eternal_career.soulblessing.SoulBlessingCapability;
import com.carrot123.eternal_career.soulblessing.SoulBlessingLifecycleEvents;
import com.carrot123.eternal_career.soulblessing.SoulBlessingMenu;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkEvent;

public record SoulBlessingRequestPacket(boolean open) {

    private static final Logger LOGGER = LogUtils.getLogger();

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(open);
    }

    public static SoulBlessingRequestPacket decode(FriendlyByteBuf buffer) {
        return new SoulBlessingRequestPacket(buffer.readBoolean());
    }

    public static void handle(
            SoulBlessingRequestPacket packet,
            Supplier<NetworkEvent.Context> context
    ) {
        NetworkEvent.Context ctx = context.get();

        ctx.enqueueWork(() -> {
            ServerPlayer sender = ctx.getSender();

            if (sender == null || !sender.isAlive() || sender.isRemoved()) {
                return;
            }

            if (packet.open()) {
                if (sender.containerMenu instanceof SoulBlessingMenu) {
                    SoulBlessingLifecycleEvents.sync(sender);
                    return;
                }

                if (!sender.getCapability(SoulBlessingCapability.INVENTORY)
                        .isPresent()) {
                    LOGGER.warn(
                            "Soul Blessing menu cannot open for {}: inventory capability missing",
                            sender.getGameProfile().getName()
                    );
                    return;
                }

                safelyReturnCarried(sender);

                if (sender.containerMenu != sender.inventoryMenu) {
                    sender.closeContainer();
                }

                if (!sender.getCapability(SoulBlessingCapability.INVENTORY)
                        .isPresent()) {
                    LOGGER.warn(
                            "Soul Blessing inventory disappeared during menu transition for {}",
                            sender.getGameProfile().getName()
                    );
                    return;
                }

                NetworkHooks.openScreen(
                        sender,
                        new SimpleMenuProvider(
                                (id, inventory, player) ->
                                        new SoulBlessingMenu(id, inventory),
                                Component.translatable(
                                        "soul_blessing.eternal_career.title"
                                )
                        )
                );

                SoulBlessingLifecycleEvents.sync(sender);
                return;
            }

            if (sender.containerMenu instanceof SoulBlessingMenu) {
                safelyReturnCarried(sender);
                sender.closeContainer();

                ModNetwork.send(
                        sender,
                        new SoulBlessingReturnPacket()
                );
            }
        });

        ctx.setPacketHandled(true);
    }

    private static void safelyReturnCarried(ServerPlayer player) {
        ItemStack carried = player.containerMenu.getCarried();

        if (carried.isEmpty()) {
            return;
        }

        ItemStack returned = carried.copy();
        player.containerMenu.setCarried(ItemStack.EMPTY);

        player.getInventory().add(returned);

        if (!returned.isEmpty()) {
            player.drop(returned, false);
        }
    }
}
