package com.carrot123.eternal_career.lich;

import com.carrot123.eternal_career.EternalCareer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = EternalCareer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LichStageAbilities {
    public static final UUID NOVICE_ATTACK_DAMAGE_UUID = UUID.nameUUIDFromBytes(
            "eternal_career:novice_lich/attack_damage".getBytes(StandardCharsets.UTF_8));
    public static final UUID NOVICE_MAX_HEALTH_UUID = UUID.nameUUIDFromBytes(
            "eternal_career:novice_lich/max_health".getBytes(StandardCharsets.UTF_8));
    private static final UUID INTERMEDIATE_MAX_HEALTH_UUID = UUID.nameUUIDFromBytes(
            "eternal_career:intermediate_lich/max_health".getBytes(StandardCharsets.UTF_8));
    private static final UUID LICH_KING_MAX_HEALTH_UUID = UUID.nameUUIDFromBytes(
            "eternal_career:lich_king/max_health".getBytes(StandardCharsets.UTF_8));
    private static final UUID INTERMEDIATE_FOCUS_DAMAGE_UUID = UUID.nameUUIDFromBytes(
            "eternal_career:intermediate_lich/focus_damage".getBytes(StandardCharsets.UTF_8));
    private static final UUID STAGE_SPELL_POWER_MULTIPLIER_UUID = UUID.nameUUIDFromBytes(
            "eternal_career:lich_stage/spell_power_multiplier".getBytes(StandardCharsets.UTF_8));
    private static final UUID LICH_KING_CAST_DURATION_UUID = UUID.nameUUIDFromBytes(
            "eternal_career:lich_king/cast_duration".getBytes(StandardCharsets.UTF_8));
    private static final UUID LICH_KING_SPELL_COOLDOWN_UUID = UUID.nameUUIDFromBytes(
            "eternal_career:lich_king/spell_cooldown".getBytes(StandardCharsets.UTF_8));
    private static final ResourceLocation FOCUS_DAMAGE_ID =
            new ResourceLocation("until_eternity", "focus_damage");
    private static final ResourceLocation SPELL_POWER_MULTIPLIER_ID =
            new ResourceLocation("goety_revelation", "spell_power_multiplier");
    private static final ResourceLocation CAST_DURATION_ID =
            new ResourceLocation("goety_revelation", "cast_duration");
    private static final ResourceLocation SPELL_COOLDOWN_ID =
            new ResourceLocation("goety_revelation", "spell_cooldown");
    private static final List<MobEffect> FORBIDDEN_EFFECTS = List.of(
            MobEffects.REGENERATION, MobEffects.POISON, MobEffects.BLINDNESS,
            MobEffects.HUNGER, MobEffects.CONFUSION, MobEffects.SATURATION);

    private LichStageAbilities() {
    }

    public static void applyStage(ServerPlayer player) {
        syncStageModifiers(player);
        if (LichUtils.isAtLeastLichStage(player, LichStage.ADVANCED_LICH)
                && player.isUnderWater()) {
            player.setAirSupply(player.getMaxAirSupply());
        }
        if (LichUtils.getLichStage(player) == LichStage.LICH_KING) {
            LichKingUndeadEvents.clearNearbyTargets(player);
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
                || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        syncStageModifiers(player);
        if (!LichUtils.isLich(player)) {
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
            if (!LichUtils.isAtLeastLichStage(player, LichStage.ADVANCED_LICH)) {
                refreshSunFire(player);
            }
        }
    }

    private static void refreshNightVision(ServerPlayer player) {
        MobEffectInstance existing = player.getEffect(MobEffects.NIGHT_VISION);
        if (existing == null || existing.getDuration() <= 400) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 600, 0, true, false));
        }
    }

    private static void syncStageModifiers(ServerPlayer player) {
        LichStage stage = LichUtils.getLichStage(player);
        syncModifier(player.getAttribute(Attributes.ATTACK_DAMAGE), NOVICE_ATTACK_DAMAGE_UUID,
                "eternal_career:novice_lich/attack_damage",
                stage.isAtLeast(LichStage.NOVICE_LICH) ? -0.5D : 0.0D,
                AttributeModifier.Operation.MULTIPLY_TOTAL);

        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        syncModifier(health, NOVICE_MAX_HEALTH_UUID,
                "eternal_career:novice_lich/max_health",
                stage == LichStage.NOVICE_LICH ? -0.5D : 0.0D,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
        syncModifier(health, INTERMEDIATE_MAX_HEALTH_UUID,
                "eternal_career:intermediate_lich/max_health",
                stage == LichStage.INTERMEDIATE_LICH ? -0.25D : 0.0D,
                AttributeModifier.Operation.MULTIPLY_TOTAL);
        syncModifier(health, LICH_KING_MAX_HEALTH_UUID,
                "eternal_career:lich_king/max_health",
                stage == LichStage.LICH_KING ? 0.25D : 0.0D,
                AttributeModifier.Operation.MULTIPLY_TOTAL);

        double focusBonus = switch (stage) {
            case INTERMEDIATE_LICH -> 0.10D;
            case ADVANCED_LICH -> 0.20D;
            case LICH_KING -> 0.50D;
            default -> 0.0D;
        };
        syncRegisteredModifier(player, FOCUS_DAMAGE_ID, INTERMEDIATE_FOCUS_DAMAGE_UUID,
                "eternal_career:lich_stage/focus_damage", focusBonus,
                AttributeModifier.Operation.MULTIPLY_BASE);

        double powerBonus = switch (stage) {
            case ADVANCED_LICH -> 0.5D;
            case LICH_KING -> 2.0D;
            default -> 0.0D;
        };
        syncRegisteredModifier(player, SPELL_POWER_MULTIPLIER_ID,
                STAGE_SPELL_POWER_MULTIPLIER_UUID,
                "eternal_career:lich_stage/spell_power_multiplier", powerBonus,
                AttributeModifier.Operation.ADDITION);

        double kingReduction = stage == LichStage.LICH_KING ? 1.0D : 0.0D;
        syncRegisteredModifier(player, CAST_DURATION_ID, LICH_KING_CAST_DURATION_UUID,
                "eternal_career:lich_king/cast_duration", kingReduction,
                AttributeModifier.Operation.ADDITION);
        syncRegisteredModifier(player, SPELL_COOLDOWN_ID, LICH_KING_SPELL_COOLDOWN_UUID,
                "eternal_career:lich_king/spell_cooldown", kingReduction,
                AttributeModifier.Operation.ADDITION);
    }

    private static void syncRegisteredModifier(ServerPlayer player, ResourceLocation id,
            UUID uuid, String name, double amount, AttributeModifier.Operation operation) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(id);
        if (attribute != null) {
            syncModifier(player.getAttribute(attribute), uuid, name, amount, operation);
        }
    }

    private static void syncModifier(AttributeInstance instance, UUID uuid, String name,
            double amount, AttributeModifier.Operation operation) {
        if (instance == null) {
            return;
        }
        AttributeModifier current = instance.getModifier(uuid);
        if (amount == 0.0D) {
            if (current != null) {
                instance.removeModifier(uuid);
            }
            return;
        }
        if (current != null && current.getOperation() == operation
                && Double.compare(current.getAmount(), amount) == 0) {
            return;
        }
        if (current != null) {
            instance.removeModifier(uuid);
        }
        instance.addTransientModifier(new AttributeModifier(uuid, name, amount, operation));
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
