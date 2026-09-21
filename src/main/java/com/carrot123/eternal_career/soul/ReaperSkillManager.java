package com.carrot123.eternal_career.soul;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.carrot123.eternal_career.career.capability.soul.SoulCapability;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class ReaperSkillManager {
    private static final int SOUL_COST_PER_SECOND = 10;
    private static final int TICKS_PER_COST = 20;

    private static final UUID ATTACK_SPEED_MODIFIER_UUID =
            UUID.fromString("64c02c8b-56d1-46b9-b40c-7967931f38af");

    private static final AttributeModifier ATTACK_SPEED_MODIFIER =
            new AttributeModifier(
                    ATTACK_SPEED_MODIFIER_UUID,
                    "eternal_career_reaper_skill_attack_speed",
                    0.10D,
                    AttributeModifier.Operation.MULTIPLY_TOTAL
            );

    private static final Set<UUID> ACTIVE = new HashSet<>();
    private static final Map<UUID, Integer> TICKS = new HashMap<>();

    private ReaperSkillManager() {
    }

    public static void toggle(ServerPlayer player) {
        UUID id = player.getUUID();

        if (ACTIVE.contains(id)) {
            stop(player);
            return;
        }

        start(player);
    }

    public static void start(ServerPlayer player) {
        UUID id = player.getUUID();

        if (ACTIVE.contains(id)) {
            return;
        }

        if (!player.isAlive()) {
            return;
        }

        if (SoulSetManager.findActiveSoulSet(player).isEmpty()) {
            return;
        }

        var soulOptional = player.getCapability(SoulCapability.SOUL).resolve();
        if (soulOptional.isEmpty()) {
            return;
        }

        var soul = soulOptional.get();

        if (!soul.consumeSoul(SOUL_COST_PER_SECOND)) {
            return;
        }

        ACTIVE.add(id);
        TICKS.put(id, 0);

        applyAttackSpeed(player);

        player.serverLevel().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ENDER_DRAGON_GROWL,
                SoundSource.PLAYERS,
                2.0F,
                1.0F
        );
    }

    public static void stop(ServerPlayer player) {
        UUID id = player.getUUID();

        ACTIVE.remove(id);
        TICKS.remove(id);

        removeAttackSpeed(player);
    }

    public static void forget(ServerPlayer player) {
        stop(player);
    }

    public static void forget(UUID playerId) {
        ACTIVE.remove(playerId);
        TICKS.remove(playerId);
    }

    public static boolean isActive(ServerPlayer player) {
        if (!ACTIVE.contains(player.getUUID())) {
            return false;
        }

        if (!player.isAlive()) {
            return false;
        }

        return SoulSetManager.findActiveSoulSet(player).isPresent();
    }

    public static void validate(ServerPlayer player) {
        if (!ACTIVE.contains(player.getUUID())) {
            return;
        }

        if (!player.isAlive() || SoulSetManager.findActiveSoulSet(player).isEmpty()) {
            stop(player);
            return;
        }

        applyAttackSpeed(player);
    }

    public static void tick(MinecraftServer server) {
        for (UUID id : Set.copyOf(ACTIVE)) {
            ServerPlayer player = server.getPlayerList().getPlayer(id);

            if (player == null) {
                ACTIVE.remove(id);
                TICKS.remove(id);
                continue;
            }

            if (!player.isAlive() || SoulSetManager.findActiveSoulSet(player).isEmpty()) {
                stop(player);
                continue;
            }

            applyAttackSpeed(player);

            int ticks = TICKS.getOrDefault(id, 0) + 1;

            if (ticks < TICKS_PER_COST) {
                TICKS.put(id, ticks);
                continue;
            }

            TICKS.put(id, 0);

            var soulOptional = player.getCapability(SoulCapability.SOUL).resolve();

            if (soulOptional.isEmpty()) {
                stop(player);
                continue;
            }

            if (!soulOptional.get().consumeSoul(SOUL_COST_PER_SECOND)) {
                stop(player);
            }
        }
    }

    public static float boostScytheDamage(float damage) {
        if (damage <= 0.0F) {
            return damage;
        }

        double result = damage * 3.0D;

        if (result >= Float.MAX_VALUE) {
            return Float.MAX_VALUE;
        }

        return (float) result;
    }

    public static int boostSoulReward(int amount) {
        if (amount <= 0) {
            return amount;
        }

        long result = (long) amount * 2L;

        return result >= Integer.MAX_VALUE
                ? Integer.MAX_VALUE
                : (int) result;
    }

    public static void clear(MinecraftServer server) {
        for (UUID id : Set.copyOf(ACTIVE)) {
            ServerPlayer player = server.getPlayerList().getPlayer(id);

            if (player != null) {
                removeAttackSpeed(player);
            }
        }

        ACTIVE.clear();
        TICKS.clear();
    }

    private static void applyAttackSpeed(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(Attributes.ATTACK_SPEED);

        if (attribute == null) {
            return;
        }

        if (attribute.getModifier(ATTACK_SPEED_MODIFIER_UUID) == null) {
            attribute.addTransientModifier(ATTACK_SPEED_MODIFIER);
        }
    }

    private static void removeAttackSpeed(ServerPlayer player) {
        AttributeInstance attribute = player.getAttribute(Attributes.ATTACK_SPEED);

        if (attribute == null) {
            return;
        }

        attribute.removeModifier(ATTACK_SPEED_MODIFIER_UUID);
    }
}