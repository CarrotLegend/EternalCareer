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

public final class UndeadCurseItem extends Item implements ICurioItem, ICursed {

    public static final String SLOT =
            PandoraBoxItem.CURSE_SPIRIT_SLOT;

    private static final String UPGRADED_TAG =
            "EternalCareerUndeadCurseUpgraded";

    public UndeadCurseItem(Properties properties) {
        super(properties);
    }

    public static boolean isUpgraded(ItemStack stack) {
        return stack.hasTag()
                && stack.getTag() != null
                && stack.getTag().getBoolean(UPGRADED_TAG);
    }

    public static void upgrade(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }

        stack.getOrCreateTag().putBoolean(
                UPGRADED_TAG,
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
                                        ModItems.UNDEAD_CURSE.get()
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
        if (!isUpgraded(stack)) {
            tooltip.add(
                    Component.translatable(
                                    "tooltip.eternal_career.undead_curse"
                            )
                            .withStyle(ChatFormatting.RED)
            );

            return;
        }

        tooltip.add(
                Component.translatable(
                                "tooltip.eternal_career.undead_curse.upgraded.day"
                        )
                        .withStyle(ChatFormatting.WHITE)
        );

        tooltip.add(
                Component.translatable(
                                "tooltip.eternal_career.undead_curse.upgraded.night"
                        )
                        .withStyle(ChatFormatting.WHITE)
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