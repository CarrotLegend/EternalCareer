package com.carrot123.eternal_career.event;

import com.carrot123.eternal_career.EternalCareer;
import com.carrot123.eternal_career.item.MaterialArrowItem;
import com.carrot123.eternal_career.registry.ModItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
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

    private static final ResourceLocation DRAGON_BREATH_ARROW =
            new ResourceLocation(
                    "enigmaticaddons",
                    "dragon_breath_arrow"
            );

    private static final ResourceLocation DEATH_ARROW =
            new ResourceLocation(
                    "goety",
                    "death_arrow"
            );

    private static final String APPLIED_TAG =
            "EternalCareerSpecialBowProjectileDamageApplied";

    private static final String MATERIAL_APPLIED_TAG =
            "EternalCareerSpecialBowMaterialDamageApplied";

    private static final String DRAGON_MATERIAL_MULTIPLIER_KEY =
            "EternalCareerDragonBowMaterialMultiplier";

    private static final String DRAGON_MATERIAL_TICK_KEY =
            "EternalCareerDragonBowMaterialTick";

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

    @SubscribeEvent(
            priority = EventPriority.LOWEST
    )
    public static void onSpecialMaterialProjectileJoin(
            EntityJoinLevelEvent event
    ) {
        if (event.getLevel().isClientSide()
                || event.loadedFromDisk()
                || !(event.getEntity() instanceof AbstractArrow arrow)
                || !(arrow.getOwner() instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag arrowData =
                arrow.getPersistentData();

        if (arrowData.getBoolean(
                MATERIAL_APPLIED_TAG
        )) {
            return;
        }

        ItemStack bow =
                findSourceBow(player);

        if (bow.isEmpty()) {
            return;
        }

        ResourceLocation bowId =
                ForgeRegistries.ITEMS.getKey(
                        bow.getItem()
                );

        ResourceLocation projectileId =
                ForgeRegistries.ENTITY_TYPES.getKey(
                        arrow.getType()
                );

        if (bowId == null
                || projectileId == null) {
            return;
        }

        double multiplier = 1.0D;

        if (DRAGON_BOW.equals(bowId)
                && DRAGON_BREATH_ARROW.equals(
                projectileId
        )) {
            multiplier =
                    getPendingDragonBowMultiplier(
                            player
                    );
        } else if (APOCALYPTIC_LONGBOW.equals(
                bowId
        )
                && DEATH_ARROW.equals(
                projectileId
        )) {
            ItemStack ammunition =
                    player.getProjectile(
                            bow
                    );

            multiplier =
                    materialMultiplier(
                            ammunition
                    );
        } else {
            return;
        }

        if (!Double.isFinite(multiplier)
                || multiplier <= 1.0D) {
            return;
        }

        double damage =
                arrow.getBaseDamage();

        if (!Double.isFinite(damage)
                || damage < 0.0D) {
            return;
        }

        double modified =
                damage * multiplier;

        if (!Double.isFinite(modified)) {
            return;
        }

        arrow.setBaseDamage(
                Math.min(
                        modified,
                        Double.MAX_VALUE
                )
        );

        arrowData.putBoolean(
                MATERIAL_APPLIED_TAG,
                true
        );
    }

    public static void prepareDragonBowMaterialArrow(
            Player player,
            ItemStack bow
    ) {
        if (player == null
                || player.level().isClientSide()
                || bow.isEmpty()) {
            return;
        }

        CompoundTag data =
                player.getPersistentData();

        data.remove(
                DRAGON_MATERIAL_MULTIPLIER_KEY
        );

        data.remove(
                DRAGON_MATERIAL_TICK_KEY
        );

        ItemStack ammunition =
                findMaterialArrow(
                        player
                );

        if (ammunition.isEmpty()) {
            return;
        }

        double multiplier =
                materialMultiplier(
                        ammunition
                );

        if (multiplier <= 1.0D) {
            return;
        }

        data.putDouble(
                DRAGON_MATERIAL_MULTIPLIER_KEY,
                multiplier
        );

        data.putLong(
                DRAGON_MATERIAL_TICK_KEY,
                player.level().getGameTime()
        );

        if (player.getAbilities().instabuild) {
            return;
        }

        boolean infinite = false;

        if (ammunition.getItem()
                instanceof MaterialArrowItem materialArrow) {
            infinite =
                    materialArrow.isInfinite(
                            ammunition,
                            bow,
                            player
                    );
        }

        if (!infinite) {
            ammunition.shrink(1);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(
            TickEvent.PlayerTickEvent event
    ) {
        if (event.phase
                != TickEvent.Phase.END
                || event.player.level().isClientSide()) {
            return;
        }

        Player player =
                event.player;

        CompoundTag data =
                player.getPersistentData();

        if (!data.contains(
                DRAGON_MATERIAL_TICK_KEY
        )) {
            return;
        }

        long tick =
                data.getLong(
                        DRAGON_MATERIAL_TICK_KEY
                );

        if (player.level().getGameTime()
                <= tick) {
            return;
        }

        data.remove(
                DRAGON_MATERIAL_MULTIPLIER_KEY
        );

        data.remove(
                DRAGON_MATERIAL_TICK_KEY
        );
    }

    private static double getPendingDragonBowMultiplier(
            Player player
    ) {
        CompoundTag data =
                player.getPersistentData();

        if (!data.contains(
                DRAGON_MATERIAL_MULTIPLIER_KEY
        )
                || !data.contains(
                DRAGON_MATERIAL_TICK_KEY
        )) {
            return 1.0D;
        }

        long tick =
                data.getLong(
                        DRAGON_MATERIAL_TICK_KEY
                );

        if (tick
                != player.level().getGameTime()) {
            return 1.0D;
        }

        return data.getDouble(
                DRAGON_MATERIAL_MULTIPLIER_KEY
        );
    }

    private static ItemStack findMaterialArrow(
            Player player
    ) {
        ItemStack offHand =
                player.getOffhandItem();

        if (materialMultiplier(
                offHand
        ) > 1.0D) {
            return offHand;
        }

        ItemStack mainHand =
                player.getMainHandItem();

        if (materialMultiplier(
                mainHand
        ) > 1.0D) {
            return mainHand;
        }

        for (int slot = 0;
             slot < player.getInventory()
                     .getContainerSize();
             slot++) {

            ItemStack stack =
                    player.getInventory()
                            .getItem(slot);

            if (materialMultiplier(
                    stack
            ) > 1.0D) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    private static double materialMultiplier(
            ItemStack stack
    ) {
        if (stack == null
                || stack.isEmpty()) {
            return 1.0D;
        }

        if (stack.is(
                ModItems.IRON_ARROW.get()
        )) {
            return MaterialArrowItem.Material
                    .IRON
                    .multiplier();
        }

        if (stack.is(
                ModItems.DIAMOND_ARROW.get()
        )) {
            return MaterialArrowItem.Material
                    .DIAMOND
                    .multiplier();
        }

        if (stack.is(
                ModItems.NETHERITE_ARROW.get()
        )) {
            return MaterialArrowItem.Material
                    .NETHERITE
                    .multiplier();
        }

        return 1.0D;
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