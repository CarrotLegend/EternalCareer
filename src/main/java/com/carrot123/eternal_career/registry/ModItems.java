package com.carrot123.eternal_career.registry;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.armor.ChefArmorItem;
import com.carrot123.eternal_career.armor.ChefArmorMaterial;
import com.carrot123.eternal_career.item.GodsRecognitionItem;
import com.carrot123.eternal_career.item.HeadChefSheathItem;
import com.carrot123.eternal_career.item.SinRockItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Eternal Career item registrations. */
public final class ModItems {
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EternalCareer.MOD_ID);

    public static final RegistryObject<Item> CHEF_HAT = registerChefArmor(
            "chef_hat", ArmorItem.Type.HELMET);
    public static final RegistryObject<Item> CHEF_JACKET = registerChefArmor(
            "chef_jacket", ArmorItem.Type.CHESTPLATE);
    public static final RegistryObject<Item> CHEF_LEGGINGS = registerChefArmor(
            "chef_leggings", ArmorItem.Type.LEGGINGS);
    public static final RegistryObject<Item> CHEF_BOOTS = registerChefArmor(
            "chef_boots", ArmorItem.Type.BOOTS);
    public static final RegistryObject<Item> GODS_RECOGNITION = ITEMS.register(
            "gods_recognition",
            () -> new GodsRecognitionItem(new Item.Properties().fireResistant()));
    public static final RegistryObject<Item> SIN_ROCK = ITEMS.register(
            "sin_rock",
            () -> new SinRockItem(new Item.Properties()));
    public static final RegistryObject<Item> HEAD_CHEF_SHEATH = ITEMS.register(
            "head_chef_sheath",
            () -> new HeadChefSheathItem(new Item.Properties().stacksTo(1)));

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
