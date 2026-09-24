package com.carrot123.eternal_career.loot;

import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public record LichResearchNotesDropRule(Item drop,
        List<ResourceLocation> entityIds,
        List<TagKey<EntityType<?>>> entityTags,
        float chance,
        int count) {
    public LichResearchNotesDropRule {
        entityIds = List.copyOf(entityIds);
        entityTags = List.copyOf(entityTags);
    }

    public boolean matches(LivingEntity entity) {
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        if (entityId != null && entityIds.contains(entityId)) {
            return true;
        }
        for (TagKey<EntityType<?>> tag : entityTags) {
            if (entity.getType().is(tag)) {
                return true;
            }
        }
        return false;
    }
}
