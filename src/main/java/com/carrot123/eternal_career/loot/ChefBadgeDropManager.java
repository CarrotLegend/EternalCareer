package com.carrot123.eternal_career.loot;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModItems;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
import com.mojang.logging.LogUtils;

public final class ChefBadgeDropManager
        extends SimpleJsonResourceReloadListener {

    public static final ChefBadgeDropManager INSTANCE =
            new ChefBadgeDropManager();

    private static final Logger LOGGER =
            LogUtils.getLogger();

    private static final Gson GSON =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    private static final String DIRECTORY =
            "chef_badge_drops";

    private static final Set<ResourceLocation>
            ALLOWED_BADGES =
            Set.of(
                    id("chef_apprentice_badge"),
                    id("intermediate_chef_badge"),
                    id("advanced_chef_badge"),
                    id("senior_technician_badge"),
                    id("master_chef_badge")
            );

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

        for (Map.Entry<ResourceLocation, JsonElement>
                entry : objects.entrySet()) {

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

        ResourceLocation badgeId =
                new ResourceLocation(
                        GsonHelper.getAsString(
                                json,
                                "badge"
                        )
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
                        .getValue(badgeId);

        if (badge == null
                || badge == Items.AIR) {
            throw new IllegalArgumentException(
                    "Unknown badge "
                            + badgeId
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

        List<TagKey<EntityType<?>>> tags =
                new ArrayList<>();

        for (JsonElement target :
                targets) {

            String value =
                    target.getAsString();

            if (value.startsWith("#")) {

                ResourceLocation tagId =
                        new ResourceLocation(
                                value.substring(1)
                        );

                tags.add(
                        TagKey.create(
                                Registries.ENTITY_TYPE,
                                tagId
                        )
                );

                continue;
            }

            ResourceLocation entityId =
                    new ResourceLocation(value);

            entityIds.add(entityId);
        }

        if (entityIds.isEmpty()
                && tags.isEmpty()) {
            throw new IllegalArgumentException(
                    "Rule "
                            + fileId
                            + " contains no targets"
            );
        }

        return new ChefBadgeDropRule(
                badge,
                List.copyOf(entityIds),
                List.copyOf(tags),
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