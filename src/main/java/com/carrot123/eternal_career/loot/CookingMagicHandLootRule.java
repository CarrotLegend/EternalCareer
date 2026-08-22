package com.carrot123.eternal_career.loot;

import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/** Immutable data-pack rule for Cooking Magic Hand bonus loot. */
public record CookingMagicHandLootRule(
        ResourceLocation source,
        List<Target> targets,
        ResourceLocation lootTable,
        int rolls
) {
    public CookingMagicHandLootRule {
        targets = List.copyOf(targets);
    }

    /** Targets are entity IDs or entity-type tags and are ORed within one rule. */
    public boolean matches(EntityType<?> entityType, Registry<EntityType<?>> entityTypes) {
        ResourceLocation entityId = entityTypes.getKey(entityType);
        return targets.stream().anyMatch(target -> target.matches(entityType, entityId, entityTypes));
    }

    public record Target(ResourceLocation id, boolean tag) {
        public boolean matches(
                EntityType<?> entityType,
                ResourceLocation entityId,
                Registry<EntityType<?>> entityTypes
        ) {
            if (!tag) {
                return id.equals(entityId);
            }

            TagKey<EntityType<?>> tagKey = TagKey.create(entityTypes.key(), id);
            return entityTypes.wrapAsHolder(entityType).is(tagKey);
        }

        public TagKey<EntityType<?>> asTagKey(Registry<EntityType<?>> entityTypes) {
            return TagKey.create(entityTypes.key(), id);
        }
    }
}
