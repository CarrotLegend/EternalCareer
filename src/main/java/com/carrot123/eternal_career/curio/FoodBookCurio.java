package com.carrot123.eternal_career.curio;

import com.carrot123.eternal_career.EternalCareer;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public final class FoodBookCurio implements ICurioItem {

    public static final ResourceLocation FOOD_BOOK_ID =
            new ResourceLocation("solcarrot", "food_book");

    public static final FoodBookCurio INSTANCE = new FoodBookCurio();

    public FoodBookCurio() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Item foodBook = ForgeRegistries.ITEMS.getValue(FOOD_BOOK_ID);

            if (foodBook == null || foodBook == Items.AIR) {
                return;
            }

            CuriosApi.registerCurio(foodBook, INSTANCE);
        });
    }
    @Override
    public List<Component> getAttributesTooltip(
            List<Component> tooltips,
            ItemStack stack
    ) {
        tooltips.clear();
        return tooltips;
    }
}