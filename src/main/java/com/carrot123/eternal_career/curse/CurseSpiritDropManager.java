package com.carrot123.eternal_career.curse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public final class CurseSpiritDropManager
        extends SimpleJsonResourceReloadListener {

    private static final Gson GSON =
            new Gson();

    private static final Logger LOGGER =
            LogUtils.getLogger();

    public static final CurseSpiritDropManager INSTANCE =
            new CurseSpiritDropManager();

    private volatile List<DropRule> rules =
            List.of();

    private CurseSpiritDropManager() {
        super(
                GSON,
                "curse_spirit_drops"
        );
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> objects,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        List<DropRule> loaded =
                new ArrayList<>();

        for (Map.Entry<ResourceLocation, JsonElement> entry
                : objects.entrySet()) {
            try {
                loaded.add(
                        parseRule(
                                entry.getValue()
                        )
                );
            } catch (RuntimeException exception) {
                LOGGER.error(
                        "Failed to load curse spirit drop rule {}",
                        entry.getKey(),
                        exception
                );
            }
        }

        rules =
                List.copyOf(loaded);
    }

    public List<DropRule> getRules() {
        return rules;
    }

    private static DropRule parseRule(
            JsonElement element
    ) {
        JsonObject object =
                GsonHelper.convertToJsonObject(
                        element,
                        "curse spirit drop rule"
                );

        String itemValue =
                GsonHelper.getAsString(
                        object,
                        "item"
                );

        ResourceLocation itemId =
                ResourceLocation.tryParse(
                        itemValue
                );

        if (itemId == null) {
            throw new JsonParseException(
                    "Invalid item id: "
                            + itemValue
            );
        }

        JsonArray bossesArray =
                GsonHelper.getAsJsonArray(
                        object,
                        "bosses"
                );

        Set<ResourceLocation> bosses =
                new HashSet<>();

        for (JsonElement bossElement
                : bossesArray) {
            String value =
                    bossElement.getAsString();

            ResourceLocation id =
                    ResourceLocation.tryParse(
                            value
                    );

            if (id == null) {
                throw new JsonParseException(
                        "Invalid entity id: "
                                + value
                );
            }

            bosses.add(id);
        }

        if (bosses.isEmpty()) {
            throw new JsonParseException(
                    "Boss list cannot be empty"
            );
        }

        double chance =
                Mth.clamp(
                        GsonHelper.getAsDouble(
                                object,
                                "chance",
                                1.0D
                        ),
                        0.0D,
                        1.0D
                );

        int minCount =
                Math.max(
                        1,
                        GsonHelper.getAsInt(
                                object,
                                "min_count",
                                1
                        )
                );

        int maxCount =
                Math.max(
                        minCount,
                        GsonHelper.getAsInt(
                                object,
                                "max_count",
                                minCount
                        )
                );

        return new DropRule(
                itemId,
                Set.copyOf(bosses),
                chance,
                minCount,
                maxCount
        );
    }

    public record DropRule(
            ResourceLocation itemId,
            Set<ResourceLocation> bosses,
            double chance,
            int minCount,
            int maxCount
    ) {

        public boolean matches(
                ResourceLocation entityId
        ) {
            return bosses.contains(
                    entityId
            );
        }
    }
}