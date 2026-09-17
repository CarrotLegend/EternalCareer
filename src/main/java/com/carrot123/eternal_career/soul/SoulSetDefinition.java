package com.carrot123.eternal_career.soul;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public record SoulSetDefinition(ResourceLocation id, TagKey<Item> armorTag, int maxSoul) {
    public static SoulSetDefinition parse(ResourceLocation id, JsonElement json) {
        if (!json.isJsonObject()) throw new JsonParseException("Expected object");
        JsonObject object = json.getAsJsonObject();
        JsonElement tag = object.get("armor_tag");
        if (tag == null || !tag.isJsonPrimitive() || !tag.getAsJsonPrimitive().isString())
            throw new JsonParseException("armor_tag must be a resource location string");
        ResourceLocation location = ResourceLocation.tryParse(tag.getAsString());
        if (location == null || !location.getNamespace().equals("until_eternity")
                || !location.getPath().startsWith("soul_sets/")
                || location.getPath().length() <= "soul_sets/".length())
            throw new JsonParseException("armor_tag must identify until_eternity:soul_sets/<name>");
        JsonElement capacity = object.get("max_soul");
        if (capacity == null || !capacity.isJsonPrimitive() || !capacity.getAsJsonPrimitive().isNumber())
            throw new JsonParseException("max_soul must be a positive integer");
        int max;
        try { max = capacity.getAsBigDecimal().intValueExact(); }
        catch (ArithmeticException | NumberFormatException e) {
            throw new JsonParseException("max_soul must be a positive 32-bit integer", e);
        }
        if (max <= 0) throw new JsonParseException("max_soul must be positive");
        return new SoulSetDefinition(id, TagKey.create(Registries.ITEM, location), max);
    }
}
