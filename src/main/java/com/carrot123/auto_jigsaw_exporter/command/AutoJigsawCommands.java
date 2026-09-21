package com.carrot123.auto_jigsaw_exporter.command;

import com.carrot123.auto_jigsaw_exporter.AutoJigsawExporter;
import com.carrot123.auto_jigsaw_exporter.blockentity.StartMarkerBlockEntity;
import com.carrot123.auto_jigsaw_exporter.export.ExportJobManager;
import com.carrot123.auto_jigsaw_exporter.export.JigsawExportPlan;
import com.carrot123.auto_jigsaw_exporter.registry.ModBlocks;
import com.carrot123.auto_jigsaw_exporter.selection.MarkerSelectionManager;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AutoJigsawExporter.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class AutoJigsawCommands {
    private AutoJigsawCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("autojigsaw")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("status")
                                .executes(context -> showStatus(context.getSource().getPlayerOrException())))
                        .then(Commands.literal("export")
                                .then(Commands.argument("namespace", StringArgumentType.word())
                                        .then(Commands.argument("name", StringArgumentType.word())
                                                .executes(context -> export(
                                                        context.getSource().getPlayerOrException(),
                                                        StringArgumentType.getString(context, "namespace"),
                                                        StringArgumentType.getString(context, "name"),
                                                        32,
                                                        false))
                                                .then(Commands.argument("piece_size", IntegerArgumentType.integer(8, 48))
                                                        .executes(context -> export(
                                                                context.getSource().getPlayerOrException(),
                                                                StringArgumentType.getString(context, "namespace"),
                                                                StringArgumentType.getString(context, "name"),
                                                                IntegerArgumentType.getInteger(context, "piece_size"),
                                                                false))
                                                        .then(Commands.argument("include_entities", BoolArgumentType.bool())
                                                                .executes(context -> export(
                                                                        context.getSource().getPlayerOrException(),
                                                                        StringArgumentType.getString(context, "namespace"),
                                                                        StringArgumentType.getString(context, "name"),
                                                                        IntegerArgumentType.getInteger(context, "piece_size"),
                                                                        BoolArgumentType.getBool(context, "include_entities")))))))));
    }

    private static int showStatus(ServerPlayer player) {
        BlockPos startPos = MarkerSelectionManager.getSelectedStartPos(player);
        StartMarkerBlockEntity marker = MarkerSelectionManager.getSelectedStart(player);
        if (startPos == null || marker == null) {
            player.displayClientMessage(
                    Component.translatable("message.auto_jigsaw_exporter.no_start_selected"),
                    false);
            return 0;
        }

        BlockPos endPos = marker.getEndPos();
        if (endPos == null) {
            player.displayClientMessage(
                    Component.translatable(
                            "message.auto_jigsaw_exporter.status_start_only",
                            startPos.getX(), startPos.getY(), startPos.getZ()),
                    false);
            return 1;
        }

        player.displayClientMessage(
                Component.translatable(
                        "message.auto_jigsaw_exporter.status_ready",
                        startPos.getX(), startPos.getY(), startPos.getZ(),
                        endPos.getX(), endPos.getY(), endPos.getZ()),
                false);
        return 1;
    }

    private static int export(ServerPlayer player,
                              String namespace,
                              String name,
                              int pieceSize,
                              boolean includeEntities) {
        ResourceLocation structureId = ResourceLocation.tryParse(namespace + ":" + name);
        if (structureId == null
                || !structureId.getNamespace().equals(namespace)
                || !structureId.getPath().equals(name)) {
            player.displayClientMessage(
                    Component.translatable("message.auto_jigsaw_exporter.invalid_id", namespace + ":" + name),
                    false);
            return 0;
        }

        BlockPos startPos = MarkerSelectionManager.getSelectedStartPos(player);
        StartMarkerBlockEntity marker = MarkerSelectionManager.getSelectedStart(player);
        if (startPos == null || marker == null) {
            player.displayClientMessage(
                    Component.translatable("message.auto_jigsaw_exporter.no_start_selected"),
                    false);
            return 0;
        }

        BlockPos endPos = marker.getEndPos();
        if (endPos == null) {
            player.displayClientMessage(
                    Component.translatable("message.auto_jigsaw_exporter.start_waiting_end"),
                    false);
            return 0;
        }

        if (!player.serverLevel().getBlockState(startPos).is(ModBlocks.START_MARKER.get())
                || !player.serverLevel().getBlockState(endPos).is(ModBlocks.END_MARKER.get())) {
            player.displayClientMessage(
                    Component.translatable("message.auto_jigsaw_exporter.marker_missing"),
                    false);
            return 0;
        }

        if (startPos.equals(endPos)) {
            player.displayClientMessage(
                    Component.translatable("message.auto_jigsaw_exporter.zero_selection"),
                    false);
            return 0;
        }

        try {
            JigsawExportPlan plan = JigsawExportPlan.create(
                    structureId,
                    startPos,
                    endPos,
                    pieceSize);
            return ExportJobManager.start(
                    player,
                    structureId,
                    plan,
                    pieceSize,
                    includeEntities) ? 1 : 0;
        } catch (Exception exception) {
            AutoJigsawExporter.LOGGER.error("Failed to start auto jigsaw export", exception);
            player.displayClientMessage(
                    Component.translatable(
                            "message.auto_jigsaw_exporter.failed",
                            exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage()),
                    false);
            return 0;
        }
    }
}
