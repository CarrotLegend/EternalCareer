package com.carrot123.auto_jigsaw_exporter.export;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JigsawExportPlan {
    private final ResourceLocation structureId;
    private final BlockPos startMarker;
    private final BlockPos endMarker;
    private final BlockPos min;
    private final BlockPos max;
    private final AxisSlices xSlices;
    private final AxisSlices ySlices;
    private final AxisSlices zSlices;
    private final List<Piece> pieces;
    private final List<Edge> edges;
    private final int rootPieceIndex;
    private final int recommendedDepth;

    private JigsawExportPlan(ResourceLocation structureId,
                             BlockPos startMarker,
                             BlockPos endMarker,
                             BlockPos min,
                             BlockPos max,
                             AxisSlices xSlices,
                             AxisSlices ySlices,
                             AxisSlices zSlices,
                             List<Piece> pieces,
                             List<Edge> edges,
                             int rootPieceIndex,
                             int recommendedDepth) {
        this.structureId = structureId;
        this.startMarker = startMarker;
        this.endMarker = endMarker;
        this.min = min;
        this.max = max;
        this.xSlices = xSlices;
        this.ySlices = ySlices;
        this.zSlices = zSlices;
        this.pieces = List.copyOf(pieces);
        this.edges = List.copyOf(edges);
        this.rootPieceIndex = rootPieceIndex;
        this.recommendedDepth = recommendedDepth;
    }

    public static JigsawExportPlan create(ResourceLocation structureId,
                                          BlockPos startMarker,
                                          BlockPos endMarker,
                                          int targetPieceSize) {
        int minX = Math.min(startMarker.getX(), endMarker.getX());
        int minY = Math.min(startMarker.getY(), endMarker.getY());
        int minZ = Math.min(startMarker.getZ(), endMarker.getZ());
        int maxX = Math.max(startMarker.getX(), endMarker.getX());
        int maxY = Math.max(startMarker.getY(), endMarker.getY());
        int maxZ = Math.max(startMarker.getZ(), endMarker.getZ());

        BlockPos min = new BlockPos(minX, minY, minZ);
        BlockPos max = new BlockPos(maxX, maxY, maxZ);

        AxisSlices xs = AxisSlices.create(maxX - minX + 1, targetPieceSize);
        AxisSlices ys = AxisSlices.create(maxY - minY + 1, targetPieceSize);
        AxisSlices zs = AxisSlices.create(maxZ - minZ + 1, targetPieceSize);

        List<Piece> pieces = new ArrayList<>();
        Map<GridPos, Integer> indices = new HashMap<>();

        for (int y = 0; y < ys.count(); y++) {
            for (int z = 0; z < zs.count(); z++) {
                for (int x = 0; x < xs.count(); x++) {
                    BlockPos origin = min.offset(xs.offset(x), ys.offset(y), zs.offset(z));
                    Vec3i size = new Vec3i(xs.length(x), ys.length(y), zs.length(z));
                    int index = pieces.size();
                    pieces.add(new Piece(index, x, y, z, origin, size));
                    indices.put(new GridPos(x, y, z), index);
                }
            }
        }

        int rootX = startMarker.getX() == minX ? 0 : xs.count() - 1;
        int rootY = startMarker.getY() == minY ? 0 : ys.count() - 1;
        int rootZ = startMarker.getZ() == minZ ? 0 : zs.count() - 1;
        int rootPiece = indices.get(new GridPos(rootX, rootY, rootZ));

        List<Edge> edges = new ArrayList<>();
        int maxDepth = 0;

        for (Piece child : pieces) {
            if (child.index() == rootPiece) {
                continue;
            }

            int parentX = child.gridX();
            int parentY = child.gridY();
            int parentZ = child.gridZ();

            if (parentX != rootX) {
                parentX += Integer.compare(rootX, parentX);
            } else if (parentZ != rootZ) {
                parentZ += Integer.compare(rootZ, parentZ);
            } else if (parentY != rootY) {
                parentY += Integer.compare(rootY, parentY);
            }

            int parentIndex = indices.get(new GridPos(parentX, parentY, parentZ));
            Piece parent = pieces.get(parentIndex);
            Direction direction = directionBetween(parent, child);
            edges.add(new Edge(parentIndex, child.index(), direction));

            int depth = Math.abs(child.gridX() - rootX)
                    + Math.abs(child.gridY() - rootY)
                    + Math.abs(child.gridZ() - rootZ);
            maxDepth = Math.max(maxDepth, depth);
        }

        return new JigsawExportPlan(
                structureId,
                startMarker.immutable(),
                endMarker.immutable(),
                min,
                max,
                xs,
                ys,
                zs,
                pieces,
                edges,
                rootPiece,
                maxDepth);
    }

    private static Direction directionBetween(Piece parent, Piece child) {
        int dx = Integer.compare(child.gridX(), parent.gridX());
        int dy = Integer.compare(child.gridY(), parent.gridY());
        int dz = Integer.compare(child.gridZ(), parent.gridZ());
        if (dx > 0) return Direction.EAST;
        if (dx < 0) return Direction.WEST;
        if (dy > 0) return Direction.UP;
        if (dy < 0) return Direction.DOWN;
        if (dz > 0) return Direction.SOUTH;
        if (dz < 0) return Direction.NORTH;
        throw new IllegalArgumentException("Parent and child pieces are not adjacent");
    }

    public ResourceLocation structureId() {
        return structureId;
    }

    public BlockPos startMarker() {
        return startMarker;
    }

    public BlockPos endMarker() {
        return endMarker;
    }

    public BlockPos min() {
        return min;
    }

    public BlockPos max() {
        return max;
    }

    public AxisSlices xSlices() {
        return xSlices;
    }

    public AxisSlices ySlices() {
        return ySlices;
    }

    public AxisSlices zSlices() {
        return zSlices;
    }

    public List<Piece> pieces() {
        return pieces;
    }

    public List<Edge> edges() {
        return edges;
    }

    public Piece rootPiece() {
        return pieces.get(rootPieceIndex);
    }

    public int rootPieceIndex() {
        return rootPieceIndex;
    }

    public int recommendedDepth() {
        return recommendedDepth;
    }

    public List<Edge> edgesForPiece(int pieceIndex) {
        return edges.stream()
                .filter(edge -> edge.parentIndex() == pieceIndex || edge.childIndex() == pieceIndex)
                .toList();
    }

    public ResourceLocation structureLocation(Piece piece) {
        return new ResourceLocation(
                structureId.getNamespace(),
                structureId.getPath() + "/piece_" + piece.gridX() + "_" + piece.gridY() + "_" + piece.gridZ());
    }

    public ResourceLocation poolLocation(Piece piece) {
        return structureLocation(piece);
    }

    public ResourceLocation linkLocation(Edge edge) {
        Piece parent = pieces.get(edge.parentIndex());
        Piece child = pieces.get(edge.childIndex());
        return new ResourceLocation(
                structureId.getNamespace(),
                structureId.getPath() + "/link_"
                        + parent.gridX() + "_" + parent.gridY() + "_" + parent.gridZ()
                        + "_to_"
                        + child.gridX() + "_" + child.gridY() + "_" + child.gridZ());
    }

    public record Piece(int index, int gridX, int gridY, int gridZ,
                        BlockPos origin, Vec3i size) {
    }

    public record Edge(int parentIndex, int childIndex, Direction direction) {
    }

    private record GridPos(int x, int y, int z) {
    }

    public static final class AxisSlices {
        private final int[] offsets;
        private final int[] lengths;

        private AxisSlices(int[] offsets, int[] lengths) {
            this.offsets = offsets;
            this.lengths = lengths;
        }

        public static AxisSlices create(int totalLength, int targetSize) {
            if (totalLength <= 0) {
                throw new IllegalArgumentException("Axis length must be positive");
            }
            if (totalLength <= targetSize) {
                return new AxisSlices(new int[]{0}, new int[]{totalLength});
            }

            int count = (totalLength + targetSize - 1) / targetSize;
            int remainder = totalLength - targetSize * (count - 1);

            if (remainder > 0 && remainder < 3 && count > 1) {
                count--;
            }

            int[] offsets = new int[count];
            int[] lengths = new int[count];
            int cursor = 0;
            for (int i = 0; i < count; i++) {
                offsets[i] = cursor;
                if (i == count - 1) {
                    lengths[i] = totalLength - cursor;
                } else {
                    lengths[i] = targetSize;
                }
                cursor += lengths[i];
            }
            return new AxisSlices(offsets, lengths);
        }

        public int count() {
            return lengths.length;
        }

        public int offset(int index) {
            return offsets[index];
        }

        public int length(int index) {
            return lengths[index];
        }
    }
}
