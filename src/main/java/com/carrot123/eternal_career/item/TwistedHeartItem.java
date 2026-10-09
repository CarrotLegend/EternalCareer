package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.api.items.ICursed;
import com.aizistral.enigmaticlegacy.handlers.SuperpositionHandler;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public final class TwistedHeartItem
        extends Item
        implements ICurioItem, ICursed {

    public static final String SLOT =
            "charm";

    public TwistedHeartItem(
            Properties properties
    ) {
        super(properties);
    }

    @Override
    public boolean canEquip(
            SlotContext context,
            ItemStack stack
    ) {
        if (context == null) {
            return false;
        }

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

        return SuperpositionHandler
                .isTheCursedOne(player);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {
        tooltip.add(
                Component.translatable(
                        "tooltip.eternal_career.twisted_heart"
                )
        );
    }
}