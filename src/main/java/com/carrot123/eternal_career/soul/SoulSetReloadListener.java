package com.carrot123.eternal_career.soul;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

public final class SoulSetReloadListener extends SimpleJsonResourceReloadListener {
    public SoulSetReloadListener() { super(new Gson(), "soul_sets"); }
    @Override protected void apply(Map<ResourceLocation, JsonElement> resources,
                                   ResourceManager manager, ProfilerFiller profiler) {
        var definitions = new ArrayList<SoulSetDefinition>();
        resources.forEach((id, json) -> {
            try { definitions.add(SoulSetDefinition.parse(id, json)); }
            catch (RuntimeException e) { LogUtils.getLogger().error("Skipping soul set {}: {}", id, e.getMessage()); }
        });
        SoulSetManager.replace(definitions);
    }
}
