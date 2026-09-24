package com.carrot123.eternal_career.lich;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LichKingUndeadEvents {
    private static final double TARGET_CLEAR_RADIUS = 64.0D;

    private LichKingUndeadEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        if (event.getEntity() instanceof Mob mob
                && event.getNewTarget() instanceof ServerPlayer player
                && LichUtils.getLichStage(player) == LichStage.LICH_KING
                && isAffectedUndead(mob)) {
            event.setNewTarget(null);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END
                && event.player instanceof ServerPlayer player
                && player.tickCount % 20 == 0
                && LichUtils.getLichStage(player) == LichStage.LICH_KING) {
            clearNearbyTargets(player);
        }
    }

    static void clearNearbyTargets(ServerPlayer player) {
        player.serverLevel().getEntitiesOfClass(Mob.class,
                player.getBoundingBox().inflate(TARGET_CLEAR_RADIUS),
                mob -> mob.getTarget() == player && isAffectedUndead(mob))
                .forEach(mob -> mob.setTarget(null));
    }

    private static boolean isAffectedUndead(Mob mob) {
        return mob.getMobType() == MobType.UNDEAD && mob.getMaxHealth() <= 200.0F;
    }
}
