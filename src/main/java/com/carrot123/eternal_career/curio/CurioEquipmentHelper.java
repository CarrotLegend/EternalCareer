package com.carrot123.eternal_career.curio;

import com.carrot123.eternal_career.item.HeadChefSheathItem;
import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;
import com.carrot123.eternal_career.registry.ModItems;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraftforge.registries.ForgeRegistries;

import top.theillusivec4.curios.api.CuriosApi;

public final class CurioEquipmentHelper {

    public static final ResourceLocation FOOD_BOOK_ID =
            new ResourceLocation(
                    "solcarrot",
                    "food_book"
            );

    private CurioEquipmentHelper() {
    }

    public static boolean hasGodsRecognition(
            Player player
    ) {
        return hasEquippedCurio(
                player,
                ModItems.GODS_RECOGNITION.get(),
                FoodBookCurio.CHARM_SLOT
        );
    }

    public static boolean hasFoodBook(
            Player player
    ) {
        Item foodBook =
                ForgeRegistries.ITEMS.getValue(
                        FOOD_BOOK_ID
                );

        return foodBook != null
                && foodBook != Items.AIR
                && hasEquippedCurio(
                        player,
                        foodBook,
                        FoodBookCurio.CHARM_SLOT
                );
    }

    public static boolean hasCookingMagicHand(
            Player player
    ) {
        if (!RedemptionAccessController.hasRedemptionAccess(player)) {
            return false;
        }
        return CuriosApi
                .getCuriosInventory(player)
                .resolve()
                .map(handler ->
                    handler.findCurios(
                        ModItems.COOKING_MAGIC_HAND.get()
                                )
                        .stream()
                        .anyMatch(result ->
                                "hands".equals(
                                        result.slotContext()
                                                .identifier()
                                )
                                        && !result
                                        .slotContext()
                                        .cosmetic()
                        )
                )
                .orElse(false);
    }

    public static boolean hasHeadChefSheath(
            Player player
    ) {
        return CuriosApi
                .getCuriosInventory(player)
                .resolve()
                .map(handler ->
                        handler.findCurios(
                                        ModItems.HEAD_CHEF_SHEATH.get()
                                )
                                .stream()
                                .anyMatch(result ->
                                        HeadChefSheathItem.BELT_SLOT
                                                .equals(
                                                        result.slotContext()
                                                                .identifier()
                                                )
                                                && !result
                                                .slotContext()
                                                .cosmetic()
                                )
                )
                .orElse(false);
    }

    public static boolean hasLichResearchNotes(Player player) {
        return player != null && CuriosApi.getCuriosInventory(player).resolve()
                .map(handler -> handler.findCurios(ModItems.LICH_RESEARCH_NOTES.get())
                        .stream()
                        .anyMatch(result -> FoodBookCurio.CHARM_SLOT.equals(
                                result.slotContext().identifier())
                                && !result.slotContext().cosmetic()))
                .orElse(false);
    }

    public static boolean hasChaosBlade(Player player) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .map(handler -> handler.findCurios(ModItems.CHAOS_BLADE.get()).stream()
                        .anyMatch(result -> FoodBookCurio.CHARM_SLOT.equals(
                                result.slotContext().identifier())
                                && !result.slotContext().cosmetic()))
                .orElse(false);
    }

    public static boolean hasSoulTank(
        Player player
) {
    return hasEquippedCurio(
            player,
            ModItems.SOUL_TANK.get(),
            FoodBookCurio.CHARM_SLOT
    );
}

    public static boolean hasEquippedCurio(
            Player player,
            Item item
    ) {
        if (RedemptionAccessController.deny(player, new ItemStack(item))) {
            return false;
        }
        return CuriosApi
                .getCuriosInventory(player)
                .resolve()
                .map(handler ->
                        handler.findCurios(item)
                                .stream()
                                .anyMatch(result ->
                                        !result.slotContext().cosmetic()
                                )
                )
                .orElse(false);
    }

    public static boolean hasEquippedCurio(
            Player player,
            Item item,
            String slotId
    ) {
        if (RedemptionAccessController.deny(player, new ItemStack(item))) {
            return false;
        }
        return CuriosApi
                .getCuriosInventory(player)
                .resolve()
                .map(handler ->
                        handler.findCurios(item)
                                .stream()
                                .anyMatch(result ->
                                        slotId.equals(result.slotContext().identifier())
                                                && !result.slotContext()
                                                .cosmetic()
                                )
                )
                .orElse(false);
    }
}
