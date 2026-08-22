package com.carrot123.eternal_career.loot;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

/** Loads cooking_magic_hand_loot JSON rules from every data-pack namespace. */
public final class CookingMagicHandLootReloadListener extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final String DIRECTORY = "cooking_magic_hand_loot";

    private final LootDataManager lootData;

    public CookingMagicHandLootReloadListener(LootDataManager lootData) {
        super(GSON, DIRECTORY);
        this.lootData = lootData;
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> resources,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        List<CookingMagicHandLootRule> rules = new ArrayList<>();
        resources.forEach((source, json) -> {
            try {
                CookingMagicHandLootRule rule = parseRule(source, json);
                if (lootData.getElement(new LootDataId<>(LootDataType.TABLE, rule.lootTable())) == null) {
                    throw fieldError("loot_table", "loot table does not exist: " + rule.lootTable());
                }
                rules.add(rule);
            } catch (RuntimeException exception) {
                LOGGER.error(
                        "Skipping Cooking Magic Hand loot rule {}: {}",
                        source,
                        exception.getMessage());
            }
        });

        CookingMagicHandLootManager.replaceRules(rules);
        LOGGER.info("Loaded {} Cooking Magic Hand loot rule(s)", rules.size());
    }

    private static CookingMagicHandLootRule parseRule(ResourceLocation source, JsonElement json) {
        if (!json.isJsonObject()) {
            throw fieldError("root", "must be a JSON object");
        }
        JsonObject object = json.getAsJsonObject();
        JsonElement targetsElement = required(object, "targets");
        if (!targetsElement.isJsonArray()) {
            throw fieldError("targets", "must be a non-empty string array");
        }
        JsonArray targetArray = targetsElement.getAsJsonArray();
        if (targetArray.isEmpty()) {
            throw fieldError("targets", "must not be empty");
        }

        List<CookingMagicHandLootRule.Target> targets = new ArrayList<>();
        for (int index = 0; index < targetArray.size(); index++) {
            JsonElement targetElement = targetArray.get(index);
            if (!targetElement.isJsonPrimitive()
                    || !targetElement.getAsJsonPrimitive().isString()) {
                throw fieldError("targets[" + index + "]", "must be a string");
            }
            String rawTarget = targetElement.getAsString();
            boolean tag = rawTarget.startsWith("#");
            String rawId = tag ? rawTarget.substring(1) : rawTarget;
            ResourceLocation targetId = ResourceLocation.tryParse(rawId);
            if (targetId == null) {
                throw fieldError("targets[" + index + "]", "invalid resource location: " + rawTarget);
            }
            if (!tag && !ForgeRegistries.ENTITY_TYPES.containsKey(targetId)) {
                throw fieldError("targets[" + index + "]", "entity type does not exist: " + targetId);
            }
            targets.add(new CookingMagicHandLootRule.Target(targetId, tag));
        }

        ResourceLocation lootTable = parseResourceLocation(object, "loot_table");
        JsonElement rollsElement = required(object, "rolls");
        if (!rollsElement.isJsonPrimitive() || !rollsElement.getAsJsonPrimitive().isNumber()) {
            throw fieldError("rolls", "must be an integer of at least 1");
        }
        int rolls;
        try {
            rolls = rollsElement.getAsInt();
        } catch (NumberFormatException exception) {
            throw fieldError("rolls", "must be an integer of at least 1");
        }
        if (rolls < 1 || rollsElement.getAsDouble() != rolls) {
            throw fieldError("rolls", "must be an integer of at least 1");
        }

        return new CookingMagicHandLootRule(source, targets, lootTable, rolls);
    }

    private static JsonElement required(JsonObject object, String field) {
        JsonElement value = object.get(field);
        if (value == null || value.isJsonNull()) {
            throw fieldError(field, "is required");
        }
        return value;
    }

    private static ResourceLocation parseResourceLocation(JsonObject object, String field) {
        JsonElement element = required(object, field);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
            throw fieldError(field, "must be a resource-location string");
        }
        ResourceLocation id = ResourceLocation.tryParse(element.getAsString());
        if (id == null) {
            throw fieldError(field, "invalid resource location: " + element.getAsString());
        }
        return id;
    }

    private static JsonParseException fieldError(String field, String message) {
        return new JsonParseException("field '" + field + "' " + message);
    }
}
