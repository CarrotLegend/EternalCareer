package com.carrot123.eternal_career.soul;

import com.carrot123.eternal_career.career.capability.soul.SoulCapability;
import com.carrot123.eternal_career.network.ModNetwork;
import com.carrot123.eternal_career.network.SoulSyncPacket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/** Server-thread-only dirty queue: no player scan and no unconditional tick packets. */
public final class SoulSyncManager {
    private static final Set<UUID> DIRTY = new HashSet<>();
    private static final Map<UUID, SoulSyncPacket> LAST = new HashMap<>();
    private SoulSyncManager() {}
    public static void mark(ServerPlayer player) { DIRTY.add(player.getUUID()); }
    public static void force(ServerPlayer player) { LAST.remove(player.getUUID()); mark(player); }
    public static void forget(ServerPlayer player) { DIRTY.remove(player.getUUID()); LAST.remove(player.getUUID()); }
    public static void clear() { DIRTY.clear(); LAST.clear(); }
    public static void flush(MinecraftServer server) {
        Set<UUID> pending = Set.copyOf(DIRTY);
        DIRTY.clear();
        for (UUID id : pending) {
            ServerPlayer player = server.getPlayerList().getPlayer(id);
            if (player == null) { LAST.remove(id); continue; }
            player.getCapability(SoulCapability.SOUL).ifPresent(soul -> {
                var set = SoulSetManager.findActiveSoulSet(player);
                SoulSyncPacket packet = new SoulSyncPacket(soul.getSoul(), set.isPresent(),
                        set.map(SoulSetDefinition::maxSoul).orElse(0));
                if (!packet.equals(LAST.get(id))) {
                    ModNetwork.send(player, packet);
                    LAST.put(id, packet);
                }
            });
        }
    }
}
