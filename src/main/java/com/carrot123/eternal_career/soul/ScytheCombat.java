package com.carrot123.eternal_career.soul;

import com.carrot123.eternal_career.registry.ModTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;

public final class ScytheCombat {
    private ScytheCombat() {}
    public static ServerPlayer directAttacker(DamageSource source) {
        if (source.getEntity() instanceof ServerPlayer player
                && source.getDirectEntity() == player && source.is(DamageTypes.PLAYER_ATTACK)
                && player.getMainHandItem().is(ModTags.Items.SCYTHES)) return player;
        return null;
    }
    public static float scaledDamage(float damage, double bonus) {
        return (float) Math.min(Float.MAX_VALUE, Math.max(0, damage * (1.0D + bonus)));
    }
    public static int reward(float maxHealth) {
        if (Float.isNaN(maxHealth)) return 1;
        return (int) Math.max(1, Math.min(1000, Math.floor(maxHealth / 100.0D)));
    }
}
