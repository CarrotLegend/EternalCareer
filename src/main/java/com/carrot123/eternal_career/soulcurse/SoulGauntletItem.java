package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.item.CurseEquipHelper;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class SoulGauntletItem extends Item implements ICurioItem {
    public static final int MAX_KILLS = 4000;
    private static final String KILLS = "EternalCareerSoulGauntletKills";
    public SoulGauntletItem(Properties properties) { super(properties); }
    public static int kills(ItemStack stack) { return stack.hasTag() ? Math.min(MAX_KILLS, Math.max(0, stack.getTag().getInt(KILLS))) : 0; }
    public static int damageTenthsPercent(ItemStack stack) { return kills(stack) * 5; }
    public static boolean addKill(ItemStack stack) {
        if (!(stack.getItem() instanceof SoulGauntletItem) || kills(stack) >= MAX_KILLS) return false;
        stack.getOrCreateTag().putInt(KILLS, kills(stack) + 1);
        return true;
    }
    @Override public boolean canEquip(SlotContext context, ItemStack stack) {
        return !context.cosmetic() && "hands".equals(context.identifier()) && context.entity() instanceof Player player
                && SoulFaction.hasCore(player) && CurseEquipHelper.canEquipSingle(player, this, context);
    }
    @Override public boolean canEquipFromUse(SlotContext context, ItemStack stack) { return canEquip(context, stack); }
    @Override public List<Component> getAttributesTooltip(List<Component> lines, ItemStack stack) { lines.clear(); return lines; }
    @Override public List<Component> getSlotsTooltip(List<Component> lines, ItemStack stack) { lines.clear(); return lines; }
}
