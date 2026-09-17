package com.carrot123.eternal_career.soul;

import com.carrot123.eternal_career.registry.ModTags;
import com.mojang.logging.LogUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public final class SoulSetManager {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static volatile List<SoulSetDefinition> loaded = List.of();
    private static volatile List<SoulSetDefinition> active = List.of();
    private SoulSetManager() {}

    public static void replace(List<SoulSetDefinition> definitions) {
        loaded = List.copyOf(definitions);
        active = List.of(); // Never continue using stale definitions before tag binding.
    }
    public static void validateTags(RegistryAccess access) {
        var items = access.registryOrThrow(Registries.ITEM);
        active = loaded.stream().filter(d -> {
            boolean exists = items.getTag(d.armorTag()).isPresent();
            if (!exists) LogUtils.getLogger().error("Skipping soul set {}: missing tag #{}", d.id(), d.armorTag().location());
            return exists;
        }).toList();
    }
    public static Optional<SoulSetDefinition> findActiveSoulSet(Player player) {
        List<Set<TagKey<Item>>> armorTags = java.util.Arrays.stream(SLOTS)
                .map(player::getItemBySlot)
                .map(stack -> stack.isEmpty() ? Set.<TagKey<Item>>of()
                        : stack.getTags().collect(java.util.stream.Collectors.toSet())).toList();
        return selectSet(active, armorTags);
    }
    static Optional<SoulSetDefinition> selectSet(List<SoulSetDefinition> definitions,
                                                List<Set<TagKey<Item>>> armorTags) {
        if (armorTags.size() != 4 || armorTags.stream().anyMatch(tags -> !tags.contains(ModTags.Items.SOUL_ARMOR)))
            return Optional.empty();
        return definitions.stream().filter(d -> armorTags.stream().allMatch(tags -> tags.contains(d.armorTag())))
                .sorted(Comparator.comparingInt(SoulSetDefinition::maxSoul).reversed()
                        .thenComparing(d -> d.id().toString())).findFirst();
    }
    public static void clear() { loaded = List.of(); active = List.of(); }
}
