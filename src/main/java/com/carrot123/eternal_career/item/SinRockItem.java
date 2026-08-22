package com.carrot123.eternal_career.item;

import com.aizistral.enigmaticlegacy.EnigmaticLegacy;
import com.aizistral.enigmaticlegacy.api.capabilities.IPlaytimeCounter;
import com.aizistral.enigmaticlegacy.packets.clients.PacketSyncPlayTime;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

/** A consumable that normalizes Enigmatic Legacy's Seven Curses playtime ratio. */
public final class SinRockItem extends Item {
    public static final int USE_DURATION_TICKS = 80;

    public SinRockItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
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
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (level instanceof ServerLevel serverLevel && entity instanceof ServerPlayer player) {
            normalizeCursedPlaytime(player);
            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.TOTEM_USE,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F);
            serverLevel.sendParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    player.getX(),
                    player.getY() + player.getBbHeight() * 0.5D,
                    player.getZ(),
                    40,
                    0.5D,
                    0.5D,
                    0.5D,
                    0.15D);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return stack;
    }

    private static void normalizeCursedPlaytime(ServerPlayer player) {
        int totalPlayTime = Math.max(
                0,
                player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME)));
        IPlaytimeCounter counter = IPlaytimeCounter.get(player);
        counter.setTimeWithCurses(totalPlayTime);
        counter.setTimeWithoutCurses(0L);

        player.getStats().sendStats(player);
        EnigmaticLegacy.packetInstance.send(
                PacketDistributor.PLAYER.with(() -> player),
                new PacketSyncPlayTime(player.getUUID(), totalPlayTime, 0L));
    }
}
