# Auto Jigsaw Exporter

Forge 1.20.1 / Forge 47.4.20 / Java 17 development tool for exporting very large built structures into deterministic jigsaw template pieces.

## Workflow

1. Get `auto_jigsaw_exporter:start_marker` and `auto_jigsaw_exporter:end_marker` from the Functional Blocks creative tab.
2. Place the start marker at one corner of the cuboid. It becomes your active start marker automatically.
3. Place the end marker at the opposite corner. It is automatically linked to the selected start marker.
4. Run:

   `/autojigsaw export <namespace> <name>`

   Default piece size is 32 and entities are not included.

   Optional form:

   `/autojigsaw export <namespace> <name> <piece_size 8..48> <include_entities true|false>`

5. The exporter writes one structure piece per server tick into:

   `<world>/datapacks/auto_jigsaw_<namespace>_<name>/`

6. Each piece gets a deterministic template pool containing only itself. Adjacent pieces are linked by generated jigsaw block data. The original block at every generated connector is stored as `final_state`.
7. Copy the generated `data/<namespace>/structures` and `data/<namespace>/worldgen/template_pool` folders into the target mod/datapack.

## Re-linking markers

Right-click a start marker to make it active, then right-click an end marker to bind it. The end position is persisted in the start marker block entity.

## Generated graph

The exporter does not create a single linear chain. It generates a rooted 3D spanning tree from the start corner. Maximum required jigsaw depth is approximately the Manhattan distance across the piece grid rather than the total number of pieces.

The generated manifest is stored at:

`data/<namespace>/auto_jigsaw_export/<name>.json`

It contains the start pool, every piece offset and size, total piece count, and the recommended jigsaw depth.

## Notes

This mod exports the jigsaw assets. For extremely large structures, vanilla `JigsawStructure` depth/radius limits may still be too small; use a custom WDA-style jigsaw structure generator when integrating the exported assets into a production mod.
