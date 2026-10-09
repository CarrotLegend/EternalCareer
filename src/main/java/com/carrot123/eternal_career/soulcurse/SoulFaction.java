package com.carrot123.eternal_career.soulcurse;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.redemption.RedemptionAccessController;
import com.carrot123.eternal_career.compat.redemption.RedemptionItemHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public final class SoulFaction {
    public static final TagKey<Item> SOUL_ITEMS = TagKey.create(Registries.ITEM, new ResourceLocation(EternalCareer.MOD_ID, "soul_faction_items"));
    private SoulFaction() {}
    public static boolean isCore(ItemStack stack) { return !stack.isEmpty() && (stack.is(SoulCoreRegistry.SOUL_CORE.get()) || stack.is(SoulCoreRegistry.BLOOD_HUNTER_CORE.get()) || stack.is(SoulCoreRegistry.HYENA_CORE.get())); }
    public static boolean isForeign(ItemStack stack) { return !stack.isEmpty() && (stack.getItem() instanceof ICursed || RedemptionItemHelper.isRedemptionItem(stack) || RedemptionItemHelper.RING_OF_REDEMPTION_ID.equals(ForgeRegistries.ITEMS.getKey(stack.getItem()))); }
    public static boolean isSoulItem(ItemStack stack) { return !stack.isEmpty() && stack.is(SOUL_ITEMS) && !isForeign(stack); }
    public static boolean hasCore(Player player) {
        return player != null && CuriosApi.getCuriosInventory(player).resolve().map(handler -> handler.findCurios(SoulFaction::isCore).stream().anyMatch(result -> !result.slotContext().cosmetic() && "ring".equals(result.slotContext().identifier()))).orElse(false);
    }
    public static boolean hasForeignCurio(Player player) {
        return player != null && CuriosApi.getCuriosInventory(player).resolve().map(handler -> handler.getCurios().values().stream().anyMatch(slot -> {
            for (int i = 0; i < slot.getStacks().getSlots(); i++) if (isForeign(slot.getStacks().getStackInSlot(i))) return true;
            return false;
        })).orElse(false);
    }
    public static boolean canJoin(Player player) {
        if (player == null || hasCore(player) || SuperpositionHandler.isTheCursedOne(player) || RedemptionAccessController.hasLiveRedemptionAccess(player) || hasForeignCurio(player)) return false;
        for (ItemStack stack : player.getInventory().armor) if (isForeign(stack)) return false;
        return true;
    }
    public static boolean canEquip(Player player, ItemStack stack) {
        if (player == null || stack.isEmpty()) return true;
        if (isCore(stack)) return canJoin(player);
        if (hasCore(player)) return !isForeign(stack);
        return !isSoulItem(stack);
    }
    public static boolean canUse(Player player, ItemStack stack) {
        if (player == null || stack.isEmpty()) return true;
        if (isCore(stack)) return canJoin(player);
        return hasCore(player) ? !isForeign(stack) : !isSoulItem(stack);
    }
}
