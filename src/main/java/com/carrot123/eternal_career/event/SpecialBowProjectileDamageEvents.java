package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(
        modid = EternalCareer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class SpecialBowProjectileDamageEvents {

    private static final ResourceLocation PHOENIX_BOW =
            new ResourceLocation(
                    "aether",
                    "phoenix_bow"
            );

    private static final ResourceLocation DRAGON_BOW =
            new ResourceLocation(
                    "enigmaticaddons",
                    "dragon_bow"
            );

    private static final ResourceLocation APOCALYPTIC_LONGBOW =
            new ResourceLocation(
                    "goety_revelation",
                    "bow_of_revelation"
            );

    private static final String APPLIED_TAG =
            "EternalCareerSpecialBowProjectileDamageApplied";

    private SpecialBowProjectileDamageEvents() {
    }

    @SubscribeEvent
    public static void onProjectileJoin(
            EntityJoinLevelEvent event
    ) {
        if (event.getLevel().isClientSide()
                || event.loadedFromDisk()
                || !(event.getEntity() instanceof AbstractArrow arrow)
                || !(arrow.getOwner() instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag data =
                arrow.getPersistentData();

        if (data.getBoolean(APPLIED_TAG)) {
            return;
        }

        ItemStack bow =
                findSourceBow(player);

        if (bow.isEmpty()) {
            return;
        }

        ResourceLocation id =
                ForgeRegistries.ITEMS.getKey(
                        bow.getItem()
                );

        if (id == null) {
            return;
        }

        double bonus;

        if (PHOENIX_BOW.equals(id)) {
            bonus = 140.0D;
        } else if (DRAGON_BOW.equals(id)) {
            bonus = 190.0D;
        } else if (APOCALYPTIC_LONGBOW.equals(id)) {
            bonus = 288.0D;
        } else {
            return;
        }

        arrow.setBaseDamage(
                arrow.getBaseDamage() + bonus
        );

        data.putBoolean(
                APPLIED_TAG,
                true
        );
    }

    private static ItemStack findSourceBow(
            ServerPlayer player
    ) {
        ItemStack using =
                player.getUseItem();

        if (isTargetBow(using)) {
            return using;
        }

        ItemStack mainHand =
                player.getMainHandItem();

        if (isTargetBow(mainHand)) {
            return mainHand;
        }

        ItemStack offHand =
                player.getOffhandItem();

        if (isTargetBow(offHand)) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }

    private static boolean isTargetBow(
            ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return false;
        }

        ResourceLocation id =
                ForgeRegistries.ITEMS.getKey(
                        stack.getItem()
                );

        return PHOENIX_BOW.equals(id)
                || DRAGON_BOW.equals(id)
                || APOCALYPTIC_LONGBOW.equals(id);
    }
}