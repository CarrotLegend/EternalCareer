package com.carrot123.eternal_career.curio;

import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

public final class CurioEquipmentHelper {

    public static final ResourceLocation FOOD_BOOK_ID =
            new ResourceLocation("solcarrot", "food_book");

    private CurioEquipmentHelper() {
    }

    public static boolean hasGodsRecognition(Player player) {
        return hasEquippedCurio(player, ModItems.GODS_RECOGNITION.get());
    }

    public static boolean hasFoodBook(Player player) {
        Item foodBook = ForgeRegistries.ITEMS.getValue(FOOD_BOOK_ID);

        return foodBook != null
                && foodBook != Items.AIR
                && hasEquippedCurio(player, foodBook);
    }

    /** Returns whether the functional hands slot contains the Cooking Magic Hand. */
    public static boolean hasCookingMagicHand(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .map(handler -> handler.findCurios(ModItems.COOKING_MAGIC_HAND.get())
                        .stream()
                        .anyMatch(result ->
                                "hands".equals(result.slotContext().identifier())
                                        && !result.slotContext().cosmetic()))
                .orElse(false);
    }

    public static boolean hasEquippedCurio(Player player, Item item) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .map(handler ->
                        handler.findCurios(item)
                                .stream()
                                .anyMatch(result ->
                                        !result.slotContext().cosmetic()
                                )
                )
                .orElse(false);
    }
}
