package com.carrot123.eternal_career.lich;

import com.carrot123.eternal_career.EternalCareer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LichStageAbilities {
    public static final UUID NOVICE_ATTACK_DAMAGE_UUID = UUID.nameUUIDFromBytes(
            "eternal_career:novice_lich/attack_damage".getBytes(StandardCharsets.UTF_8));
    public static final UUID NOVICE_MAX_HEALTH_UUID = UUID.nameUUIDFromBytes(
            "eternal_career:novice_lich/max_health".getBytes(StandardCharsets.UTF_8));
    private static final AttributeModifier NOVICE_ATTACK_DAMAGE = new AttributeModifier(
            NOVICE_ATTACK_DAMAGE_UUID, "eternal_career:novice_lich/attack_damage", -0.5D,
            AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final AttributeModifier NOVICE_MAX_HEALTH = new AttributeModifier(
            NOVICE_MAX_HEALTH_UUID, "eternal_career:novice_lich/max_health", -0.5D,
            AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final List<MobEffect> FORBIDDEN_EFFECTS = List.of(
            MobEffects.REGENERATION, MobEffects.POISON, MobEffects.BLINDNESS,
            MobEffects.HUNGER, MobEffects.CONFUSION, MobEffects.SATURATION);

    private LichStageAbilities() {
    }

    public static void applyStage(ServerPlayer player) {
        AttributeInstance attack = player.getAttribute(Attributes.ATTACK_DAMAGE);
        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (attack != null) {
            attack.removeModifier(NOVICE_ATTACK_DAMAGE_UUID);
        }
        if (health != null) {
            health.removeModifier(NOVICE_MAX_HEALTH_UUID);
        }
        if (LichUtils.isLich(player)) {
            if (attack != null) {
                attack.addTransientModifier(NOVICE_ATTACK_DAMAGE);
            }
            if (health != null) {
                health.addTransientModifier(NOVICE_MAX_HEALTH);
            }
        }
        if (LichUtils.isLich(player)) {
            for (MobEffect effect : FORBIDDEN_EFFECTS) {
                player.removeEffect(effect);
            }
            player.getFoodData().setFoodLevel(17);
            refreshNightVision(player);
        }
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity() instanceof net.minecraft.world.entity.player.Player player
                && LichUtils.isLich(player)
                && FORBIDDEN_EFFECTS.contains(event.getEffectInstance().getEffect())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END
                || !(event.player instanceof ServerPlayer player)
                || !LichUtils.isLich(player)) {
            return;
        }
        if (player.getFoodData().getFoodLevel() != 17) {
            player.getFoodData().setFoodLevel(17);
        }
        if (!player.hasEffect(MobEffects.NIGHT_VISION)) {
            refreshNightVision(player);
        }
        if (player.tickCount % 20 == 0) {
            refreshNightVision(player);
            refreshSunFire(player);
        }
    }

    private static void refreshNightVision(ServerPlayer player) {
        MobEffectInstance existing = player.getEffect(MobEffects.NIGHT_VISION);
        if (existing == null || existing.getDuration() <= 400) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0, true, false));
        }
    }

    private static void refreshSunFire(ServerPlayer player) {
        BlockPos eyes = BlockPos.containing(player.getX(), player.getEyeY(), player.getZ());
        if (player.level().isDay()
                && player.getLightLevelDependentMagicValue() > 0.5F
                && player.level().canSeeSky(eyes)
                && !player.isInWaterRainOrBubble()
                && player.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                && !player.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            player.setSecondsOnFire(2);
        }
    }
}
