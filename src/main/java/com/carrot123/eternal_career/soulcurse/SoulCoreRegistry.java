package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SoulCoreRegistry {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EternalCareer.MOD_ID);
    public static final RegistryObject<Item> SOUL_CORE = registerItem("soul_core", false);
    public static final RegistryObject<Item> BLOOD_HUNTER_CORE = registerItem("blood_hunter_core", true);
    public static final RegistryObject<Item> HYENA_CORE = registerItem("hyena_core", false);
    private static RegistryObject<Item> registerItem(String id, boolean bloodHunter) { return ITEMS.register(id, () -> new SoulCoreItem(new Item.Properties().rarity(Rarity.EPIC).fireResistant(), bloodHunter)); }
    public static void register(IEventBus bus) { ITEMS.register(bus); }
}
