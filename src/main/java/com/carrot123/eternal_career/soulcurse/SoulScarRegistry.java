package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SoulScarRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EternalCareer.MOD_ID);
    public static final RegistryObject<Item> SOUL_GAUNTLET = ITEMS.register("soul_gauntlet", () -> new SoulGauntletItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> TRANSFORMING_SCAR = ITEMS.register("transforming_scar", () -> new TransformingScarItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> COWARD = ITEMS.register("coward", () -> new SoulScarItem(SoulScarItem.Kind.COWARD, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> FRAGILE = ITEMS.register("fragile", () -> new SoulScarItem(SoulScarItem.Kind.FRAGILE, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> PESSIMIST = ITEMS.register("pessimist", () -> new SoulScarItem(SoulScarItem.Kind.PESSIMIST, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    private SoulScarRegistry() {}
    public static void register(IEventBus bus) { ITEMS.register(bus); }
}
