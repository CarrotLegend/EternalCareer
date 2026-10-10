package com.carrot123.eternal_career.soulcurse;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class SoulBoxItem extends Item implements ICurioItem {
    public static final ResourceLocation FRAGMENT = new ResourceLocation("enigmaticdelicacy", "cursed_soul_fragment");
    public static final ResourceLocation[] UNLOCK_ITEMS = {new ResourceLocation("cataclysm", "witherite_ingot"), new ResourceLocation("aether", "silver_dungeon_key"), new ResourceLocation("irons_spellbooks", "pyrium_ingot"), new ResourceLocation("goety", "unholy_blood")};
    private static final String FRAGMENTS = "EternalCareerSoulFragments", STAGE = "EternalCareerSoulStage";
    public SoulBoxItem(Properties properties) { super(properties.stacksTo(1)); }
    public static int stage(ItemStack stack) { return !stack.isEmpty() && stack.getItem() instanceof SoulBoxItem && stack.hasTag() ? Math.max(0, Math.min(4, stack.getTag().getInt(STAGE))) : 0; }
    public static int capacity(ItemStack stack) { return 10 * (stage(stack) + 1); }
    public static int fragments(ItemStack stack) { return !stack.isEmpty() && stack.getItem() instanceof SoulBoxItem && stack.hasTag() ? Math.max(0, Math.min(capacity(stack), stack.getTag().getInt(FRAGMENTS))) : 0; }
    public static int damagePercent(ItemStack stack) { return fragments(stack) * 20; }
    public static boolean addFragment(ItemStack stack) { if (!(stack.getItem() instanceof SoulBoxItem) || fragments(stack) >= capacity(stack)) return false; stack.getOrCreateTag().putInt(FRAGMENTS, fragments(stack) + 1); return true; }
    public static boolean unlock(ItemStack stack, ItemStack material) { int level = stage(stack); if (!(stack.getItem() instanceof SoulBoxItem) || level >= 4 || !matches(material, UNLOCK_ITEMS[level])) return false; stack.getOrCreateTag().putInt(STAGE, level + 1); return true; }
    public static boolean matches(ItemStack stack, ResourceLocation id) { return !stack.isEmpty() && id.equals(ForgeRegistries.ITEMS.getKey(stack.getItem())); }
    @Override public boolean canEquip(SlotContext context, ItemStack stack) {
        if (context == null || context.cosmetic() || !"scroll".equals(context.identifier()) || !(context.entity() instanceof Player player) || !SoulFaction.hasCore(player)) return false;
        return CuriosApi.getCuriosInventory(player).resolve().map(handler -> handler.findCurios(this).stream().noneMatch(result -> !result.slotContext().cosmetic() && "scroll".equals(result.slotContext().identifier()) && result.slotContext().index() != context.index())).orElse(false);
    }
    @Override public boolean canEquipFromUse(SlotContext context, ItemStack stack) { return canEquip(context, stack); }
    @Override public List<net.minecraft.network.chat.Component> getAttributesTooltip(List<net.minecraft.network.chat.Component> tooltip, ItemStack stack) { tooltip.clear(); return tooltip; }
    @Override public List<net.minecraft.network.chat.Component> getSlotsTooltip(List<net.minecraft.network.chat.Component> tooltip, ItemStack stack) { tooltip.clear(); return tooltip; }
}
