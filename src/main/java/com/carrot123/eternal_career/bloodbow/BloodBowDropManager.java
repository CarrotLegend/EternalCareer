package com.carrot123.eternal_career.bloodbow;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

public final class BloodBowDropManager extends SimpleJsonResourceReloadListener {
    public static final BloodBowDropManager INSTANCE = new BloodBowDropManager();
    private static final Logger LOGGER = LogUtils.getLogger();
    private volatile List<Rule> rules = List.of();
    private BloodBowDropManager() { super(new Gson(), "blood_bow_drops"); }
    @Override protected void apply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        List<Rule> parsed = new ArrayList<>();
        resources.forEach((key, value) -> {
            try {
                JsonObject json = GsonHelper.convertToJsonObject(value, "blood bow drop");
                ResourceLocation item = ResourceLocation.tryParse(GsonHelper.getAsString(json, "item"));
                if (item == null) throw new IllegalArgumentException("Invalid item ID");
                Set<ResourceLocation> mobs = new HashSet<>();
                for (JsonElement entry : GsonHelper.getAsJsonArray(json, "bosses")) {
                    ResourceLocation id = ResourceLocation.tryParse(entry.getAsString());
                    if (id == null) throw new IllegalArgumentException("Invalid boss ID");
                    mobs.add(id);
                }
                if (mobs.isEmpty()) throw new IllegalArgumentException("Empty bosses");
                double chance = Mth.clamp(GsonHelper.getAsDouble(json, "chance", 1), 0, 1);
                int min = Math.max(1, GsonHelper.getAsInt(json, "min_count", 1));
                int max = Math.max(min, GsonHelper.getAsInt(json, "max_count", min));
                parsed.add(new Rule(item, Set.copyOf(mobs), chance, min, max));
            } catch (Exception ex) { LOGGER.error("Invalid Blood Bow loot rule {}", key, ex); }
        });
        rules = List.copyOf(parsed);
    }
    public List<Rule> rules() { return rules; }
    public record Rule(ResourceLocation item, Set<ResourceLocation> bosses, double chance, int min, int max) {}
}
