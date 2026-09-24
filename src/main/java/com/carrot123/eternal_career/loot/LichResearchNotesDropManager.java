package com.carrot123.eternal_career.loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

public final class LichResearchNotesDropManager extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().create();
    private static final String DIRECTORY = "lich_research_notes_drops";

    public static final LichResearchNotesDropManager INSTANCE =
            new LichResearchNotesDropManager();

    private volatile List<LichResearchNotesDropRule> rules = List.of();

    private LichResearchNotesDropManager() {
        super(GSON, DIRECTORY);
    }

    public List<LichResearchNotesDropRule> getRules() {
        return rules;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> objects,
            ResourceManager resourceManager, ProfilerFiller profiler) {
        List<LichResearchNotesDropRule> loaded = new ArrayList<>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : objects.entrySet()) {
            try {
                loaded.add(parseRule(entry.getKey(), entry.getValue()));
            } catch (Exception exception) {
                LOGGER.error("Failed to load lich research notes drop rule {}",
                        entry.getKey(), exception);
            }
        }
        rules = List.copyOf(loaded);
        LOGGER.info("Loaded {} lich research notes drop rules", rules.size());
    }

    private static LichResearchNotesDropRule parseRule(ResourceLocation fileId,
            JsonElement element) {
        JsonObject json = GsonHelper.convertToJsonObject(element,
                "lich research notes drop rule");
        String dropString = GsonHelper.getAsString(json, "drop");
        ResourceLocation dropId = dropString.contains(":")
                ? ResourceLocation.tryParse(dropString) : null;
        if (dropId == null) {
            throw new IllegalArgumentException("Invalid fully qualified drop id '"
                    + dropString + "' in " + fileId);
        }
        Item drop = ForgeRegistries.ITEMS.getValue(dropId);
        if (drop == null || drop == Items.AIR) {
            throw new IllegalArgumentException("Unknown drop item " + dropId + " in " + fileId);
        }

        float chance = GsonHelper.getAsFloat(json, "chance", 1.0F);
        if (!Float.isFinite(chance)) {
            throw new IllegalArgumentException("Non-finite chance in " + fileId);
        }
        chance = Math.max(0.0F, Math.min(1.0F, chance));
        int count = Math.max(1, GsonHelper.getAsInt(json, "count", 1));

        JsonArray targets = GsonHelper.getAsJsonArray(json, "targets");
        List<ResourceLocation> entityIds = new ArrayList<>();
        List<TagKey<EntityType<?>>> entityTags = new ArrayList<>();
        for (JsonElement targetElement : targets) {
            if (!targetElement.isJsonPrimitive()
                    || !targetElement.getAsJsonPrimitive().isString()) {
                LOGGER.warn("Ignoring non-string target in lich research notes rule {}", fileId);
                continue;
            }
            String target = targetElement.getAsString();
            if (target.isBlank()) {
                continue;
            }
            boolean isTag = target.startsWith("#");
            ResourceLocation targetId = ResourceLocation.tryParse(
                    isTag ? target.substring(1) : target);
            if (targetId == null) {
                LOGGER.warn("Ignoring invalid entity target '{}' in {}", target, fileId);
                continue;
            }
            if (isTag) {
                entityTags.add(TagKey.create(Registries.ENTITY_TYPE, targetId));
            } else {
                entityIds.add(targetId);
            }
        }
        if (entityIds.isEmpty() && entityTags.isEmpty()) {
            throw new IllegalArgumentException("No valid entity targets in " + fileId);
        }
        return new LichResearchNotesDropRule(drop, entityIds, entityTags, chance, count);
    }
}
