package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.item.CurseEquipHelper;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class SoulScarItem extends Item implements ICurioItem {
    public enum Kind {
        COWARD("cataclysm:cursium_ingot", "coward"),
        FRAGILE("legendary_monsters:anchor_handle", "fragile"),
        PESSIMIST("cataclysm:essence_of_the_storm", "pessimist");
        public final ResourceLocation material;
        public final String name;
        Kind(String material, String name) { this.material = new ResourceLocation(material); this.name = name; }
    }
    private static final String REVERSED = "EternalCareerSoulScarReversed";
    private final Kind kind;
    public SoulScarItem(Kind kind, Properties properties) { super(properties); this.kind = kind; }
    public Kind kind() { return kind; }
    public static boolean reversed(ItemStack stack) { return stack.hasTag() && stack.getTag().getBoolean(REVERSED); }
    public static boolean reverse(ItemStack scar, ItemStack ingredient) {
        if (!(scar.getItem() instanceof SoulScarItem item) || reversed(scar) || ingredient.isEmpty()
                || !item.kind.material.equals(ForgeRegistries.ITEMS.getKey(ingredient.getItem()))) return false;
        scar.getOrCreateTag().putBoolean(REVERSED, true);
        return true;
    }
    @Override public Component getName(ItemStack stack) {
        return Component.translatable("item.eternal_career." + kind.name + (reversed(stack) ? ".reversed" : ""));
    }
    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.eternal_career.scar." + kind.name + (reversed(stack) ? ".reversed" : ""))
                .withStyle(reversed(stack) ? ChatFormatting.GOLD : ChatFormatting.RED));
        if (!reversed(stack)) lines.add(Component.translatable("tooltip.eternal_career.scar.reverse_with",
                Component.translatable(ForgeRegistries.ITEMS.getValue(kind.material) != null
                        ? ForgeRegistries.ITEMS.getValue(kind.material).getDescriptionId() : "item.minecraft.air")).withStyle(ChatFormatting.GRAY));
    }
    @Override public boolean canEquip(SlotContext context, ItemStack stack) {
        if (context.cosmetic() || !TransformingScarItem.SLOT.equals(context.identifier())
                || !(context.entity() instanceof Player player) || !SoulFaction.hasCore(player)) return false;
        return CuriosApi.getCuriosInventory(player).resolve().map(handler -> handler.findCurios(SoulScarRegistry.TRANSFORMING_SCAR.get())
                .stream().anyMatch(result -> !result.slotContext().cosmetic() && "charm".equals(result.slotContext().identifier()))).orElse(false)
                && CurseEquipHelper.canEquipSingle(player, this, context);
    }
    @Override public boolean canUnequip(SlotContext context, ItemStack stack) { return context.entity() instanceof Player p && p.getAbilities().instabuild; }
    @Override public ICurio.DropRule getDropRule(SlotContext context, DamageSource source, int looting, boolean recentlyHit, ItemStack stack) { return ICurio.DropRule.ALWAYS_KEEP; }
    @Override public List<Component> getAttributesTooltip(List<Component> lines, ItemStack stack) { lines.clear(); return lines; }
    @Override public List<Component> getSlotsTooltip(List<Component> lines, ItemStack stack) { lines.clear(); return lines; }
}
