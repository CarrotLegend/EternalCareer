package com.carrot123.eternal_career.network;

import com.carrot123.eternal_career.EternalCareer;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(EternalCareer.MOD_ID, "main"), () -> VERSION,
            VERSION::equals, VERSION::equals);
    private ModNetwork() {}
    public static void register() {
        CHANNEL.registerMessage(0, SoulSyncPacket.class, SoulSyncPacket::encode,
                SoulSyncPacket::decode, SoulSyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }
    public static void send(ServerPlayer player, SoulSyncPacket packet) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }
}
