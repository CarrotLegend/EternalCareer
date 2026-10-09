package com.carrot123.eternal_career.career.api;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;

public final class RangeDamageHelper {
    private RangeDamageHelper() {}
    public static boolean isRanged(DamageSource source) { return source.is(DamageTypeTags.IS_PROJECTILE) || source.getDirectEntity() != source.getEntity(); }
    public static boolean isNonRanged(DamageSource source) { return !isRanged(source); }
}
