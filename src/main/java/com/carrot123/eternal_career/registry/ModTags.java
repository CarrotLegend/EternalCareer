package com.carrot123.eternal_career.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModTags {
    private ModTags() {
    }

    public static final class Items {
        // Shared modpack tag: intentionally uses the until_eternity namespace.
        public static final TagKey<Item> SCYTHES =
                TagKey.create(Registries.ITEM, new ResourceLocation("until_eternity", "scythes"));

        public static final TagKey<Item> SOUL_ARMOR =
                TagKey.create(Registries.ITEM, new ResourceLocation("until_eternity", "soul_armor"));

        private Items() {
        }
    }
}
