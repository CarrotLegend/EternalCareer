package com.carrot123.eternal_career.compat.redemption;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;

/** Shared classification and Curios equipment checks for redemption items. */
public final class RedemptionItemHelper {
    public static final String RING_SLOT = "ring";
    public static final ResourceLocation RING_OF_REDEMPTION_ID =
            new ResourceLocation("enigmaticaddons", "bless_ring");
    public static final TagKey<Item> REDEMPTION_ITEMS = TagKey.create(
            Registries.ITEM,
            new ResourceLocation(EternalCareer.MOD_ID, "redemption_items"));

    private RedemptionItemHelper() {
    }

    public static boolean isRedemptionItem(ItemStack stack) {
        return stack.is(REDEMPTION_ITEMS);
    }

    public static boolean hasRingOfRedemption(Player player) {
        Item ring = ForgeRegistries.ITEMS.getValue(RING_OF_REDEMPTION_ID);
        if (ring == null || ring == Items.AIR) {
            return false;
        }
        return CuriosApi.getCuriosInventory(player).resolve()
                .map(handler -> handler.findCurios(ring).stream().anyMatch(result ->
                        RING_SLOT.equals(result.slotContext().identifier())
                                && !result.slotContext().cosmetic()))
                .orElse(false);
    }

    public static boolean canUseRedemptionItem(Player player, ItemStack stack) {
        return !isRedemptionItem(stack) || hasRingOfRedemption(player);
    }
}
