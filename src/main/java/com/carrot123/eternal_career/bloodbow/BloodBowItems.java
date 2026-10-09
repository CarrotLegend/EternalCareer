package com.carrot123.eternal_career.bloodbow;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.fletching.BowModificationItem;
import com.carrot123.eternal_career.item.MaterialArrowItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class BloodBowItems {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EternalCareer.MOD_ID);
    public static final RegistryObject<Item> BLOOD_HUNT_MODIFICATION = upgrade("blood_hunt_modification", "blood_hunt");
    public static final RegistryObject<Item> FANG_MODIFICATION = upgrade("fang_modification", "fang");
    public static final RegistryObject<Item> FANG_II_MODIFICATION = upgrade("fang_ii_modification", "fang_ii");
    public static final RegistryObject<Item> FANG_III_MODIFICATION = upgrade("fang_iii_modification", "fang_iii");
    public static final RegistryObject<Item> FANG_IV_MODIFICATION = upgrade("fang_iv_modification", "fang_iv");
    public static final RegistryObject<MaterialArrowItem> BLOOD_HUNTER_ARROW = ITEMS.register("blood_hunter_arrow", () -> new MaterialArrowItem(MaterialArrowItem.Material.BLOOD_HUNTER, new Item.Properties()));
    private BloodBowItems() {}
    private static RegistryObject<Item> upgrade(String itemName, String modification) {
        return ITEMS.register(itemName, () -> new BowModificationItem(new Item.Properties().rarity(Rarity.RARE), new ResourceLocation(EternalCareer.MOD_ID, modification)));
    }
    public static void register(IEventBus bus) { ITEMS.register(bus); }
}
