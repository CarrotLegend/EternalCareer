package com.carrot123.eternal_career.soulcurse;

import com.carrot123.eternal_career.item.CurseEquipHelper;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class TransformingScarItem extends Item implements ICurioItem {
    public static final String SLOT = "scar";
    private static final String STORED = "EternalCareerStoredSoulScars";
    public TransformingScarItem(Properties properties) { super(properties); }
    @Override public boolean canEquip(SlotContext context, ItemStack stack) {
        return !context.cosmetic() && "charm".equals(context.identifier()) && context.entity() instanceof Player p
                && SoulFaction.hasCore(p) && CurseEquipHelper.canEquipSingle(p, this, context);
    }
    @Override public boolean canEquipFromUse(SlotContext context, ItemStack stack) { return canEquip(context, stack); }
    @Override public boolean canUnequip(SlotContext context, ItemStack stack) { return context.entity() instanceof Player p && p.getAbilities().instabuild; }
    @Override public ICurio.DropRule getDropRule(SlotContext context, DamageSource source, int looting, boolean recentlyHit, ItemStack stack) { return ICurio.DropRule.ALWAYS_KEEP; }
    @Override public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext context, UUID slotUuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> attributes = LinkedHashMultimap.create();
        if (!context.cosmetic() && "charm".equals(context.identifier())) CuriosApi.addSlotModifier(attributes, SLOT, slotUuid, 4.0D, AttributeModifier.Operation.ADDITION);
        return attributes;
    }
    @Override public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.eternal_career.transforming_scar.slots").withStyle(ChatFormatting.GOLD));
        lines.add(Component.translatable("tooltip.eternal_career.transforming_scar.flavor").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
    }
    @Override public void onUnequip(SlotContext context, ItemStack newStack, ItemStack oldStack) {
        if (!(context.entity() instanceof ServerPlayer player) || !player.getAbilities().instabuild || newStack.is(this)) return;
        CuriosApi.getCuriosInventory(player).resolve().ifPresent(handler -> handler.getStacksHandler(SLOT).ifPresent(slot -> {
            ListTag saved = new ListTag();
            for (int i = 0; i < slot.getStacks().getSlots(); i++) {
                ItemStack item = slot.getStacks().getStackInSlot(i);
                CompoundTag nbt = new CompoundTag();
                if (!item.isEmpty()) item.save(nbt);
                saved.add(nbt);
                if (!item.isEmpty()) handler.setEquippedCurio(SLOT, i, ItemStack.EMPTY);
            }
            oldStack.getOrCreateTag().put(STORED, saved);
        }));
    }
    public static ListTag savedScars(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getList(STORED, Tag.TAG_COMPOUND) : new ListTag();
    }
    @Override public List<Component> getAttributesTooltip(List<Component> lines, ItemStack stack) { lines.clear(); return lines; }
    @Override public List<Component> getSlotsTooltip(List<Component> lines, ItemStack stack) { lines.clear(); return lines; }
}
