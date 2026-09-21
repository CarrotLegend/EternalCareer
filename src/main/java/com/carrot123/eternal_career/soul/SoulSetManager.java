package com.carrot123.eternal_career.soul;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.carrot123.eternal_career.registry.ModTags;
import com.mojang.logging.LogUtils;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class SoulSetManager {

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    private static volatile List<SoulSetDefinition> loaded = List.of();

    private SoulSetManager() {
    }

    public static void replace(List<SoulSetDefinition> definitions) {
        loaded = List.copyOf(definitions);

        LogUtils.getLogger().info(
                "Loaded {} soul set definition(s)",
                loaded.size()
        );
    }

    public static void validateTags(RegistryAccess access) {
        var itemRegistry = access.registryOrThrow(Registries.ITEM);

        for (SoulSetDefinition definition : loaded) {
            if (itemRegistry.getTag(definition.armorTag()).isEmpty()) {
                LogUtils.getLogger().error(
                        "Soul set {} references missing item tag #{}",
                        definition.id(),
                        definition.armorTag().location()
                );
            }
        }
    }

    public static Optional<SoulSetDefinition> findActiveSoulSet(Player player) {

        ItemStack[] armor = Arrays.stream(ARMOR_SLOTS)
                .map(player::getItemBySlot)
                .toArray(ItemStack[]::new);

        for (ItemStack stack : armor) {
            if (stack.isEmpty()) {
                return Optional.empty();
            }
        }

        for (ItemStack stack : armor) {
            if (!stack.is(ModTags.Items.SOUL_ARMOR)) {
                return Optional.empty();
            }
        }

        return loaded.stream()
                .filter(definition ->
                        Arrays.stream(armor)
                                .allMatch(stack ->
                                        stack.is(definition.armorTag())
                                )
                )

                .sorted(
                        Comparator
                                .comparingInt(SoulSetDefinition::maxSoul)
                                .reversed()
                                .thenComparing(
                                        definition ->
                                                definition.id().toString()
                                )
                )
                .findFirst();
    }

    public static void clear() {
        loaded = List.of();
    }
}
