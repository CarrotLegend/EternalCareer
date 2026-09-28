package com.carrot123.eternal_career.fletching;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class BowModificationItem extends Item {
    private final ResourceLocation modificationId;

    public BowModificationItem(Properties properties, ResourceLocation modificationId) {
        super(properties);
        this.modificationId = modificationId;
    }

    public BowModification modification() {
        return BowModifications.byId(modificationId);
    }
}
