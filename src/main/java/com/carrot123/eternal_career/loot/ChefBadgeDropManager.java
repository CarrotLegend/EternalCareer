package com.carrot123.eternal_career.loot;

import com.carrot123.eternal_career.EternalCareer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.mojang.logging.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public final class ChefBadgeDropManager
        extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER =
            LogUtils.getLogger();

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final String DIRECTORY =
            "chef_badge_drops";

    private static final Set<ResourceLocation> ALLOWED_BADGES =
            Set.of(
                    id("chef_apprentice_badge"),
                    id("intermediate_chef_badge"),
                    id("advanced_chef_badge"),
                    id("senior_technician_badge"),
                    id("master_chef_badge")
            );

    public static final ChefBadgeDropManager INSTANCE =
            new ChefBadgeDropManager();

    private volatile List<ChefBadgeDropRule> rules =
            List.of();

    private ChefBadgeDropManager() {
        super(
                GSON,
                DIRECTORY
        );
    }

    public List<ChefBadgeDropRule> getRules() {
        return rules;
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> objects,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        List<ChefBadgeDropRule> loaded =
                new ArrayList<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry
                : objects.entrySet()) {

            try {
                ChefBadgeDropRule rule =
                        parseRule(
                                entry.getKey(),
                                entry.getValue()
                        );

                if (rule != null) {
                    loaded.add(rule);
                }

            } catch (Exception exception) {

                LOGGER.error(
                        "Failed to load chef badge drop rule {}",
                        entry.getKey(),
                        exception
                );
            }
        }

        rules =
                Collections.unmodifiableList(
                        loaded
                );

        LOGGER.info(
                "Loaded {} chef badge drop rules",
                rules.size()
        );
    }

    private static ChefBadgeDropRule parseRule(
            ResourceLocation fileId,
            JsonElement element
    ) {
        JsonObject json =
                GsonHelper.convertToJsonObject(
                        element,
                        "chef badge drop rule"
                );

        String badgeString =
                GsonHelper.getAsString(
                        json,
                        "badge"
                );

        ResourceLocation badgeId =
                new ResourceLocation(
                        badgeString
                );

        if (!ALLOWED_BADGES.contains(badgeId)) {
            throw new IllegalArgumentException(
                    "Unsupported badge id "
                            + badgeId
                            + " in "
                            + fileId
            );
        }

        Item badge =
                ForgeRegistries.ITEMS
                        .getValue(
                                badgeId
                        );

        if (badge == null
                || badge == Items.AIR) {

            throw new IllegalArgumentException(
                    "Unknown badge "
                            + badgeId
                            + " in "
                            + fileId
            );
        }

        float chance =
                GsonHelper.getAsFloat(
                        json,
                        "chance",
                        1.0F
                );

        chance =
                Math.max(
                        0.0F,
                        Math.min(
                                1.0F,
                                chance
                        )
                );

        int count =
                Math.max(
                        1,
                        GsonHelper.getAsInt(
                                json,
                                "count",
                                1
                        )
                );

        JsonArray targets =
                GsonHelper.getAsJsonArray(
                        json,
                        "targets"
                );

        List<ResourceLocation> entityIds =
                new ArrayList<>();

        List<TagKey<EntityType<?>>> entityTags =
                new ArrayList<>();

        for (JsonElement targetElement
                : targets) {

            if (!targetElement.isJsonPrimitive()
                    || !targetElement
                    .getAsJsonPrimitive()
                    .isString()) {

                LOGGER.warn(
                        "Ignoring non-string target in chef badge rule {}",
                        fileId
                );

                continue;
            }

            String target =
                    targetElement.getAsString();

            if (target == null
                    || target.isBlank()) {

                continue;
            }

            if (target.startsWith("#")) {

                String tagString =
                        target.substring(1);

                ResourceLocation tagId =
                        ResourceLocation.tryParse(
                                tagString
                        );

                if (tagId == null) {

                    LOGGER.warn(
                            "Ignoring invalid entity tag '{}' in {}",
                            target,
                            fileId
                    );

                    continue;
                }

                entityTags.add(
                        TagKey.create(
                                Registries.ENTITY_TYPE,
                                tagId
                        )
                );

                continue;
            }

            ResourceLocation entityId =
                    ResourceLocation.tryParse(
                            target
                    );

            if (entityId == null) {

                LOGGER.warn(
                        "Ignoring invalid entity id '{}' in {}",
                        target,
                        fileId
                );

                continue;
            }

            entityIds.add(
                    entityId
            );
        }

        if (entityIds.isEmpty()
                && entityTags.isEmpty()) {

            throw new IllegalArgumentException(
                    "Chef badge drop rule "
                            + fileId
                            + " contains no valid targets"
            );
        }

        return new ChefBadgeDropRule(
                badge,
                List.copyOf(entityIds),
                List.copyOf(entityTags),
                chance,
                count
        );
    }

    private static ResourceLocation id(
            String path
    ) {
        return new ResourceLocation(
                EternalCareer.MOD_ID,
                path
        );
    }
}