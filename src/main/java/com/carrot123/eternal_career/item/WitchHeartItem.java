package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.helpers.ItemLoreHelper;
import com.carrot123.eternal_career.witch.WitchHeartMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import java.util.List;

public final class WitchHeartItem extends Item implements ICurioItem {
    public static final String BREW_KEY = "WitchHeartBrew";
    public static final String NEXT_USE_KEY = "WitchHeartNextUse";

    public WitchHeartItem(Properties properties) { super(properties.stacksTo(1)); }

    @Override
    public boolean canEquip(SlotContext context, ItemStack stack) {
        return context != null && !context.cosmetic() && "charm".equals(context.identifier()) && context.entity() instanceof Player;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heart = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, new SimpleMenuProvider((id, inventory, viewer) ->
                    new WitchHeartMenu(id, inventory, hand), Component.translatable("container.eternal_career.witch_heart")), buffer -> buffer.writeEnum(hand));
        }
        return InteractionResultHolder.sidedSuccess(heart, level.isClientSide);
    }

    public static ItemStack getStoredBrew(ItemStack heart) {
        CompoundTag tag = heart.getTag();
        return tag != null && tag.contains(BREW_KEY, 10) ? ItemStack.of(tag.getCompound(BREW_KEY)) : ItemStack.EMPTY;
    }

    public static void setStoredBrew(ItemStack heart, ItemStack brew) {
        CompoundTag tag = heart.getOrCreateTag();
        if (brew.isEmpty()) tag.remove(BREW_KEY);
        else tag.put(BREW_KEY, brew.save(new CompoundTag()));
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
        ItemLoreHelper.addLocalizedFormattedString(tooltip, "curios.modifiers.charm", ChatFormatting.GOLD);
        ItemLoreHelper.addLocalizedString(tooltip, "tooltip.eternal_career.witch_heart.effect");
        ItemLoreHelper.addLocalizedString(tooltip, "tooltip.eternal_career.witch_heart.infinite");
        ItemLoreHelper.addLocalizedString(tooltip, "tooltip.enigmaticlegacy.void");
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltip, ItemStack stack) { tooltip.clear(); return tooltip; }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltip, ItemStack stack) { tooltip.clear(); return tooltip; }
}
