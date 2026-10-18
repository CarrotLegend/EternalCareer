package com.carrot123.eternal_career.item;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.Item;

public final class FistBladeItems {
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EternalCareer.MOD_ID);

    public static final RegistryObject<FistBladeItem> IRON_FIST_BLADE =
            ITEMS.register("iron_fist_blade",
                    () -> new FistBladeItem(Tiers.IRON, 20, Tiers.IRON.getUses(), false));

    public static final RegistryObject<FistBladeItem> DIAMOND_FIST_BLADE =
            ITEMS.register("diamond_fist_blade",
                    () -> new FistBladeItem(Tiers.DIAMOND, 58, Tiers.DIAMOND.getUses(), false));

    public static final RegistryObject<FistBladeItem> NETHERITE_FIST_BLADE =
            ITEMS.register("netherite_fist_blade",
                    () -> new FistBladeItem(Tiers.NETHERITE, 76, Tiers.NETHERITE.getUses(), true));

    public static final RegistryObject<FistBladeItem> AETHER_FIST_BLADE =
            ITEMS.register("aether_fist_blade",
                    () -> new FistBladeItem(Tiers.NETHERITE, 124, 2089, true));

    public static final RegistryObject<FistBladeItem> FINAL_ALLOY_FIST_BLADE =
            ITEMS.register("final_alloy_fist_blade",
                    () -> new FistBladeItem(Tiers.NETHERITE, 291, 4040, true));

    private FistBladeItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
