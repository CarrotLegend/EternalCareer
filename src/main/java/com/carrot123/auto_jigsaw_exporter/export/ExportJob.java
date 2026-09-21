package com.carrot123.auto_jigsaw_exporter.export;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public final class ExportJob {
    private final MinecraftServer server;
    private final ResourceKey<Level> dimension;
    private final UUID owner;
    private final JigsawExportPlan plan;
    private final Path packRoot;
    private final int targetPieceSize;
    private final boolean includeEntities;
    private int nextPiece;

    public ExportJob(MinecraftServer server,
                     ResourceKey<Level> dimension,
                     UUID owner,
                     JigsawExportPlan plan,
                     Path packRoot,
                     int targetPieceSize,
                     boolean includeEntities) {
        this.server = server;
        this.dimension = dimension;
        this.owner = owner;
        this.plan = plan;
        this.packRoot = packRoot;
        this.targetPieceSize = targetPieceSize;
        this.includeEntities = includeEntities;
    }

    public MinecraftServer server() {
        return server;
    }

    public UUID owner() {
        return owner;
    }

    public JigsawExportPlan plan() {
        return plan;
    }

    public Path packRoot() {
        return packRoot;
    }

    public boolean processNext() throws IOException {
        ServerLevel level = server.getLevel(dimension);
        if (level == null) {
            throw new IOException("Export dimension is no longer loaded: " + dimension.location());
        }

        if (nextPiece >= plan.pieces().size()) {
            finish();
            return true;
        }

        JigsawExportPlan.Piece piece = plan.pieces().get(nextPiece);
        JigsawTemplateWriter.writePiece(level, plan, piece, packRoot, includeEntities);
        nextPiece++;

        ServerPlayer player = server.getPlayerList().getPlayer(owner);
        if (player != null) {
            player.displayClientMessage(
                    Component.translatable(
                            "message.auto_jigsaw_exporter.progress",
                            nextPiece,
                            plan.pieces().size()),
                    true);
        }

        if (nextPiece >= plan.pieces().size()) {
            finish();
            return true;
        }
        return false;
    }

    private void finish() throws IOException {
        JigsawTemplateWriter.writeManifest(plan, packRoot, targetPieceSize, includeEntities);
        ServerPlayer player = server.getPlayerList().getPlayer(owner);
        if (player != null) {
            player.displayClientMessage(
                    Component.translatable(
                            "message.auto_jigsaw_exporter.complete",
                            plan.pieces().size(),
                            packRoot.toAbsolutePath().toString()),
                    false);
        }
    }
}
