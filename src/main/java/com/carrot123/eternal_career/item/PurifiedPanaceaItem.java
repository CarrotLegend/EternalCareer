package com.carrot123.eternal_career.item;

import com.carrot123.eternal_career.lich.LichStage;
import com.carrot123.eternal_career.lich.LichUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;

public final class PurifiedPanaceaItem extends Item {
    private final LichStage requiredStage;
    private final LichStage targetStage;

    public PurifiedPanaceaItem(Properties properties, LichStage requiredStage, LichStage targetStage) {
        super(properties);
        this.requiredStage = requiredStage;
        this.targetStage = targetStage;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!LichUtils.canAdvanceTo(player, requiredStage, targetStage)) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player)
                || !LichUtils.canAdvanceTo(player, requiredStage, targetStage)
                || !LichUtils.setLichStage(player, targetStage)) {
            return stack;
        }
        CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 40;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    public boolean isTooHighFor(Player player) {
        return LichUtils.getLichStage(player).ordinal() < requiredStage.ordinal();
    }
}
