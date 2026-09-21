package com.carrot123.auto_jigsaw_exporter.export;

import com.carrot123.auto_jigsaw_exporter.registry.ModBlocks;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class JigsawTemplateWriter {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JigsawTemplateWriter() {
    }

    public static void writePiece(ServerLevel level,
                                  JigsawExportPlan plan,
                                  JigsawExportPlan.Piece piece,
                                  Path packRoot,
                                  boolean includeEntities) throws IOException {
        StructureTemplate template = new StructureTemplate();
        template.fillFromWorld(
                level,
                piece.origin(),
                piece.size(),
                includeEntities,
                Blocks.STRUCTURE_VOID);
        template.setAuthor("Auto Jigsaw Exporter");

        CompoundTag structureTag = NbtUtils.addCurrentDataVersion(template.save(new CompoundTag()));
        replaceSelectionMarker(structureTag, piece, plan.startMarker());
        replaceSelectionMarker(structureTag, piece, plan.endMarker());

        List<JigsawExportPlan.Edge> edges = new ArrayList<>(plan.edgesForPiece(piece.index()));
        edges.sort(Comparator.comparingInt(edge -> edge.parentIndex() * 1_000_000 + edge.childIndex()));

        for (JigsawExportPlan.Edge edge : edges) {
            if (edge.parentIndex() == piece.index()) {
                addOutgoingConnector(level, structureTag, plan, piece, edge);
            } else {
                addIncomingConnector(level, structureTag, plan, piece, edge);
            }
        }

        ResourceLocation structureLocation = plan.structureLocation(piece);
        Path structurePath = packRoot.resolve("data")
                .resolve(structureLocation.getNamespace())
                .resolve("structures")
                .resolve(structureLocation.getPath() + ".nbt");
        Files.createDirectories(structurePath.getParent());
        NbtIo.writeCompressed(structureTag, structurePath.toFile());

        writePoolJson(plan, piece, packRoot);
    }

    public static void writePackMeta(Path packRoot, ResourceLocation structureId) throws IOException {
        Files.createDirectories(packRoot);
        JsonObject root = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", 15);
        pack.addProperty("description", "Auto Jigsaw Export: " + structureId);
        root.add("pack", pack);
        writeJson(packRoot.resolve("pack.mcmeta"), root);
    }

    public static void writeManifest(JigsawExportPlan plan,
                                     Path packRoot,
                                     int targetPieceSize,
                                     boolean includeEntities) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("structure", plan.structureId().toString());
        json.addProperty("start_pool", plan.poolLocation(plan.rootPiece()).toString());
        json.addProperty("target_piece_size", targetPieceSize);
        json.addProperty("piece_count", plan.pieces().size());
        json.addProperty("recommended_jigsaw_depth", plan.recommendedDepth());
        json.addProperty("include_entities", includeEntities);

        JsonArray min = new JsonArray();
        min.add(plan.min().getX());
        min.add(plan.min().getY());
        min.add(plan.min().getZ());
        json.add("min", min);

        JsonArray max = new JsonArray();
        max.add(plan.max().getX());
        max.add(plan.max().getY());
        max.add(plan.max().getZ());
        json.add("max", max);

        JsonArray pieces = new JsonArray();
        for (JigsawExportPlan.Piece piece : plan.pieces()) {
            JsonObject pieceJson = new JsonObject();
            pieceJson.addProperty("structure", plan.structureLocation(piece).toString());
            pieceJson.addProperty("pool", plan.poolLocation(piece).toString());
            pieceJson.addProperty("grid_x", piece.gridX());
            pieceJson.addProperty("grid_y", piece.gridY());
            pieceJson.addProperty("grid_z", piece.gridZ());

            JsonArray offset = new JsonArray();
            offset.add(piece.origin().getX() - plan.min().getX());
            offset.add(piece.origin().getY() - plan.min().getY());
            offset.add(piece.origin().getZ() - plan.min().getZ());
            pieceJson.add("offset", offset);

            JsonArray size = new JsonArray();
            size.add(piece.size().getX());
            size.add(piece.size().getY());
            size.add(piece.size().getZ());
            pieceJson.add("size", size);
            pieces.add(pieceJson);
        }
        json.add("pieces", pieces);

        Path manifestPath = packRoot.resolve("data")
                .resolve(plan.structureId().getNamespace())
                .resolve("auto_jigsaw_export")
                .resolve(plan.structureId().getPath() + ".json");
        writeJson(manifestPath, json);

        String readme = "Auto Jigsaw Exporter\n"
                + "Structure: " + plan.structureId() + "\n"
                + "Start pool: " + plan.poolLocation(plan.rootPiece()) + "\n"
                + "Pieces: " + plan.pieces().size() + "\n"
                + "Recommended jigsaw depth: " + plan.recommendedDepth() + "\n"
                + "The generated pools are deterministic: every pool contains exactly one piece.\n"
                + "Copy data/<namespace>/structures and data/<namespace>/worldgen/template_pool into your target mod or datapack.\n";
        Files.writeString(packRoot.resolve("AUTO_JIGSAW_EXPORT.txt"), readme, StandardCharsets.UTF_8);
    }

    private static void writePoolJson(JigsawExportPlan plan,
                                      JigsawExportPlan.Piece piece,
                                      Path packRoot) throws IOException {
        ResourceLocation poolLocation = plan.poolLocation(piece);
        ResourceLocation structureLocation = plan.structureLocation(piece);

        JsonObject root = new JsonObject();
        root.addProperty("name", poolLocation.toString());
        root.addProperty("fallback", "minecraft:empty");

        JsonArray elements = new JsonArray();
        JsonObject weighted = new JsonObject();
        weighted.addProperty("weight", 1);

        JsonObject element = new JsonObject();
        element.addProperty("element_type", "minecraft:single_pool_element");
        element.addProperty("location", structureLocation.toString());
        element.addProperty("processors", "minecraft:empty");
        element.addProperty("projection", "rigid");
        weighted.add("element", element);
        elements.add(weighted);
        root.add("elements", elements);

        Path poolPath = packRoot.resolve("data")
                .resolve(poolLocation.getNamespace())
                .resolve("worldgen")
                .resolve("template_pool")
                .resolve(poolLocation.getPath() + ".json");
        writeJson(poolPath, root);
    }

    private static void addOutgoingConnector(ServerLevel level,
                                             CompoundTag structureTag,
                                             JigsawExportPlan plan,
                                             JigsawExportPlan.Piece piece,
                                             JigsawExportPlan.Edge edge) {
        Direction direction = edge.direction();
        ConnectorPair pair = connectorPair(level, plan, edge);
        BlockPos localPos = pair.parentLocal();
        BlockPos worldPos = piece.origin().offset(localPos);
        String finalState = finalState(level, worldPos);
        ResourceLocation link = plan.linkLocation(edge);
        JigsawExportPlan.Piece child = plan.pieces().get(edge.childIndex());

        CompoundTag blockEntity = new CompoundTag();
        blockEntity.putString("name", plan.structureId() + "/out");
        blockEntity.putString("target", link.toString());
        blockEntity.putString("pool", plan.poolLocation(child).toString());
        blockEntity.putString("final_state", finalState);
        blockEntity.putString("joint", "aligned");

        putJigsawBlock(structureTag, localPos, orientation(direction), blockEntity);
    }

    private static void addIncomingConnector(ServerLevel level,
                                             CompoundTag structureTag,
                                             JigsawExportPlan plan,
                                             JigsawExportPlan.Piece piece,
                                             JigsawExportPlan.Edge edge) {
        Direction direction = edge.direction().getOpposite();
        ConnectorPair pair = connectorPair(level, plan, edge);
        BlockPos localPos = pair.childLocal();
        BlockPos worldPos = piece.origin().offset(localPos);
        String finalState = finalState(level, worldPos);
        ResourceLocation link = plan.linkLocation(edge);

        CompoundTag blockEntity = new CompoundTag();
        blockEntity.putString("name", link.toString());
        blockEntity.putString("target", "minecraft:empty");
        blockEntity.putString("pool", "minecraft:empty");
        blockEntity.putString("final_state", finalState);
        blockEntity.putString("joint", "aligned");

        putJigsawBlock(structureTag, localPos, orientation(direction), blockEntity);
    }

    private static void replaceSelectionMarker(CompoundTag structureTag,
                                               JigsawExportPlan.Piece piece,
                                               BlockPos markerPos) {
        if (!inside(piece, markerPos)) {
            return;
        }
        BlockPos local = markerPos.subtract(piece.origin());
        CompoundTag airState = new CompoundTag();
        airState.putString("Name", "minecraft:air");
        putBlock(structureTag, local, airState, null);
    }

    private static boolean inside(JigsawExportPlan.Piece piece, BlockPos pos) {
        Vec3i size = piece.size();
        BlockPos origin = piece.origin();
        return pos.getX() >= origin.getX() && pos.getX() < origin.getX() + size.getX()
                && pos.getY() >= origin.getY() && pos.getY() < origin.getY() + size.getY()
                && pos.getZ() >= origin.getZ() && pos.getZ() < origin.getZ() + size.getZ();
    }

    private static ConnectorPair connectorPair(ServerLevel level,
                                               JigsawExportPlan plan,
                                               JigsawExportPlan.Edge edge) {
        JigsawExportPlan.Piece parent = plan.pieces().get(edge.parentIndex());
        JigsawExportPlan.Piece child = plan.pieces().get(edge.childIndex());
        Direction direction = edge.direction();

        List<ConnectorCandidate> candidates = new ArrayList<>();
        switch (direction.getAxis()) {
            case X -> {
                int maxY = Math.min(parent.size().getY(), child.size().getY());
                int maxZ = Math.min(parent.size().getZ(), child.size().getZ());
                double cy = (maxY - 1) / 2.0D;
                double cz = (maxZ - 1) / 2.0D;
                for (int y = 0; y < maxY; y++) {
                    for (int z = 0; z < maxZ; z++) {
                        BlockPos parentLocal = new BlockPos(
                                direction == Direction.EAST ? parent.size().getX() - 1 : 0,
                                y,
                                z);
                        BlockPos childLocal = new BlockPos(
                                direction == Direction.EAST ? 0 : child.size().getX() - 1,
                                y,
                                z);
                        candidates.add(candidate(level, parent, child, parentLocal, childLocal,
                                squaredDistance(y, z, cy, cz)));
                    }
                }
            }
            case Y -> {
                int maxX = Math.min(parent.size().getX(), child.size().getX());
                int maxZ = Math.min(parent.size().getZ(), child.size().getZ());
                double cx = (maxX - 1) / 2.0D;
                double cz = (maxZ - 1) / 2.0D;
                for (int x = 0; x < maxX; x++) {
                    for (int z = 0; z < maxZ; z++) {
                        BlockPos parentLocal = new BlockPos(
                                x,
                                direction == Direction.UP ? parent.size().getY() - 1 : 0,
                                z);
                        BlockPos childLocal = new BlockPos(
                                x,
                                direction == Direction.UP ? 0 : child.size().getY() - 1,
                                z);
                        candidates.add(candidate(level, parent, child, parentLocal, childLocal,
                                squaredDistance(x, z, cx, cz)));
                    }
                }
            }
            case Z -> {
                int maxX = Math.min(parent.size().getX(), child.size().getX());
                int maxY = Math.min(parent.size().getY(), child.size().getY());
                double cx = (maxX - 1) / 2.0D;
                double cy = (maxY - 1) / 2.0D;
                for (int x = 0; x < maxX; x++) {
                    for (int y = 0; y < maxY; y++) {
                        BlockPos parentLocal = new BlockPos(
                                x,
                                y,
                                direction == Direction.SOUTH ? parent.size().getZ() - 1 : 0);
                        BlockPos childLocal = new BlockPos(
                                x,
                                y,
                                direction == Direction.SOUTH ? 0 : child.size().getZ() - 1);
                        candidates.add(candidate(level, parent, child, parentLocal, childLocal,
                                squaredDistance(x, y, cx, cy)));
                    }
                }
            }
        }

        return candidates.stream()
                .filter(candidate -> !candidate.hasBlockEntity())
                .min(Comparator.comparingInt(ConnectorCandidate::airPenalty)
                        .thenComparingDouble(ConnectorCandidate::distance))
                .map(candidate -> new ConnectorPair(candidate.parentLocal(), candidate.childLocal()))
                .orElseGet(() -> {
                    ConnectorCandidate fallback = candidates.stream()
                            .min(Comparator.comparingDouble(ConnectorCandidate::distance))
                            .orElseThrow();
                    return new ConnectorPair(fallback.parentLocal(), fallback.childLocal());
                });
    }

    private static ConnectorCandidate candidate(ServerLevel level,
                                                JigsawExportPlan.Piece parent,
                                                JigsawExportPlan.Piece child,
                                                BlockPos parentLocal,
                                                BlockPos childLocal,
                                                double distance) {
        BlockPos parentWorld = parent.origin().offset(parentLocal);
        BlockPos childWorld = child.origin().offset(childLocal);
        boolean hasBlockEntity = level.getBlockEntity(parentWorld) != null
                || level.getBlockEntity(childWorld) != null;
        int airPenalty = (level.getBlockState(parentWorld).isAir() ? 0 : 1)
                + (level.getBlockState(childWorld).isAir() ? 0 : 1);
        return new ConnectorCandidate(parentLocal, childLocal, hasBlockEntity, airPenalty, distance);
    }

    private static double squaredDistance(double a, double b, double centerA, double centerB) {
        double da = a - centerA;
        double db = b - centerB;
        return da * da + db * db;
    }

    private static String orientation(Direction direction) {
        return switch (direction) {
            case EAST -> "east_up";
            case WEST -> "west_up";
            case NORTH -> "north_up";
            case SOUTH -> "south_up";
            case UP -> "up_north";
            case DOWN -> "down_north";
        };
    }

    private static void putJigsawBlock(CompoundTag structureTag,
                                       BlockPos localPos,
                                       String orientation,
                                       CompoundTag blockEntity) {
        CompoundTag blockState = new CompoundTag();
        blockState.putString("Name", "minecraft:jigsaw");
        CompoundTag properties = new CompoundTag();
        properties.putString("orientation", orientation);
        blockState.put("Properties", properties);
        putBlock(structureTag, localPos, blockState, blockEntity);
    }

    private static void putBlock(CompoundTag structureTag,
                                 BlockPos localPos,
                                 CompoundTag state,
                                 CompoundTag blockEntity) {
        ListTag palette = structureTag.getList("palette", Tag.TAG_COMPOUND);
        int stateIndex = findOrAddPaletteState(palette, state);

        ListTag blocks = structureTag.getList("blocks", Tag.TAG_COMPOUND);
        for (int i = 0; i < blocks.size(); i++) {
            CompoundTag block = blocks.getCompound(i);
            if (samePosition(block, localPos)) {
                block.putInt("state", stateIndex);
                if (blockEntity == null) {
                    block.remove("nbt");
                } else {
                    block.put("nbt", blockEntity.copy());
                }
                return;
            }
        }

        CompoundTag block = new CompoundTag();
        block.put("pos", new IntArrayTag(new int[]{localPos.getX(), localPos.getY(), localPos.getZ()}));
        block.putInt("state", stateIndex);
        if (blockEntity != null) {
            block.put("nbt", blockEntity.copy());
        }
        blocks.add(block);
    }

    private static int findOrAddPaletteState(ListTag palette, CompoundTag state) {
        for (int i = 0; i < palette.size(); i++) {
            if (palette.getCompound(i).equals(state)) {
                return i;
            }
        }
        palette.add(state.copy());
        return palette.size() - 1;
    }

    private static boolean samePosition(CompoundTag block, BlockPos pos) {
        int[] values = block.getIntArray("pos");
        return values.length == 3
                && values[0] == pos.getX()
                && values[1] == pos.getY()
                && values[2] == pos.getZ();
    }

    private static String finalState(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(ModBlocks.START_MARKER.get()) || state.is(ModBlocks.END_MARKER.get())) {
            return "minecraft:air";
        }

        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        if (id == null) {
            return "minecraft:air";
        }

        StringBuilder builder = new StringBuilder(id.toString());
        if (!state.getProperties().isEmpty()) {
            builder.append('[');
            boolean first = true;
            for (Property<?> property : state.getProperties()) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                builder.append(property.getName()).append('=').append(propertyValueName(state, property));
            }
            builder.append(']');
        }
        return builder.toString();
    }

    private static <T extends Comparable<T>> String propertyValueName(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    private record ConnectorPair(BlockPos parentLocal, BlockPos childLocal) {
    }

    private record ConnectorCandidate(BlockPos parentLocal,
                                      BlockPos childLocal,
                                      boolean hasBlockEntity,
                                      int airPenalty,
                                      double distance) {
    }

    private static void writeJson(Path path, JsonObject json) throws IOException {
        Files.createDirectories(path.getParent());
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(json, writer);
        }
    }
}
