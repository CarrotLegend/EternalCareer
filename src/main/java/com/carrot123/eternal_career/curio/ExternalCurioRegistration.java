package com.carrot123.eternal_career.curio;

import com.carrot123.eternal_career.EternalCareer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class ExternalCurioRegistration {

    private static final ResourceLocation FOOD_BOOK_ID =
            new ResourceLocation("solcarrot", "food_book");

    private ExternalCurioRegistration() {
    }

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Item foodBook = ForgeRegistries.ITEMS.getValue(FOOD_BOOK_ID);

            if (foodBook != null && foodBook != Items.AIR) {
                CuriosApi.registerCurio(
                        foodBook,
                        new FoodBookCurio()
                );
            }
        });
    }
}