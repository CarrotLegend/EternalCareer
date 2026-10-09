package com.carrot123.eternal_career.soulcurse;

import com.google.gson.Gson;
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

public final class SoulDropManager extends SimpleJsonResourceReloadListener {
    public static final SoulDropManager INSTANCE = new SoulDropManager();
    private static final Logger LOGGER = LogUtils.getLogger();
    private volatile List<DropRule> rules = List.of();
    private SoulDropManager() { super(new Gson(), "soul_drops"); }
    @Override protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resources, ProfilerFiller profiler) {
        List<DropRule> loaded = new ArrayList<>();
        jsons.forEach((id, json) -> { try { loaded.add(parse(json)); } catch (RuntimeException e) { LOGGER.error("Invalid soul drop JSON: {}", id, e); } });
        rules = List.copyOf(loaded);
    }
    public List<DropRule> getRules() { return rules; }
    private static DropRule parse(JsonElement json) {
        JsonObject data = GsonHelper.convertToJsonObject(json, "soul drop");
        ResourceLocation item = ResourceLocation.tryParse(GsonHelper.getAsString(data, "item"));
        if (item == null) throw new JsonParseException("Invalid soul drop item id");
        Set<ResourceLocation> bosses = new HashSet<>();
        for (JsonElement e : GsonHelper.getAsJsonArray(data, "bosses")) {
            ResourceLocation id = ResourceLocation.tryParse(e.getAsString());
            if (id == null) throw new JsonParseException("Invalid entity id");
            bosses.add(id);
        }
        if (bosses.isEmpty()) throw new JsonParseException("bosses must not be empty");
        double chance = Mth.clamp(GsonHelper.getAsDouble(data, "chance", 1), 0, 1);
        int min = Math.max(1, GsonHelper.getAsInt(data, "min_count", 1));
        int max = Math.max(min, GsonHelper.getAsInt(data, "max_count", min));
        return new DropRule(item, Set.copyOf(bosses), chance, min, max);
    }
    public record DropRule(ResourceLocation item, Set<ResourceLocation> bosses, double chance, int min, int max) {}
}
