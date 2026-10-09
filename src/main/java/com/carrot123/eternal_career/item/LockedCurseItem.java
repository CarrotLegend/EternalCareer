package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public abstract class LockedCurseItem
        extends Item
        implements ICurioItem, ICursed {

    public static final String SLOT =
            PandoraBoxItem.CURSE_SPIRIT_SLOT;

    protected LockedCurseItem(
            Properties properties
    ) {
        super(properties);
    }

    @Override
    public boolean canEquip(
            SlotContext context,
            ItemStack stack
    ) {
        if (!SLOT.equals(
                context.identifier()
        )) {
            return false;
        }

        if (context.cosmetic()) {
            return false;
        }

        if (!(context.entity()
                instanceof Player player)) {
            return false;
        }

        if (!SuperpositionHandler
                .isTheCursedOne(player)) {
            return false;
        }

        return CurseEquipHelper.canEquipSingle(
                player,
                this,
                context
        );
    }

    @Override
    public boolean canUnequip(
            SlotContext context,
            ItemStack stack
    ) {
        return false;
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
}