package com.carrot123.eternal_career.fletching;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModItems;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public final class BowModifications {
    public static final BowModification POWER = new BowModification(
            new ResourceLocation(EternalCareer.MOD_ID, "power"),
            "fletching.eternal_career.power", "fletching.eternal_career.power.description",
            "fletching.eternal_career.power.effect", 5, 0.10D,
            ModItems.POWER_MODIFICATION::get,
            List.of(new BowModification.Material(Items.FLINT, 2),
                    new BowModification.Material(Items.IRON_INGOT, 1),
                    new BowModification.Material(Items.PAPER, 1)));
    public static final BowModification VELOCITY = new BowModification(
            new ResourceLocation(EternalCareer.MOD_ID, "velocity"),
            "fletching.eternal_career.velocity", "fletching.eternal_career.velocity.description",
            "fletching.eternal_career.velocity.effect", 3, 0.05D,
            ModItems.VELOCITY_MODIFICATION::get,
            List.of(new BowModification.Material(Items.FEATHER, 2),
                    new BowModification.Material(Items.SUGAR, 1),
                    new BowModification.Material(Items.PAPER, 1)));
    public static final List<BowModification> ALL = List.of(POWER, VELOCITY);

    private BowModifications() {
    }

    public static BowModification byId(ResourceLocation id) {
        for (BowModification modification : ALL) {
            if (modification.id().equals(id)) {
                return modification;
            }
        }
        return null;
    }
}
