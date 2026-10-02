package com.carrot123.eternal_career.fletching;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.registry.ModItems;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public final class BowModifications {

    public static final BowModification POWER =
            new BowModification(
                    new ResourceLocation(
                            EternalCareer.MOD_ID,
                            "power"
                    ),
                    "fletching.eternal_career.power",
                    "fletching.eternal_career.power.description",
                    "fletching.eternal_career.power.effect",
                    5,
                    0.10D,
                    false,
                    ModItems.POWER_MODIFICATION::get,
                    List.of(
                            new BowModification.Material(
                                    Items.BLAZE_POWDER,
                                    2
                            ),
                            new BowModification.Material(
                                    Items.IRON_INGOT,
                                    4
                            ),
                            new BowModification.Material(
                                    externalItem(
                                            "morebows",
                                            "bow_upgrade_smithing_template"
                                    ),
                                    1
                            )
                    )
            );

    public static final BowModification VELOCITY =
            new BowModification(
                    new ResourceLocation(
                            EternalCareer.MOD_ID,
                            "velocity"
                    ),
                    "fletching.eternal_career.velocity",
                    "fletching.eternal_career.velocity.description",
                    "fletching.eternal_career.velocity.effect",
                    3,
                    0.05D,
                    false,
                    ModItems.VELOCITY_MODIFICATION::get,
                    List.of(
                            new BowModification.Material(
                                    Items.FEATHER,
                                    2
                            ),
                            new BowModification.Material(
                                    Items.IRON_INGOT,
                                    4
                            ),
                            new BowModification.Material(
                                    externalItem(
                                            "morebows",
                                            "bow_upgrade_smithing_template"
                                    ),
                                    1
                            )
                    )
            );

    public static final BowModification BLAST =
            new BowModification(
                    new ResourceLocation(
                            EternalCareer.MOD_ID,
                            "blast"
                    ),
                    "fletching.eternal_career.blast",
                    "fletching.eternal_career.blast.description",
                    "fletching.eternal_career.blast.effect",
                    1,
                    1.30D,
                    true,
                    ModItems.BLAST_MODIFICATION::get,
                    List.of(
                            new BowModification.Material(
                                    Items.GUNPOWDER,
                                    2
                            ),
                            new BowModification.Material(
                                    Items.BLAZE_POWDER,
                                    4
                            ),
                            new BowModification.Material(
                                    externalItem(
                                            "morebows",
                                            "bow_upgrade_smithing_template"
                                    ),
                                    1
                            )
                    )
            );

    public static final BowModification RANGER =
            new BowModification(
                    new ResourceLocation(
                            EternalCareer.MOD_ID,
                            "ranger"
                    ),
                    "fletching.eternal_career.ranger",
                    "fletching.eternal_career.ranger.description",
                    "fletching.eternal_career.ranger.effect",
                    3,
                    0.10D,
                    false,
                    ModItems.RANGER_MODIFICATION::get,
                    List.of(
                            new BowModification.Material(
                                    externalItem(
                                            "goety",
                                            "magic_emerald"
                                    ),
                                    1
                            ),
                            new BowModification.Material(
                                    Items.NETHER_STAR,
                                    1
                            ),
                            new BowModification.Material(
                                    externalItem(
                                            "cataclysm",
                                            "cursium_upgrade_smithing_template"
                                    ),
                                    1
                            )
                    )
            );

    public static final BowModification BLOODTHIRST =
            new BowModification(
                    new ResourceLocation(
                            EternalCareer.MOD_ID,
                            "bloodthirst"
                    ),
                    "fletching.eternal_career.bloodthirst",
                    "fletching.eternal_career.bloodthirst.description",
                    "fletching.eternal_career.bloodthirst.effect",
                    1,
                    0.10D,
                    true,
                    ModItems.BLOODTHIRST_MODIFICATION::get,
                    List.of(
                            new BowModification.Material(
                                    Items.SPIDER_EYE,
                                    1
                            ),
                            new BowModification.Material(
                                    Items.GHAST_TEAR,
                                    2
                            ),
                            new BowModification.Material(
                                    externalItem(
                                            "morebows",
                                            "bow_upgrade_smithing_template"
                                    ),
                                    1
                            )
                    )
            );

    public static final BowModification END =
            new BowModification(
                    new ResourceLocation(
                            EternalCareer.MOD_ID,
                            "end"
                    ),
                    "fletching.eternal_career.end",
                    "fletching.eternal_career.end.description",
                    "fletching.eternal_career.end.effect",
                    2,
                    1.00D,
                    false,
                    ModItems.END_MODIFICATION::get,
                    List.of(
                            new BowModification.Material(
                                    externalItem(
                                            "until_eternity",
                                            "dragonbreath_ingot"
                                    ),
                                    2
                            ),
                            new BowModification.Material(
                                    Items.DRAGON_EGG,
                                    1
                            ),
                            new BowModification.Material(
                                    externalItem(
                                            "cataclysm",
                                            "cursium_upgrade_smithing_template"
                                    ),
                                    1
                            )
                    )
            );

    public static final List<BowModification> ALL =
            List.of(
                    POWER,
                    VELOCITY,
                    BLAST,
                    RANGER,
                    BLOODTHIRST,
                    END
            );

    private BowModifications() {
    }

    public static BowModification byId(
            ResourceLocation id
    ) {
        for (BowModification modification : ALL) {
            if (modification.id().equals(id)) {
                return modification;
            }
        }

        return null;
    }

    private static Supplier<Item> externalItem(
            String namespace,
            String path
    ) {
        ResourceLocation id =
                new ResourceLocation(
                        namespace,
                        path
                );

        return () -> {
            Item item =
                    ForgeRegistries.ITEMS
                            .getValue(id);

            return item != null
                    ? item
                    : Items.AIR;
        };
    }
}