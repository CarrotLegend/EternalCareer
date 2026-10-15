package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.item.CurseEquipHelper;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class SoulGauntletItem extends Item implements ICurioItem {
    public static final int MAX_KILLS = 4000;
    private static final String TYPES = "EternalCareerSoulGauntletKilledTypes";
    private static final String OLD_KILLS = "EternalCareerSoulGauntletKills";

    public SoulGauntletItem(Properties properties) { super(properties); }

    public static int kills(ItemStack stack) {
        return stack.getItem() instanceof SoulGauntletItem && stack.hasTag()
                ? Math.min(MAX_KILLS, stack.getTag().getList(TYPES, Tag.TAG_STRING).size()) : 0;
    }

    public static int damageTenthsPercent(ItemStack stack) { return kills(stack) * 5; }

    public static boolean addKill(ItemStack stack, ResourceLocation mobId) {
        if (!(stack.getItem() instanceof SoulGauntletItem) || mobId == null) return false;
        CompoundTag nbt = stack.getOrCreateTag();
        ListTag types = nbt.getList(TYPES, Tag.TAG_STRING);
        if (types.size() >= MAX_KILLS) return false;
        String id = mobId.toString();
        for (int i = 0; i < types.size(); i++) if (id.equals(types.getString(i))) return false;
        types.add(StringTag.valueOf(id));
        nbt.put(TYPES, types);
        nbt.remove(OLD_KILLS);
        return true;
    }

    @Override public boolean canEquip(SlotContext context, ItemStack stack) {
        return !context.cosmetic() && "hands".equals(context.identifier())
                && context.entity() instanceof Player player && SoulFaction.hasCore(player)
                && CurseEquipHelper.canEquipSingle(player, this, context);
    }

    @Override public boolean canEquipFromUse(SlotContext context, ItemStack stack) { return canEquip(context, stack); }
    @Override public List<Component> getAttributesTooltip(List<Component> lines, ItemStack stack) { lines.clear(); return lines; }
    @Override public List<Component> getSlotsTooltip(List<Component> lines, ItemStack stack) { lines.clear(); return lines; }
}
