package com.carrot123.auto_jigsaw_exporter.selection;

import com.carrot123.auto_jigsaw_exporter.blockentity.StartMarkerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MarkerSelectionManager {
    private static final Map<UUID, Selection> SELECTIONS = new ConcurrentHashMap<>();

    private MarkerSelectionManager() {
    }

    public static void selectStart(ServerPlayer player, BlockPos startPos) {
        SELECTIONS.put(player.getUUID(), new Selection(player.level().dimension(), startPos.immutable()));
        player.displayClientMessage(
                Component.translatable("message.auto_jigsaw_exporter.start_selected",
                        startPos.getX(), startPos.getY(), startPos.getZ()),
                false);
    }

    public static boolean linkEnd(ServerPlayer player, BlockPos endPos) {
        Selection selection = SELECTIONS.get(player.getUUID());
        if (selection == null || selection.dimension() != player.level().dimension()) {
            player.displayClientMessage(
                    Component.translatable("message.auto_jigsaw_exporter.no_start_selected"),
                    false);
            return false;
        }

        BlockEntity blockEntity = player.serverLevel().getBlockEntity(selection.startPos());
        if (!(blockEntity instanceof StartMarkerBlockEntity startMarker)) {
            player.displayClientMessage(
                    Component.translatable("message.auto_jigsaw_exporter.start_missing"),
                    false);
            SELECTIONS.remove(player.getUUID());
            return false;
        }

        startMarker.setEndPos(endPos);
        player.serverLevel().sendBlockUpdated(
                selection.startPos(),
                player.serverLevel().getBlockState(selection.startPos()),
                player.serverLevel().getBlockState(selection.startPos()),
                3);
        player.displayClientMessage(
                Component.translatable("message.auto_jigsaw_exporter.end_linked",
                        endPos.getX(), endPos.getY(), endPos.getZ()),
                false);
        return true;
    }

    public static StartMarkerBlockEntity getSelectedStart(ServerPlayer player) {
        Selection selection = SELECTIONS.get(player.getUUID());
        if (selection == null || selection.dimension() != player.level().dimension()) {
            return null;
        }
        BlockEntity blockEntity = player.serverLevel().getBlockEntity(selection.startPos());
        return blockEntity instanceof StartMarkerBlockEntity marker ? marker : null;
    }

    public static BlockPos getSelectedStartPos(ServerPlayer player) {
        Selection selection = SELECTIONS.get(player.getUUID());
        if (selection == null || selection.dimension() != player.level().dimension()) {
            return null;
        }
        return selection.startPos();
    }

    private record Selection(ResourceKey<Level> dimension, BlockPos startPos) {
    }
}
