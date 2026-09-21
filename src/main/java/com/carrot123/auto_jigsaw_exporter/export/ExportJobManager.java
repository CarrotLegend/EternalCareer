package com.carrot123.auto_jigsaw_exporter.export;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.carrot123.auto_jigsaw_exporter.AutoJigsawExporter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = AutoJigsawExporter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ExportJobManager {
    private static final Map<UUID, ExportJob> JOBS = new ConcurrentHashMap<>();

    private ExportJobManager() {
    }

    public static boolean start(ServerPlayer player,
                                ResourceLocation structureId,
                                JigsawExportPlan plan,
                                int targetPieceSize,
                                boolean includeEntities) throws IOException {
        if (JOBS.containsKey(player.getUUID())) {
            player.displayClientMessage(
                    Component.translatable("message.auto_jigsaw_exporter.job_already_running"),
                    false);
            return false;
        }

        MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }

        Path worldRoot = server.getWorldPath(LevelResource.ROOT);
        String packName = ("auto_jigsaw_" + structureId.getNamespace() + "_" + structureId.getPath())
                .replaceAll("[^a-zA-Z0-9._-]", "_");
        Path packRoot = worldRoot.resolve("datapacks").resolve(packName);

        deleteRecursively(packRoot);
        JigsawTemplateWriter.writePackMeta(packRoot, structureId);

        ExportJob job = new ExportJob(
                server,
                player.level().dimension(),
                player.getUUID(),
                plan,
                packRoot,
                targetPieceSize,
                includeEntities);
        JOBS.put(player.getUUID(), job);

        player.displayClientMessage(
                Component.translatable(
                        "message.auto_jigsaw_exporter.started",
                        plan.pieces().size(),
                        plan.recommendedDepth()),
                false);
        return true;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || JOBS.isEmpty()) {
            return;
        }

        for (ExportJob job : new ArrayList<>(JOBS.values())) {
            try {
                if (job.processNext()) {
                    JOBS.remove(job.owner());
                }
            } catch (Exception exception) {
                JOBS.remove(job.owner());
                ServerPlayer player = job.server().getPlayerList().getPlayer(job.owner());
                if (player != null) {
                    player.displayClientMessage(
                            Component.translatable(
                                    "message.auto_jigsaw_exporter.failed",
                                    exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage()),
                            false);
                }
                AutoJigsawExporter.LOGGER.error("Auto jigsaw export failed for {}", job.plan().structureId(), exception);
            }
        }
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (var stream = Files.walk(path)) {
            for (Path current : stream.sorted((a, b) -> b.getNameCount() - a.getNameCount()).toList()) {
                Files.deleteIfExists(current);
            }
        }
    }
}
