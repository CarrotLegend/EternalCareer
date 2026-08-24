package com.carrot123.eternal_career.compat.redemption;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.compat.curios.CurioSafeLookup;

import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraftforge.registries.ForgeRegistries;

public final class RedemptionItemHelper {

    public static final String RING_SLOT =
            "ring";

    public static final ResourceLocation
            RING_OF_REDEMPTION_ID =
            new ResourceLocation(
                    "enigmaticaddons",
                    "bless_ring"
            );

    public static final TagKey<Item>
            REDEMPTION_ITEMS =
            TagKey.create(
                    Registries.ITEM,
                    new ResourceLocation(
                            EternalCareer.MOD_ID,
                            "redemption_items"
                    )
            );

    public static final String ACCESS_CACHE_TAG =
            "eternal_career:redemption_access_cached";

    public static final String RECHECK_TICKS_TAG =
            "eternal_career:redemption_recheck_ticks";

    public static final String
            ENIGMATIC_ADDONS_BLESS_SPAWN =
            "BlessNextSpawn";

    private RedemptionItemHelper() {
    }

    public static boolean isRedemptionItem(
            ItemStack stack
    ) {
        return stack != null
                && !stack.isEmpty()
                && stack.is(REDEMPTION_ITEMS);
    }

    public static boolean hasRingOfRedemption(
            Player player
    ) {
        if (player == null) {
            return false;
        }

        if (player.getPersistentData()
                .getBoolean(
                        ACCESS_CACHE_TAG
                )) {
            return true;
        }

        if (player.getPersistentData()
                .getBoolean(
                        ENIGMATIC_ADDONS_BLESS_SPAWN
                )) {
            return true;
        }

        return findRingOfRedemptionNow(
                player
        );
    }

    public static boolean findRingOfRedemptionNow(
            Player player
    ) {
        Item ring =
                ForgeRegistries.ITEMS
                        .getValue(
                                RING_OF_REDEMPTION_ID
                        );

        if (ring == null
                || ring == Items.AIR) {
            return false;
        }

        return CurioSafeLookup.hasEquipped(
                player,
                ring
        );
    }

    public static void setCachedAccess(
            Player player,
            boolean value
    ) {
        player.getPersistentData()
                .putBoolean(
                        ACCESS_CACHE_TAG,
                        value
                );
    }

    public static boolean hasCachedAccess(
            Player player
    ) {
        return player.getPersistentData()
                .contains(
                        ACCESS_CACHE_TAG,
                        Tag.TAG_BYTE
                )
                && player.getPersistentData()
                .getBoolean(
                        ACCESS_CACHE_TAG
                );
    }

    public static void scheduleRecheck(
            Player player,
            int ticks
    ) {
        player.getPersistentData()
                .putInt(
                        RECHECK_TICKS_TAG,
                        Math.max(1, ticks)
                );
    }

    public static boolean canUseRedemptionItem(
            Player player,
            ItemStack stack
    ) {
        return !isRedemptionItem(stack)
                || hasRingOfRedemption(player);
    }
}