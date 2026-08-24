package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.redemption.RedemptionItemHelper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import top.theillusivec4.curios.api.event.CurioChangeEvent;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class RedemptionAccessEvents {

    private static final int
            RESPAWN_RECHECK_DELAY =
            20;

    private RedemptionAccessEvents() {
    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public static void onPlayerClone(
            PlayerEvent.Clone event
    ) {
        Player original =
                event.getOriginal();

        Player clone =
                event.getEntity();

        CompoundTag originalData =
                original.getPersistentData();

        boolean hadAccess;

        if (originalData.contains(
                RedemptionItemHelper
                        .ACCESS_CACHE_TAG,
                Tag.TAG_BYTE
        )) {
            hadAccess =
                    originalData.getBoolean(
                            RedemptionItemHelper
                                    .ACCESS_CACHE_TAG
                    );
        } else {

            hadAccess =
                    originalData.getBoolean(
                            RedemptionItemHelper
                                    .ENIGMATIC_ADDONS_BLESS_SPAWN
                    )
                            || RedemptionItemHelper
                            .findRingOfRedemptionNow(
                                    original
                            );
        }

        RedemptionItemHelper
                .setCachedAccess(
                        clone,
                        hadAccess
                );

        RedemptionItemHelper
                .scheduleRecheck(
                        clone,
                        RESPAWN_RECHECK_DELAY
                );
    }

    @SubscribeEvent
    public static void onPlayerLogin(
            PlayerEvent.PlayerLoggedInEvent event
    ) {
        Player player =
                event.getEntity();

        if (player.level().isClientSide) {
            return;
        }

        CompoundTag data =
                player.getPersistentData();

        if (!data.contains(
                RedemptionItemHelper
                        .ACCESS_CACHE_TAG,
                Tag.TAG_BYTE
        )) {
            boolean value =
                    data.getBoolean(
                            RedemptionItemHelper
                                    .ENIGMATIC_ADDONS_BLESS_SPAWN
                    )
                            || RedemptionItemHelper
                            .findRingOfRedemptionNow(
                                    player
                            );

            RedemptionItemHelper
                    .setCachedAccess(
                            player,
                            value
                    );
        }

        RedemptionItemHelper.scheduleRecheck(
                player,
                RESPAWN_RECHECK_DELAY
        );
    }

    @SubscribeEvent
    public static void onPlayerRespawn(
            PlayerEvent.PlayerRespawnEvent event
    ) {
        if (!event.getEntity()
                .level()
                .isClientSide) {

            RedemptionItemHelper
                    .scheduleRecheck(
                            event.getEntity(),
                            RESPAWN_RECHECK_DELAY
                    );
        }
    }

    @SubscribeEvent
    public static void onCurioChange(
            CurioChangeEvent event
    ) {
        if (!(event.getEntity()
                instanceof Player player)) {
            return;
        }

        Item ring =
                ForgeRegistries.ITEMS
                        .getValue(
                                RedemptionItemHelper
                                        .RING_OF_REDEMPTION_ID
                        );

        if (ring == null
                || ring == Items.AIR) {
            return;
        }

        if (event.getTo().is(ring)) {
            RedemptionItemHelper
                    .setCachedAccess(
                            player,
                            true
                    );
            return;
        }

        if (event.getFrom().is(ring)) {
            RedemptionItemHelper
                .scheduleRecheck(
                    player,
                    1
                );
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(
            TickEvent.PlayerTickEvent event
    ) {
        if (event.phase
                != TickEvent.Phase.END) {
            return;
        }

        Player player =
            event.player;

        if (player.level().isClientSide) {
            return;
        }

        CompoundTag data =
            player.getPersistentData();

        int ticks =
            data.getInt(
                RedemptionItemHelper
                    .RECHECK_TICKS_TAG
            );

        if (ticks <= 0) {
            return;
        }

        ticks--;

        if (ticks > 0) {
            data.putInt(
                RedemptionItemHelper
                    .RECHECK_TICKS_TAG,
                ticks
            );
            return;
        }

        data.remove(
            RedemptionItemHelper
                .RECHECK_TICKS_TAG
        );

        RedemptionItemHelper
            .setCachedAccess(
                player,
                RedemptionItemHelper
                    .findRingOfRedemptionNow(
                        player
                    )
            );
    }
}