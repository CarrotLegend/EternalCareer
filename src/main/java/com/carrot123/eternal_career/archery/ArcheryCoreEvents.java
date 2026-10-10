package com.carrot123.eternal_career.archery;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.ArcheryMasterCoreItem;
import com.carrot123.eternal_career.registry.ModItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID)
public final class ArcheryCoreEvents {

    private static final int TICKS_PER_BONUS = 20;
    private static final int MAX_BURST_BONUS = 5;
    private static final int MAX_CHARGE_TICKS =
            TICKS_PER_BONUS * MAX_BURST_BONUS;

    private static final String START_TICK_KEY =
            "EternalCareerArcheryBurstStartTick";

    private static final String MAX_CHARGE_SOUND_KEY =
            "EternalCareerArcheryBurstMaxChargeSound";

    private static final String PENDING_BONUS_KEY =
            "EternalCareerArcheryBurstPendingBonus";

    private static final String PENDING_TICK_KEY =
            "EternalCareerArcheryBurstPendingTick";

    private static final String PROJECTILE_BONUS_KEY =
            "EternalCareerArcheryBurstBonus";

    private ArcheryCoreEvents() {
    }

    public static boolean hasMode(
            Player player,
            int mode
    ) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .map(handler ->
                        handler.findCurios(
                                        ModItems.ARCHERY_MASTER_CORE.get()
                                )
                                .stream()
                                .anyMatch(result ->
                                        ArcheryMasterCoreItem.SLOT.equals(
                                                result.slotContext()
                                                        .identifier()
                                        )
                                                && !result.slotContext()
                                                .cosmetic()
                                                && ArcheryMasterCoreItem.mode(
                                                result.stack()
                                        ) == mode
                                )
                )
                .orElse(false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseStart(
            LivingEntityUseItemEvent.Start event
    ) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide()
                || !hasMode(
                player,
                ArcheryMasterCoreItem.BURST
        )) {
            return;
        }

        CompoundTag data =
                player.getPersistentData();

        data.putLong(
                START_TICK_KEY,
                player.level().getGameTime()
        );

        data.remove(
                MAX_CHARGE_SOUND_KEY
        );

        data.remove(
                PENDING_BONUS_KEY
        );

        data.remove(
                PENDING_TICK_KEY
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseTick(
            LivingEntityUseItemEvent.Tick event
    ) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide()
                || !hasMode(
                player,
                ArcheryMasterCoreItem.BURST
        )) {
            return;
        }

        playMaxChargeSoundIfReady(
                player
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseStop(
            LivingEntityUseItemEvent.Stop event
    ) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide()) {
            return;
        }

        finishCharge(
                player
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseFinish(
            LivingEntityUseItemEvent.Finish event
    ) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide()) {
            return;
        }

        finishCharge(
                player
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onArrowLoose(
            ArrowLooseEvent event
    ) {
        Player player =
                event.getEntity();

        if (event.getLevel().isClientSide()
                || !hasMode(
                player,
                ArcheryMasterCoreItem.BURST
        )) {
            return;
        }

        playMaxChargeSoundIfReady(
                player
        );

        int chargeTicks =
                Math.max(
                        0,
                        event.getCharge()
                );

        setPendingBonus(
                player,
                chargeTicks
        );

        player.getPersistentData()
                .remove(
                        START_TICK_KEY
                );

        player.getPersistentData()
                .remove(
                        MAX_CHARGE_SOUND_KEY
                );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onProjectileJoin(
            EntityJoinLevelEvent event
    ) {
        if (event.getLevel().isClientSide()
                || event.loadedFromDisk()
                || !(event.getEntity()
                instanceof Projectile projectile)) {
            return;
        }

        Entity owner =
                projectile.getOwner();

        if (!(owner instanceof Player player)
                || !hasMode(
                player,
                ArcheryMasterCoreItem.BURST
        )) {
            return;
        }

        CompoundTag playerData =
                player.getPersistentData();

        int bonus =
                0;

        if (playerData.contains(
                PENDING_BONUS_KEY
        )
                && playerData.contains(
                PENDING_TICK_KEY
        )) {

            long pendingTick =
                    playerData.getLong(
                            PENDING_TICK_KEY
                    );

            long currentTick =
                    event.getLevel()
                            .getGameTime();

            if (currentTick >= pendingTick
                    && currentTick - pendingTick <= 5L) {

                bonus =
                        playerData.getInt(
                                PENDING_BONUS_KEY
                        );
            }
        }

        if (bonus <= 0
                && playerData.contains(
                START_TICK_KEY
        )) {

            long startTick =
                    playerData.getLong(
                            START_TICK_KEY
                    );

            long elapsed =
                    Math.max(
                            0L,
                            player.level()
                                            .getGameTime()
                                    - startTick
                    );

            bonus =
                    Math.min(
                            MAX_BURST_BONUS,
                            (int) (
                                    elapsed
                                            / TICKS_PER_BONUS
                            )
                    );
        }

        bonus =
                Math.max(
                        0,
                        Math.min(
                                MAX_BURST_BONUS,
                                bonus
                        )
                );

        if (bonus <= 0) {
            return;
        }

        projectile.getPersistentData()
                .putInt(
                        PROJECTILE_BONUS_KEY,
                        bonus
                );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onProjectileHurt(
            LivingHurtEvent event
    ) {
        if (event.getEntity()
                .level()
                .isClientSide()) {
            return;
        }

        Entity directEntity =
                event.getSource()
                        .getDirectEntity();

        if (!(directEntity
                instanceof Projectile projectile)) {
            return;
        }

        CompoundTag data =
                projectile.getPersistentData();

        if (!data.contains(
                PROJECTILE_BONUS_KEY
        )) {
            return;
        }

        int bonus =
                Math.max(
                        0,
                        Math.min(
                                MAX_BURST_BONUS,
                                data.getInt(
                                        PROJECTILE_BONUS_KEY
                                )
                        )
                );

        if (bonus <= 0) {
            return;
        }

        float original =
                event.getAmount();

        if (original <= 0.0F
                || !Float.isFinite(
                original
        )) {
            return;
        }

        double multiplier =
                1.0D + bonus * 1.20D;

        double modified =
                original * multiplier;

        if (!Double.isFinite(
                modified
        )) {
            return;
        }

        event.setAmount(
                modified >= Float.MAX_VALUE
                        ? Float.MAX_VALUE
                        : (float) modified
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(
            TickEvent.PlayerTickEvent event
    ) {
        if (event.phase
                != TickEvent.Phase.END
                || event.player
                .level()
                .isClientSide()) {
            return;
        }

        Player player =
                event.player;

        CompoundTag data =
                player.getPersistentData();

        long currentTick =
                player.level()
                        .getGameTime();

        if (data.contains(
                PENDING_TICK_KEY
        )) {

            long pendingTick =
                    data.getLong(
                            PENDING_TICK_KEY
                    );

            if (currentTick
                    - pendingTick > 5L) {

                data.remove(
                        PENDING_BONUS_KEY
                );

                data.remove(
                        PENDING_TICK_KEY
                );
            }
        }

        if (data.contains(
                START_TICK_KEY
        )) {
            if (player.isUsingItem()) {
                playMaxChargeSoundIfReady(
                        player
                );
            } else {
                long startTick =
                        data.getLong(
                                START_TICK_KEY
                        );

                if (currentTick
                        - startTick > 10L) {

                    data.remove(
                            START_TICK_KEY
                    );

                    data.remove(
                            MAX_CHARGE_SOUND_KEY
                    );
                }
            }
        }

        if (!hasMode(
                player,
                ArcheryMasterCoreItem.BURST
        )) {
            data.remove(
                    START_TICK_KEY
            );

            data.remove(
                    MAX_CHARGE_SOUND_KEY
            );

            data.remove(
                    PENDING_BONUS_KEY
            );

            data.remove(
                    PENDING_TICK_KEY
            );
        }
    }

    private static void finishCharge(
            Player player
    ) {
        CompoundTag data =
                player.getPersistentData();

        if (!hasMode(
                player,
                ArcheryMasterCoreItem.BURST
        )) {
            data.remove(
                    START_TICK_KEY
            );

            data.remove(
                    MAX_CHARGE_SOUND_KEY
            );

            data.remove(
                    PENDING_BONUS_KEY
            );

            data.remove(
                    PENDING_TICK_KEY
            );

            return;
        }

        if (!data.contains(
                START_TICK_KEY
        )) {
            return;
        }

        playMaxChargeSoundIfReady(
                player
        );

        long startTick =
                data.getLong(
                        START_TICK_KEY
                );

        long elapsed =
                Math.max(
                        0L,
                        player.level()
                                        .getGameTime()
                                - startTick
                );

        setPendingBonus(
                player,
                (int) Math.min(
                        Integer.MAX_VALUE,
                        elapsed
                )
        );

        data.remove(
                START_TICK_KEY
        );

        data.remove(
                MAX_CHARGE_SOUND_KEY
        );
    }

    private static void playMaxChargeSoundIfReady(
            Player player
    ) {
        CompoundTag data =
                player.getPersistentData();

        if (!data.contains(
                START_TICK_KEY
        )
                || data.getBoolean(
                MAX_CHARGE_SOUND_KEY
        )) {
            return;
        }

        long startTick =
                data.getLong(
                        START_TICK_KEY
                );

        long elapsed =
                Math.max(
                        0L,
                        player.level()
                                        .getGameTime()
                                - startTick
                );

        if (elapsed < MAX_CHARGE_TICKS) {
            return;
        }

        data.putBoolean(
                MAX_CHARGE_SOUND_KEY,
                true
        );

        player.level()
                .playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                        SoundSource.PLAYERS,
                        0.8F,
                        1.6F
                );
    }

    private static void setPendingBonus(
            Player player,
            int chargeTicks
    ) {
        CompoundTag data =
                player.getPersistentData();

        int bonus =
                Math.max(
                        0,
                        Math.min(
                                MAX_BURST_BONUS,
                                chargeTicks
                                        / TICKS_PER_BONUS
                        )
                );

        data.remove(
                PENDING_BONUS_KEY
        );

        data.remove(
                PENDING_TICK_KEY
        );

        if (bonus <= 0) {
            return;
        }

        data.putInt(
                PENDING_BONUS_KEY,
                bonus
        );

        data.putLong(
                PENDING_TICK_KEY,
                player.level()
                        .getGameTime()
        );
    }
}