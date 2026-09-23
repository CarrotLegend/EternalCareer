package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import com.carrot123.eternal_career.loot.ChefBadgeDropManager;
import com.carrot123.eternal_career.loot.ChefBadgeDropRule;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class ChefBadgeDropEvents {

    private static final int MAX_OWNER_DEPTH = 8;

    private ChefBadgeDropEvents() {
    }

    @SubscribeEvent
    public static void onAddReloadListener(
            AddReloadListenerEvent event
    ) {
        event.addListener(
                ChefBadgeDropManager.INSTANCE
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDrops(
            LivingDropsEvent event
    ) {
        LivingEntity target = event.getEntity();

        if (target.level().isClientSide) {
            return;
        }

        Player player = findKillingPlayer(event);

        if (player == null) {
            return;
        }

        if (!CurioEquipmentHelper.hasHeadChefSheath(player)) {
            return;
        }

        for (ChefBadgeDropRule rule :
                ChefBadgeDropManager.INSTANCE.getRules()) {

            if (!rule.matches(target)) {
                continue;
            }

            if (target.getRandom().nextFloat()
                    >= rule.chance()) {
                continue;
            }

            ItemStack stack =
                    new ItemStack(
                            rule.badge(),
                            rule.count()
                    );

            ItemEntity drop =
                    new ItemEntity(
                            target.level(),
                            target.getX(),
                            target.getY(),
                            target.getZ(),
                            stack
                    );

            event.getDrops().add(drop);
        }
    }

    private static Player findKillingPlayer(
            LivingDropsEvent event
    ) {
        Player player =
                findPlayer(
                        event.getSource().getEntity(),
                        0
                );

        if (player != null) {
            return player;
        }

        player =
                findPlayer(
                        event.getSource().getDirectEntity(),
                        0
                );

        if (player != null) {
            return player;
        }

        LivingEntity killCredit =
                event.getEntity().getKillCredit();

        if (killCredit instanceof Player creditedPlayer) {
            return creditedPlayer;
        }

        return findPlayer(
                killCredit,
                0
        );
    }

    private static Player findPlayer(
            Entity entity,
            int depth
    ) {
        if (entity == null
                || depth >= MAX_OWNER_DEPTH) {
            return null;
        }

        if (entity instanceof Player player) {
            return player;
        }

        if (entity instanceof TraceableEntity traceable) {
            Entity owner =
                    traceable.getOwner();

            if (owner != null
                    && owner != entity) {

                Player player =
                        findPlayer(
                                owner,
                                depth + 1
                        );

                if (player != null) {
                    return player;
                }
            }
        }

        if (entity instanceof OwnableEntity ownable) {
            Entity owner =
                    ownable.getOwner();

            if (owner != null
                    && owner != entity) {

                Player player =
                        findPlayer(
                                owner,
                                depth + 1
                        );

                if (player != null) {
                    return player;
                }
            }
        }

        return null;
    }
}