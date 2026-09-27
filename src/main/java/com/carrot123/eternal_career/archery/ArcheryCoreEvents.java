package com.carrot123.eternal_career.archery;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.ArcheryMasterCoreItem;
import com.carrot123.eternal_career.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID)
public final class ArcheryCoreEvents {
    private static final String PENDING_BONUS_KEY =
            "EternalCareerArcheryCorePendingBonus";

    private static final String PENDING_TICK_KEY =
            "EternalCareerArcheryCorePendingTick";

    private static final String ARROW_BONUS_KEY =
            "EternalCareerArcheryCoreArrowBonus";

    private ArcheryCoreEvents() {
    }

    public static boolean hasMode(Player player, int mode) {
        return CuriosApi.getCuriosInventory(player).resolve()
                .map(handler ->
                        handler.findCurios(ModItems.ARCHERY_MASTER_CORE.get())
                                .stream()
                                .anyMatch(result ->
                                        ArcheryMasterCoreItem.SLOT.equals(
                                                result.slotContext().identifier()
                                        )
                                                && !result.slotContext().cosmetic()
                                                && ArcheryMasterCoreItem.mode(
                                                        result.stack()
                                                ) == mode
                                )
                )
                .orElse(false);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onArrowLoose(ArrowLooseEvent event) {
        Player player = event.getEntity();

        if (event.getLevel().isClientSide()) {
            return;
        }

        CompoundTag data = player.getPersistentData();

        data.remove(PENDING_BONUS_KEY);
        data.remove(PENDING_TICK_KEY);

        if (!hasMode(player, ArcheryMasterCoreItem.BURST)) {
            return;
        }

        int chargeTicks = Math.max(0, event.getCharge());

        int bonus = Math.min(5, chargeTicks / 20);

        if (bonus <= 0) {
            return;
        }

        data.putInt(PENDING_BONUS_KEY, bonus);
        data.putLong(
                PENDING_TICK_KEY,
                player.level().getGameTime()
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onArrowJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()
                || event.loadedFromDisk()
                || !(event.getEntity() instanceof AbstractArrow arrow)) {
            return;
        }

        Entity owner = arrow.getOwner();

        if (!(owner instanceof Player player)) {
            return;
        }

        CompoundTag data = player.getPersistentData();

        if (!data.contains(PENDING_BONUS_KEY)
                || !data.contains(PENDING_TICK_KEY)) {
            return;
        }

        long shotTick = data.getLong(PENDING_TICK_KEY);
        long currentTick = event.getLevel().getGameTime();

        if (currentTick < shotTick
                || currentTick - shotTick > 1L) {
            return;
        }

        int bonus = Math.max(
                0,
                Math.min(
                        5,
                        data.getInt(PENDING_BONUS_KEY)
                )
        );

        if (bonus <= 0) {
            return;
        }

        arrow.getPersistentData().putInt(
                ARROW_BONUS_KEY,
                bonus
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onArrowHurt(LivingHurtEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }

        if (!(event.getSource().getDirectEntity()
                instanceof AbstractArrow arrow)) {
            return;
        }

        int bonus = Math.max(
                0,
                Math.min(
                        5,
                        arrow.getPersistentData().getInt(
                                ARROW_BONUS_KEY
                        )
                )
        );

        if (bonus <= 0) {
            return;
        }

        float amount = event.getAmount();

        if (amount <= 0.0F || !Float.isFinite(amount)) {
            return;
        }

        double multiplier = 1.0D + bonus;
        double modified = amount * multiplier;

        if (!Double.isFinite(modified)) {
            return;
        }

        event.setAmount(
                modified >= Float.MAX_VALUE
                        ? Float.MAX_VALUE
                        : (float) modified
        );
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END
                || event.player.level().isClientSide()) {
            return;
        }

        CompoundTag data = event.player.getPersistentData();

        if (!data.contains(PENDING_TICK_KEY)) {
            return;
        }

        long shotTick = data.getLong(PENDING_TICK_KEY);
        long currentTick = event.player.level().getGameTime();

        if (currentTick > shotTick + 1L) {
            data.remove(PENDING_BONUS_KEY);
            data.remove(PENDING_TICK_KEY);
        }
    }
}