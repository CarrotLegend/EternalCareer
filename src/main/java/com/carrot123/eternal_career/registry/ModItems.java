package com.carrot123.eternal_career.registry;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.armor.ChefArmorItem;
import com.carrot123.eternal_career.armor.ChefArmorMaterial;
import com.carrot123.eternal_career.armor.DeathArmorItem;
import com.carrot123.eternal_career.armor.DeathArmorMaterial;
import com.carrot123.eternal_career.item.CallOfDeathItem;
import com.carrot123.eternal_career.item.ChaosBladeItem;
import com.carrot123.eternal_career.item.ChaosFoodItem;
import com.carrot123.eternal_career.item.CookingMagicHandItem;
import com.carrot123.eternal_career.item.GodsRecognitionItem;
import com.carrot123.eternal_career.item.HeadChefSheathItem;
import com.carrot123.eternal_career.item.LichResearchNotesItem;
import com.carrot123.eternal_career.item.PurifiedPanaceaItem;
import com.carrot123.eternal_career.lich.LichStage;
import com.carrot123.eternal_career.item.SinRockItem;
import com.carrot123.eternal_career.item.SoulTankItem;
import com.carrot123.eternal_career.item.SublimationEssenceItem;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EternalCareer.MOD_ID);

    public static final RegistryObject<Item> CHEF_HAT =
            registerChefArmor("chef_hat", ArmorItem.Type.HELMET);

    public static final RegistryObject<Item> CHEF_JACKET =
            registerChefArmor("chef_jacket", ArmorItem.Type.CHESTPLATE);

    public static final RegistryObject<Item> CHEF_LEGGINGS =
            registerChefArmor("chef_leggings", ArmorItem.Type.LEGGINGS);

    public static final RegistryObject<Item> CHEF_BOOTS =
            registerChefArmor("chef_boots", ArmorItem.Type.BOOTS);

    public static final RegistryObject<Item> DEATH_HELMET =
            registerDeathArmor("death_helmet", ArmorItem.Type.HELMET);

    public static final RegistryObject<Item> DEATH_CHESTPLATE =
            registerDeathArmor("death_chestplate", ArmorItem.Type.CHESTPLATE);

    public static final RegistryObject<Item> DEATH_LEGGINGS =
            registerDeathArmor("death_leggings", ArmorItem.Type.LEGGINGS);

    public static final RegistryObject<Item> DEATH_BOOTS =
            registerDeathArmor("death_boots", ArmorItem.Type.BOOTS);

    public static final RegistryObject<Item> GODS_RECOGNITION =
            ITEMS.register(
                    "gods_recognition",
                    () -> new GodsRecognitionItem(
                            new Item.Properties().fireResistant()
                    )
            );

    public static final RegistryObject<Item> SIN_ROCK =
            ITEMS.register(
                    "sin_rock",
                    () -> new SinRockItem(
                            new Item.Properties()
                    )
            );

    public static final RegistryObject<Item> HEAD_CHEF_SHEATH =
            ITEMS.register(
                    "head_chef_sheath",
                    () -> new HeadChefSheathItem(
                            new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                    )
            );

    public static final RegistryObject<Item> CALL_OF_DEATH =
            ITEMS.register(
                    "call_of_death",
                    () -> new CallOfDeathItem(
                            new Item.Properties().stacksTo(1)
                    )
            );

    public static final RegistryObject<Item> LICH_RESEARCH_NOTES =
            ITEMS.register("lich_research_notes",
                    () -> new LichResearchNotesItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> NOVICE_PURIFIED_PANACEA =
            ITEMS.register("novice_purified_panacea",
                    () -> new PurifiedPanaceaItem(new Item.Properties().stacksTo(1),
                            LichStage.PLAYER, LichStage.NOVICE_LICH));

    public static final RegistryObject<Item> INTERMEDIATE_PURIFIED_PANACEA =
            ITEMS.register("intermediate_purified_panacea",
                    () -> new PurifiedPanaceaItem(new Item.Properties().stacksTo(1),
                            LichStage.NOVICE_LICH, LichStage.INTERMEDIATE_LICH));

    public static final RegistryObject<Item> ADVANCED_PURIFIED_PANACEA =
            ITEMS.register("advanced_purified_panacea",
                    () -> new PurifiedPanaceaItem(new Item.Properties().stacksTo(1),
                            LichStage.INTERMEDIATE_LICH, LichStage.ADVANCED_LICH));

    public static final RegistryObject<Item> LICH_KING_PURIFIED_PANACEA =
            ITEMS.register("lich_king_purified_panacea",
                    () -> new PurifiedPanaceaItem(new Item.Properties().stacksTo(1),
                            LichStage.ADVANCED_LICH, LichStage.LICH_KING));

    public static final RegistryObject<Item> COOKING_MAGIC_HAND =
            ITEMS.register(
                    "cooking_magic_hand",
                    () -> new CookingMagicHandItem(
                            new Item.Properties().stacksTo(1)
                    )
            );

    public static final RegistryObject<Item> CHEF_APPRENTICE_BADGE =
            ITEMS.register(
                    "chef_apprentice_badge",
                    () -> new Item(new Item.Properties())
            );

    public static final RegistryObject<Item> INTERMEDIATE_CHEF_BADGE =
            ITEMS.register(
                    "intermediate_chef_badge",
                    () -> new Item(new Item.Properties())
            );

    public static final RegistryObject<Item> ADVANCED_CHEF_BADGE =
            ITEMS.register(
                    "advanced_chef_badge",
                    () -> new Item(new Item.Properties())
            );

    public static final RegistryObject<Item> SENIOR_TECHNICIAN_BADGE =
            ITEMS.register(
                    "senior_technician_badge",
                    () -> new Item(new Item.Properties())
            );

    public static final RegistryObject<Item> MASTER_CHEF_BADGE =
            ITEMS.register(
                    "master_chef_badge",
                    () -> new Item(new Item.Properties())
            );

    public static final RegistryObject<Item> BLAZING_ESSENCE =
            ITEMS.register(
                    "blazing_essence",
                    () -> new Item(new Item.Properties())
            );

    public static final RegistryObject<Item> SUBLIMATION_ESSENCE =
            ITEMS.register(
                    "sublimation_essence",
                    () -> new SublimationEssenceItem(
                            new Item.Properties()
                    )
            );
    public static final RegistryObject<Item> SOUL_TANK =
        ITEMS.register(
                "soul_tank",
                () -> new SoulTankItem(
                        new Item.Properties().rarity(Rarity.RARE).stacksTo(1)
                )
        );

    public static final RegistryObject<Item> CHAOS_STEAK = ITEMS.register(
            "chaos_steak",
            () -> new ChaosFoodItem(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(8).saturationMod(0.8F)
                            .meat().alwaysEat().build()), 1, "chaos_steak")
    );

    public static final RegistryObject<Item> CHAOS_MEAT = ITEMS.register(
            "chaos_meat",
            () -> new ChaosFoodItem(new Item.Properties().food(
                    new FoodProperties.Builder().nutrition(4).saturationMod(0.5F)
                            .meat().alwaysEat().build()), 0, "chaos_meat")
    );

    public static final RegistryObject<Item> CHAOS_BLADE = ITEMS.register(
            "chaos_blade",
            () -> new ChaosBladeItem(new Item.Properties().stacksTo(1))
    );

    private ModItems() {
    }

    private static RegistryObject<Item> registerChefArmor(
            String name,
            ArmorItem.Type type
    ) {
        return ITEMS.register(
                name,
                () -> new ChefArmorItem(
                        ChefArmorMaterial.INSTANCE,
                        type,
                        new Item.Properties().stacksTo(1)
                )
        );
    }

    private static RegistryObject<Item> registerDeathArmor(
            String name,
            ArmorItem.Type type
    ) {
        return ITEMS.register(
                name,
                () -> new DeathArmorItem(
                        DeathArmorMaterial.INSTANCE,
                        type,
                        new Item.Properties()
                                .stacksTo(1)
                                .fireResistant()
                )
        );
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
