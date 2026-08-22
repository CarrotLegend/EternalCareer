package com.carrot123.eternal_career.loot;

import com.mojang.logging.LogUtils;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import org.slf4j.Logger;

/** Atomically publishes immutable rule snapshots for the death-event hot path. */
public final class CookingMagicHandLootManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static volatile List<CookingMagicHandLootRule> loadedRules = List.of();
    private static volatile List<CookingMagicHandLootRule> activeRules = List.of();

    private CookingMagicHandLootManager() {
    }

    public static void replaceRules(List<CookingMagicHandLootRule> rules) {
        List<CookingMagicHandLootRule> immutable = List.copyOf(rules);
        loadedRules = immutable;
        activeRules = immutable;
    }

    /** Removes any whole rule that names an entity-type tag absent after tag binding. */
    public static void validateTags(RegistryAccess registryAccess) {
        Registry<EntityType<?>> entityTypes = registryAccess.registryOrThrow(Registries.ENTITY_TYPE);
        List<CookingMagicHandLootRule> validated = loadedRules.stream()
                .filter(rule -> rule.targets().stream()
                        .filter(CookingMagicHandLootRule.Target::tag)
                        .allMatch(target -> {
                            boolean exists = entityTypes.getTag(target.asTagKey(entityTypes)).isPresent();
                            if (!exists) {
                                LOGGER.error(
                                        "Skipping Cooking Magic Hand loot rule {}: field 'targets' references missing entity tag #{}",
                                        rule.source(),
                                        target.id());
                            }
                            return exists;
                        }))
                .toList();
        activeRules = List.copyOf(validated);
    }

    public static List<CookingMagicHandLootRule> getMatchingRules(
            EntityType<?> entityType,
            RegistryAccess registryAccess
    ) {
        Registry<EntityType<?>> entityTypes = registryAccess.registryOrThrow(Registries.ENTITY_TYPE);
        return activeRules.stream()
                .filter(rule -> rule.matches(entityType, entityTypes))
                .toList();
    }
}
