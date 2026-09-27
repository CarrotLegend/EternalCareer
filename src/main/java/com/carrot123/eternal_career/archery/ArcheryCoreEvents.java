package com.carrot123.eternal_career.archery;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.ArcheryMasterCoreItem;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID)
public final class ArcheryCoreEvents {
    private static final String START_KEY = "EternalCareerArcheryCoreStart";
    private static final String BURST_KEY = "EternalCareerArcheryCoreBurst";
    private static final String ARROW_BONUS_KEY = "EternalCareerArcheryCoreArrowBonus";

    private ArcheryCoreEvents() {
    }

    public static boolean hasMode(Player player, int mode) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .map(handler -> handler.findCurios(ModItems.ARCHERY_MASTER_CORE.get())
                        .stream().anyMatch(result ->
                                ArcheryMasterCoreItem.SLOT.equals(result.slotContext().identifier())
                                        && !result.slotContext().cosmetic()
                                        && ArcheryMasterCoreItem.mode(result.stack()) == mode))
                .orElse(false);
    }

    @SubscribeEvent
    public static void onBowStart(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player player)
                || player.level().isClientSide()
                || !(event.getItem().getItem() instanceof BowItem)) {
            return;
        }
        player.getPersistentData().putLong(START_KEY, player.level().getGameTime());
        player.getPersistentData().putBoolean(BURST_KEY,
                hasMode(player, ArcheryMasterCoreItem.BURST));
    }

    public static void tagShot(Entity arrow, LivingEntity shooter) {
        if (!(shooter instanceof Player player) || shooter.level().isClientSide()
                || !(arrow instanceof AbstractArrow)
                || !hasMode(player, ArcheryMasterCoreItem.BURST)
                || !player.getPersistentData().getBoolean(BURST_KEY)
                || !player.getPersistentData().contains(START_KEY)) {
            return;
        }
        long elapsed = Math.max(0L,
                player.level().getGameTime() - player.getPersistentData().getLong(START_KEY));
        int bonus = (int) Math.min(5L, elapsed / 20L);
        if (bonus > 0) {
            arrow.getPersistentData().putInt(ARROW_BONUS_KEY, bonus);
        }
    }

    @SubscribeEvent
    public static void onArrowHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()
                || !(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)) {
            return;
        }
        int bonus = arrow.getPersistentData().getInt(ARROW_BONUS_KEY);
        if (bonus > 0) {
            event.setAmount(event.getAmount() * (1.0F + Math.min(5, bonus)));
        }
    }
}
