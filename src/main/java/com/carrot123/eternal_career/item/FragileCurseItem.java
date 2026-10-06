package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import com.carrot123.eternal_career.registry.ModItems;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class FragileCurseItem extends Item implements ICurioItem, ICursed {

    public static final String SLOT =
            PandoraBoxItem.CURSE_SPIRIT_SLOT;

    private static final String REVERSED_TAG =
            "EternalCareerFragileCurseReversed";

    public FragileCurseItem(Properties properties) {
        super(properties);
    }

    public static boolean isReversed(ItemStack stack) {
        return stack.hasTag()
                && stack.getTag() != null
                && stack.getTag().getBoolean(REVERSED_TAG);
    }

    public static void reverse(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        stack.getOrCreateTag().putBoolean(
                REVERSED_TAG,
                true
        );
    }

    @Override
    public boolean canEquip(
            SlotContext context,
            ItemStack stack
    ) {
        if (!SLOT.equals(context.identifier())) {
            return false;
        }

        if (context.cosmetic()) {
            return false;
        }

        if (!(context.entity() instanceof Player player)) {
            return false;
        }

        if (!SuperpositionHandler.isTheCursedOne(player)) {
            return false;
        }

        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler ->
                        handler.findCurios(
                                        ModItems.FRAGILE_CURSE.get()
                                )
                                .stream()
                                .noneMatch(result ->
                                        !result.slotContext().cosmetic()
                                                && SLOT.equals(
                                                result.slotContext()
                                                        .identifier()
                                        )
                                )
                )
                .orElse(false);
    }

    @Override
    public List<Component> getAttributesTooltip(
            List<Component> tooltips,
            ItemStack stack
    ) {
        tooltips.clear();
        return tooltips;
    }

    @Override
    public List<Component> getSlotsTooltip(
            List<Component> tooltips,
            ItemStack stack
    ) {
        tooltips.clear();
        return tooltips;
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        if (!isReversed(stack)) {
            tooltip.add(
                    Component.translatable(
                                    "tooltip.eternal_career.fragile_curse"
                            )
                            .withStyle(
                                    ChatFormatting.RED
                            )
            );

            return;
        }

        tooltip.add(
                Component.translatable(
                                "tooltip.eternal_career.fragile_curse.reversed"
                        )
                        .withStyle(
                                ChatFormatting.GOLD
                        )
        );
    }
    @Override
public boolean canUnequip(
        SlotContext context,
        ItemStack stack
) {
    return false;
}
}