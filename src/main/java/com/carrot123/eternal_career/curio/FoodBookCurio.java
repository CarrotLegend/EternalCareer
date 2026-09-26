package com.carrot123.eternal_career.curio;

import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class FoodBookCurio implements ICurioItem {

    public static final ResourceLocation FOOD_BOOK_ID =
            new ResourceLocation("solcarrot", "food_book");

    public static final String CHARM_SLOT = "charm";

    public static final FoodBookCurio INSTANCE = new FoodBookCurio();

    public FoodBookCurio() {
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return isFunctionalCharmSlot(slotContext)
                && slotContext.entity() instanceof Player player
                && RedemptionAccessController.canEquip(player, stack);
    }

    @Override
    public List<Component> getAttributesTooltip(
            List<Component> tooltips,
            ItemStack stack
    ) {
        tooltips.clear();
        return tooltips;
    }

    private static boolean isFunctionalCharmSlot(SlotContext slotContext) {
        return slotContext != null
                && CHARM_SLOT.equals(slotContext.identifier())
                && !slotContext.cosmetic();
    }
}
