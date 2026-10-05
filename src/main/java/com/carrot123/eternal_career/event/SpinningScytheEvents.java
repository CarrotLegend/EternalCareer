package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import com.carrot123.eternal_career.entity.SpinningScytheEntity;
import com.carrot123.eternal_career.registry.ModItems;
import com.carrot123.eternal_career.registry.ModTags;
import com.carrot123.eternal_career.soul.SpinningScytheFlightData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class SpinningScytheEvents {

    private SpinningScytheEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(
            PlayerInteractEvent.RightClickItem event
    ) {
        tryThrow(
                event.getEntity(),
                event.getHand(),
                event.getItemStack(),
                event
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(
            PlayerInteractEvent.RightClickBlock event
    ) {
        tryThrow(
                event.getEntity(),
                event.getHand(),
                event.getItemStack(),
                event
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(
            TickEvent.PlayerTickEvent event
    ) {
        if (event.phase
                != TickEvent.Phase.END) {
            return;
        }

        if (!(event.player
                instanceof ServerPlayer player)) {
            return;
        }

        SpinningScytheFlightData
                .recoverIfExpired(
                        player
                );
    }

    @SubscribeEvent
    public static void onClone(
            PlayerEvent.Clone event
    ) {
        if (!(event.getOriginal()
                instanceof ServerPlayer original)
                || !(event.getEntity()
                instanceof ServerPlayer clone)) {
            return;
        }

        SpinningScytheFlightData.copy(
                original,
                clone
        );

        SpinningScytheFlightData
                .recoverImmediately(
                        clone
                );
    }

    @SubscribeEvent
    public static void onLogout(
            PlayerEvent.PlayerLoggedOutEvent event
    ) {
        if (!(event.getEntity()
                instanceof ServerPlayer player)) {
            return;
        }

        SpinningScytheFlightData
                .recoverImmediately(
                        player
                );
    }

    private static void tryThrow(
            Player player,
            InteractionHand hand,
            ItemStack stack,
            PlayerInteractEvent event
    ) {
        if (hand
                != InteractionHand.MAIN_HAND) {
            return;
        }

        if (stack.isEmpty()
                || !stack.is(ModTags.Items.SCYTHES)) {
            return;
        }

        if (!CurioEquipmentHelper
                .hasEquippedCurio(
                        player,
                        ModItems.SPINNING_GLOVES.get(),
                        SpinningGlovesItem.HANDS_SLOT
                )) {
            return;
        }

        event.setCanceled(
                true
        );

        event.setCancellationResult(
                InteractionResult.sidedSuccess(
                        player.level().isClientSide
                )
        );

        if (!(player
                instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (SpinningScytheFlightData
                .hasActiveFlight(
                        serverPlayer
                )) {
            return;
        }

        int slot =
                serverPlayer.getInventory()
                        .selected;

        ItemStack original =
                stack.copy();

        UUID flightId =
                SpinningScytheFlightData.begin(
                        serverPlayer,
                        original,
                        slot
                );

        serverPlayer.getInventory()
                .setItem(
                        slot,
                        ItemStack.EMPTY
                );

        SpinningScytheEntity projectile =
                new SpinningScytheEntity(
                        serverPlayer.level(),
                        serverPlayer
                );

        projectile.initialize(
                serverPlayer,
                original,
                flightId
        );

        projectile.setPos(
                serverPlayer.getX(),
                serverPlayer.getEyeY()
                        - 0.25D,
                serverPlayer.getZ()
        );

        projectile.shootFromRotation(
                serverPlayer,
                serverPlayer.getXRot(),
                serverPlayer.getYRot(),
                0.0F,
                1.75F,
                0.0F
        );

        if (!serverPlayer.level()
                .addFreshEntity(projectile)) {
            SpinningScytheFlightData
                    .recoverImmediately(
                            serverPlayer
                    );

            return;
        }

        serverPlayer.swing(
                InteractionHand.MAIN_HAND,
                true
        );

        serverPlayer.level().playSound(
                null,
                serverPlayer.getX(),
                serverPlayer.getY(),
                serverPlayer.getZ(),
                SoundEvents.TRIDENT_THROW,
                SoundSource.PLAYERS,
                1.0F,
                1.15F
        );

        serverPlayer.containerMenu
                .broadcastChanges();
    }
}