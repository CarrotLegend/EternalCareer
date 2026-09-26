package com.carrot123.eternal_career.registry;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.soulblessing.SoulBlessingMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    private static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, EternalCareer.MOD_ID);

    public static final RegistryObject<MenuType<SoulBlessingMenu>> SOUL_BLESSING =
            MENUS.register("soul_blessing", () -> IForgeMenuType.create(
                    (containerId, inventory, ignored) ->
                            new SoulBlessingMenu(containerId, inventory)));

    private ModMenus() {}

    public static void register(IEventBus bus) { MENUS.register(bus); }
}
