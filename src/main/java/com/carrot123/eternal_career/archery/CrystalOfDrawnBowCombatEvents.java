package com.carrot123.eternal_career.archery;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.curio.CurioEquipmentHelper;
import com.carrot123.eternal_career.fletching.BowModificationHelper;
import com.carrot123.eternal_career.util.StableAttributeModifiers;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CrystalOfDrawnBowCombatEvents {
    private static final String BOW_SHOT = "EternalCareerCrystalOfDrawnBowBowShot";
    private static final String ARMOR_BREAK_KEY =
            "eternal_career:crystal_of_drawn_bow/permanent_armor_break";
    private static final UUID ARMOR_BREAK_ID = StableAttributeModifiers.id(ARMOR_BREAK_KEY);
    private static final Map<UUID, ShotContext> RECENT_BOW_SHOTS = new HashMap<>();

    private CrystalOfDrawnBowCombatEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onArrowLoose(ArrowLooseEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && event.getLevel() instanceof ServerLevel level
                && BowModificationHelper.isBow(event.getBow())
                && !(event.getBow().getItem() instanceof CrossbowItem)) {
            RECENT_BOW_SHOTS.put(player.getUUID(),
                    new ShotContext(level.getGameTime(), level.dimension()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onArrowJoin(EntityJoinLevelEvent event) {
        if (event.loadedFromDisk() || !(event.getLevel() instanceof ServerLevel level)
                || !(event.getEntity() instanceof AbstractArrow arrow)
                || arrow instanceof ThrownTrident
                || !(arrow.getOwner() instanceof ServerPlayer player)) {
            return;
        }

        ShotContext shot = RECENT_BOW_SHOTS.get(player.getUUID());
        if (shot != null && shot.tick() == level.getGameTime()
                && shot.dimension().equals(level.dimension())) {
            arrow.getPersistentData().putBoolean(BOW_SHOT, true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
            ShotContext shot = RECENT_BOW_SHOTS.get(player.getUUID());
            if (shot != null && (shot.tick() < player.serverLevel().getGameTime()
                    || !shot.dimension().equals(player.serverLevel().dimension()))) {
                RECENT_BOW_SHOTS.remove(player.getUUID());
            }
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        RECENT_BOW_SHOTS.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.getEntity().level().isClientSide
                || !Float.isFinite(event.getAmount()) || event.getAmount() <= 0.0F
                || !(event.getSource().getDirectEntity() instanceof AbstractArrow arrow)
                || arrow instanceof ThrownTrident
                || !(arrow.getOwner() instanceof ServerPlayer player)
                || event.getSource().getEntity() != player
                || event.getEntity() == player
                || !arrow.getPersistentData().getBoolean(BOW_SHOT)
                || !CurioEquipmentHelper.hasCrystalOfDrawnBow(player)) {
            return;
        }

        AttributeInstance armor = event.getEntity().getAttribute(Attributes.ARMOR);
        if (armor == null || armor.getModifier(ARMOR_BREAK_ID) != null) {
            return;
        }

        armor.addPermanentModifier(StableAttributeModifiers.create(
                ARMOR_BREAK_ID, ARMOR_BREAK_KEY, -0.20D,
                AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    private record ShotContext(long tick, ResourceKey<Level> dimension) {
    }
}
