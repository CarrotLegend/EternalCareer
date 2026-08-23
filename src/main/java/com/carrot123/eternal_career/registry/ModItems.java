package com.carrot123.eternal_career.registry;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.armor.ChefArmorItem;
import com.carrot123.eternal_career.armor.ChefArmorMaterial;
import com.carrot123.eternal_career.item.GodsRecognitionItem;
import com.carrot123.eternal_career.item.CookingMagicHandItem;
import com.carrot123.eternal_career.item.HeadChefSheathItem;
import com.carrot123.eternal_career.item.SinRockItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EternalCareer.MOD_ID);

    public static final RegistryObject<Item> CHEF_HAT = registerChefArmor("chef_hat", ArmorItem.Type.HELMET);
    public static final RegistryObject<Item> CHEF_JACKET = registerChefArmor("chef_jacket", ArmorItem.Type.CHESTPLATE);
    public static final RegistryObject<Item> CHEF_LEGGINGS = registerChefArmor("chef_leggings", ArmorItem.Type.LEGGINGS);
    public static final RegistryObject<Item> CHEF_BOOTS = registerChefArmor("chef_boots", ArmorItem.Type.BOOTS);
    public static final RegistryObject<Item> GODS_RECOGNITION = ITEMS.register("gods_recognition", () -> new GodsRecognitionItem(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> SIN_ROCK = ITEMS.register("sin_rock", () -> new SinRockItem(new Item.Properties()));
    public static final RegistryObject<Item> HEAD_CHEF_SHEATH = ITEMS.register("head_chef_sheath", () -> new HeadChefSheathItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> COOKING_MAGIC_HAND = ITEMS.register("cooking_magic_hand", () -> new CookingMagicHandItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> CHEF_APPRENTICE_BADGE = ITEMS.register("chef_apprentice_badge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INTERMEDIATE_CHEF_BADGE = ITEMS.register("intermediate_chef_badge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ADVANCED_CHEF_BADGE = ITEMS.register("advanced_chef_badge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SENIOR_TECHNICIAN_BADGE = ITEMS.register("senior_technician_badge", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MASTER_CHEF_BADGE = ITEMS.register("master_chef_badge", () -> new Item(new Item.Properties()));

    private ModItems() {
    }

    private static RegistryObject<Item> registerChefArmor(String name, ArmorItem.Type type) {
        return ITEMS.register(name, () -> new ChefArmorItem(
            ChefArmorMaterial.INSTANCE, type, new Item.Properties().stacksTo(1)));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
