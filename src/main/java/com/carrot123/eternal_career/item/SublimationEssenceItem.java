package com.carrot123.eternal_career.item;

import java.util.Map;

import it.hurts.sskirillss.relics.items.relics.base.IRelicItem;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.AbilityData;
import it.hurts.sskirillss.relics.items.relics.base.data.leveling.StatData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public final class SublimationEssenceItem extends Item {

    public static final int USE_DURATION_TICKS = 20;

    public SublimationEssenceItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand) {

        ItemStack essenceStack = player.getItemInHand(hand);

        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResultHolder.fail(essenceStack);
        }

        ItemStack offhandStack = player.getOffhandItem();

        if (!(offhandStack.getItem() instanceof IRelicItem)) {
            return InteractionResultHolder.fail(essenceStack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(essenceStack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public ItemStack finishUsingItem(
            ItemStack essenceStack,
            Level level,
            LivingEntity entity) {

        if (!(level instanceof ServerLevel serverLevel)
                || !(entity instanceof ServerPlayer player)) {
            return essenceStack;
        }

        ItemStack relicStack = player.getOffhandItem();

        if (!(relicStack.getItem() instanceof IRelicItem relic)) {
            return essenceStack;
        }

        maximizeRelicQuality(relic, relicStack);

        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );

        serverLevel.sendParticles(
                ParticleTypes.TOTEM_OF_UNDYING,
                player.getX(),
                player.getY() + player.getBbHeight() * 0.5D,
                player.getZ(),
                40,
                0.5D,
                0.5D,
                0.5D,
                0.15D
        );

        if (!player.getAbilities().instabuild) {
            essenceStack.shrink(1);
        }

        return essenceStack;
    }

    private static void maximizeRelicQuality(
            IRelicItem relic,
            ItemStack relicStack) {

        for (Map.Entry<String, AbilityData> abilityEntry
                : relic.getRelicData()
                       .getAbilities()
                       .getAbilities()
                       .entrySet()) {

            String abilityId = abilityEntry.getKey();
            AbilityData abilityData = abilityEntry.getValue();

            for (Map.Entry<String, StatData> statEntry
                    : abilityData.getStats().entrySet()) {

                String statId = statEntry.getKey();
                StatData statData = statEntry.getValue();

                double maxValue =
                        statData.getInitialValue().getValue();

                relic.setAbilityValue(
                        relicStack,
                        abilityId,
                        statId,
                        maxValue
                );
            }
        }
    }
}